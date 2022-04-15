package com.freaxjj.neteasemusic.runner;

import com.freaxjj.neteasemusic.service.NeteaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @Author: freaxjj
 * @Desc:
 * @Date: 3/25/22 4:23 PM
 */
@Slf4j
@Component
public class StartRunner implements ApplicationRunner {
    @Autowired
    private NeteaseService neteaseService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //登录
        neteaseService.login();
    }
}
