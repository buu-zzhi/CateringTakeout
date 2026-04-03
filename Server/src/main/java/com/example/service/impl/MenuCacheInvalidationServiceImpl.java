package com.example.service.impl;

import com.example.constant.CacheConstant;
import com.example.properties.CacheConsistencyProperties;
import com.example.service.MenuCacheInvalidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
@Service
public class MenuCacheInvalidationServiceImpl implements MenuCacheInvalidationService {
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private TaskScheduler cacheInvalidationTaskScheduler;
    @Autowired
    private CacheConsistencyProperties cacheConsistencyProperties;

    @Override
    public void invalidateDishCategoriesAfterCommit(Collection<Long> categoryIds, String reason) {
        invalidateAfterCommit(categoryIds, null, reason);
    }

    @Override
    public void invalidateSetmealCategoriesAfterCommit(Collection<Long> categoryIds, String reason) {
        invalidateAfterCommit(null, categoryIds, reason);
    }

    @Override
    public void invalidateMenuCachesNowAndDelayed(Collection<Long> dishCategoryIds,
                                                  Collection<Long> setmealCategoryIds,
                                                  String reason) {
        Set<Long> normalizedDishCategoryIds = normalizeCategoryIds(dishCategoryIds);
        Set<Long> normalizedSetmealCategoryIds = normalizeCategoryIds(setmealCategoryIds);
        if (normalizedDishCategoryIds.isEmpty() && normalizedSetmealCategoryIds.isEmpty()) {
            return;
        }

        evictMenus(normalizedDishCategoryIds, normalizedSetmealCategoryIds, reason, false);

        long delayMillis = cacheConsistencyProperties.getDelayDelete().toMillis();
        if (delayMillis <= 0) {
            return;
        }

        cacheInvalidationTaskScheduler.schedule(
                () -> evictMenus(normalizedDishCategoryIds, normalizedSetmealCategoryIds, reason, true),
                Instant.now().plusMillis(delayMillis)
        );
    }

    private void invalidateAfterCommit(Collection<Long> dishCategoryIds,
                                       Collection<Long> setmealCategoryIds,
                                       String reason) {
        Set<Long> normalizedDishCategoryIds = normalizeCategoryIds(dishCategoryIds);
        Set<Long> normalizedSetmealCategoryIds = normalizeCategoryIds(setmealCategoryIds);
        if (normalizedDishCategoryIds.isEmpty() && normalizedSetmealCategoryIds.isEmpty()) {
            return;
        }

        Runnable invalidationTask = () ->
                invalidateMenuCachesNowAndDelayed(normalizedDishCategoryIds, normalizedSetmealCategoryIds, reason);

        if (TransactionSynchronizationManager.isSynchronizationActive()
                && TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    invalidationTask.run();
                }
            });
            return;
        }

        invalidationTask.run();
    }

    private Set<Long> normalizeCategoryIds(Collection<Long> categoryIds) {
        Set<Long> normalizedCategoryIds = new LinkedHashSet<>();
        if (categoryIds == null) {
            return normalizedCategoryIds;
        }

        categoryIds.stream()
                .filter(categoryId -> categoryId != null && categoryId > 0)
                .forEach(normalizedCategoryIds::add);
        return normalizedCategoryIds;
    }

    private void evictMenus(Set<Long> dishCategoryIds,
                            Set<Long> setmealCategoryIds,
                            String reason,
                            boolean delayed) {
        evictCache(CacheConstant.DISH_CACHE, dishCategoryIds);
        evictCache(CacheConstant.SETMEAL_CACHE, setmealCategoryIds);
        log.info("菜单缓存删除完成, reason={}, delayed={}, dishCategoryIds={}, setmealCategoryIds={}",
                reason, delayed, dishCategoryIds, setmealCategoryIds);
    }

    private void evictCache(String cacheName, Set<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }

        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return;
        }

        categoryIds.forEach(cache::evict);
    }
}
