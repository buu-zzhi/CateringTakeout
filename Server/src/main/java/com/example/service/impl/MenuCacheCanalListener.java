package com.example.service.impl;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.example.entity.Dish;
import com.example.entity.Setmeal;
import com.example.mapper.DishMapper;
import com.example.mapper.SetmealMapper;
import com.example.properties.CacheConsistencyProperties;
import com.example.service.MenuCacheInvalidationService;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.cache.consistency.canal", name = "enabled", havingValue = "true")
public class MenuCacheCanalListener implements ApplicationRunner, DisposableBean {
    @Autowired
    private CacheConsistencyProperties cacheConsistencyProperties;
    @Autowired
    private MenuCacheInvalidationService menuCacheInvalidationService;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "menu-cache-canal-listener");
        thread.setDaemon(true);
        return thread;
    });

    private volatile boolean running = true;
    private volatile CanalConnector connector;

    @Override
    public void run(ApplicationArguments args) {
        executorService.submit(this::listenLoop);
    }

    @Override
    public void destroy() {
        running = false;
        disconnectQuietly();
        executorService.shutdownNow();
    }

    private void listenLoop() {
        CacheConsistencyProperties.Canal canalProperties = cacheConsistencyProperties.getCanal();
        while (running) {
            try {
                connector = createConnector(canalProperties);
                connector.connect();
                connector.subscribe(canalProperties.getFilter());
                connector.rollback();
                log.info("Canal 菜单缓存监听已启动, destination={}, filter={}",
                        canalProperties.getDestination(), canalProperties.getFilter());

                while (running) {
                    Message message = connector.getWithoutAck(canalProperties.getBatchSize());
                    long batchId = message.getId();
                    List<CanalEntry.Entry> entries = message.getEntries();

                    if (batchId == -1 || entries == null || entries.isEmpty()) {
                        sleepQuietly(canalProperties.getPollInterval());
                        continue;
                    }

                    try {
                        handleEntries(entries);
                        connector.ack(batchId);
                    } catch (Exception e) {
                        connector.rollback();
                        log.error("处理 Canal Binlog 失败, batchId={}", batchId, e);
                        sleepQuietly(canalProperties.getPollInterval());
                    }
                }
            } catch (Exception e) {
                log.error("Canal 菜单缓存监听异常, 准备重连", e);
                sleepQuietly(canalProperties.getPollInterval());
            } finally {
                disconnectQuietly();
            }
        }
    }

    private CanalConnector createConnector(CacheConsistencyProperties.Canal canalProperties) {
        return CanalConnectors.newSingleConnector(
                new InetSocketAddress(canalProperties.getHost(), canalProperties.getPort()),
                canalProperties.getDestination(),
                canalProperties.getUsername(),
                canalProperties.getPassword()
        );
    }

    private void handleEntries(List<CanalEntry.Entry> entries) throws InvalidProtocolBufferException {
        Set<Long> dishCategoryIds = new HashSet<>();
        Set<Long> setmealCategoryIds = new HashSet<>();

        for (CanalEntry.Entry entry : entries) {
            if (entry.getEntryType() != CanalEntry.EntryType.ROWDATA) {
                continue;
            }

            String tableName = entry.getHeader().getTableName();
            CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            CanalEntry.EventType eventType = rowChange.getEventType();

            for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                switch (tableName) {
                    case "dish" -> collectDishCategoryIds(rowData, eventType, dishCategoryIds);
                    case "dish_flavor" -> collectDishFlavorCategoryIds(rowData, dishCategoryIds);
                    case "setmeal" -> collectSetmealCategoryIds(rowData, eventType, setmealCategoryIds);
                    case "setmeal_dish" -> collectSetmealDishCategoryIds(rowData, setmealCategoryIds);
                    default -> {
                    }
                }
            }
        }

        menuCacheInvalidationService.invalidateMenuCachesNowAndDelayed(
                dishCategoryIds,
                setmealCategoryIds,
                "canal-binlog"
        );
    }

    private void collectDishCategoryIds(CanalEntry.RowData rowData,
                                        CanalEntry.EventType eventType,
                                        Set<Long> categoryIds) {
        if (eventType == CanalEntry.EventType.DELETE || eventType == CanalEntry.EventType.UPDATE) {
            addColumnValue(rowData.getBeforeColumnsList(), "category_id", categoryIds);
        }
        if (eventType == CanalEntry.EventType.INSERT || eventType == CanalEntry.EventType.UPDATE) {
            addColumnValue(rowData.getAfterColumnsList(), "category_id", categoryIds);
        }
    }

    private void collectDishFlavorCategoryIds(CanalEntry.RowData rowData, Set<Long> categoryIds) {
        addDishCategoryIdByDishId(getColumnLongValue(rowData.getBeforeColumnsList(), "dish_id"), categoryIds);
        addDishCategoryIdByDishId(getColumnLongValue(rowData.getAfterColumnsList(), "dish_id"), categoryIds);
    }

    private void collectSetmealCategoryIds(CanalEntry.RowData rowData,
                                           CanalEntry.EventType eventType,
                                           Set<Long> categoryIds) {
        if (eventType == CanalEntry.EventType.DELETE || eventType == CanalEntry.EventType.UPDATE) {
            addColumnValue(rowData.getBeforeColumnsList(), "category_id", categoryIds);
        }
        if (eventType == CanalEntry.EventType.INSERT || eventType == CanalEntry.EventType.UPDATE) {
            addColumnValue(rowData.getAfterColumnsList(), "category_id", categoryIds);
        }
    }

    private void collectSetmealDishCategoryIds(CanalEntry.RowData rowData, Set<Long> categoryIds) {
        addSetmealCategoryIdBySetmealId(getColumnLongValue(rowData.getBeforeColumnsList(), "setmeal_id"), categoryIds);
        addSetmealCategoryIdBySetmealId(getColumnLongValue(rowData.getAfterColumnsList(), "setmeal_id"), categoryIds);
    }

    private void addDishCategoryIdByDishId(Long dishId, Set<Long> categoryIds) {
        if (dishId == null) {
            return;
        }

        Dish dish = dishMapper.getById(dishId);
        if (dish != null && dish.getCategoryId() != null) {
            categoryIds.add(dish.getCategoryId());
        }
    }

    private void addSetmealCategoryIdBySetmealId(Long setmealId, Set<Long> categoryIds) {
        if (setmealId == null) {
            return;
        }

        Setmeal setmeal = setmealMapper.getById(setmealId);
        if (setmeal != null && setmeal.getCategoryId() != null) {
            categoryIds.add(setmeal.getCategoryId());
        }
    }

    private void addColumnValue(List<CanalEntry.Column> columns, String columnName, Set<Long> categoryIds) {
        Long columnValue = getColumnLongValue(columns, columnName);
        if (columnValue != null) {
            categoryIds.add(columnValue);
        }
    }

    private Long getColumnLongValue(List<CanalEntry.Column> columns, String columnName) {
        for (CanalEntry.Column column : columns) {
            if (!columnName.equals(column.getName())) {
                continue;
            }
            String value = column.getValue();
            if (value == null || value.isBlank()) {
                return null;
            }
            return Long.parseLong(value);
        }
        return null;
    }

    private void disconnectQuietly() {
        CanalConnector currentConnector = this.connector;
        this.connector = null;
        if (currentConnector == null) {
            return;
        }

        try {
            currentConnector.disconnect();
        } catch (Exception e) {
            log.warn("关闭 Canal 连接失败", e);
        }
    }

    private void sleepQuietly(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
