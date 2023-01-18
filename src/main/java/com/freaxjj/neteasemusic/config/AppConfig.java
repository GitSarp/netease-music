package com.freaxjj.neteasemusic.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @Author: freaxjj
 * @Desc:
 * @Date: 3/23/22 8:56 AM
 */
@Data
@Configuration
@ConfigurationProperties("allowed")
public class AppConfig {
    private Map<String, String> apps;
}
