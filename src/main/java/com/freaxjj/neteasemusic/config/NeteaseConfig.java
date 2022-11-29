package com.freaxjj.neteasemusic.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: freaxjj
 * @Desc:
 * @Date: 3/23/22 8:56 AM
 */
@Data
@Configuration
@ConfigurationProperties("netease")
public class NeteaseConfig {
    private String phone;

    private String password;

    private String uid;

    private String apiHost;

    private String cookie;
}
