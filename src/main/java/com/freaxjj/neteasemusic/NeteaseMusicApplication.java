package com.freaxjj.neteasemusic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableScheduling
@RefreshScope
//启用nacos服务发现
@EnableDiscoveryClient
@SpringBootApplication
public class NeteaseMusicApplication {

    public static void main(String[] args) {
        SpringApplication.run(NeteaseMusicApplication.class, args);
    }

}
