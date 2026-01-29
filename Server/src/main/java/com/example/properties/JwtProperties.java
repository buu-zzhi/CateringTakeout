package com.example.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static org.apache.naming.SelectorContext.prefix;

@Component
@ConfigurationProperties(prefix = "sky.jwt")
@Data
public class JwtProperties {
    private String adminSecretKey;
    private Long adminTtl;
    private String adminTokenName;
    private String adminDataName;

    private String userSecretKey;
    private Long userTtl;
    private String userTokenName;
}
