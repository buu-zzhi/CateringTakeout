package com.example.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data
@Component
@ConfigurationProperties(prefix = "app.cache.consistency")
public class CacheConsistencyProperties {
    private Duration delayDelete = Duration.ofMillis(500);
    private Duration dishTtl = Duration.ofMinutes(30);
    private Duration setmealTtl = Duration.ofMinutes(30);
    private Canal canal = new Canal();

    @Data
    public static class Canal {
        private boolean enabled = false;
        private String host = "127.0.0.1";
        private Integer port = 11111;
        private String destination = "example";
        private String username = "";
        private String password = "";
        private String filter = "sky_take_out.dish,sky_take_out.dish_flavor,sky_take_out.setmeal,sky_take_out.setmeal_dish";
        private Integer batchSize = 100;
        private Duration pollInterval = Duration.ofSeconds(1);
    }
}
