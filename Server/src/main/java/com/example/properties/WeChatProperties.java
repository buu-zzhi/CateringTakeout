package com.example.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "sky.wechat")
@Component
@Data
public class WeChatProperties {
    private String appid;
    private String secret;
}
