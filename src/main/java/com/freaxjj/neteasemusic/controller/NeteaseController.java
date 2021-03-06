package com.freaxjj.neteasemusic.controller;

import com.freaxjj.neteasemusic.config.AppConfig;
import com.freaxjj.neteasemusic.service.NeteaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * @Author: freaxjj
 * @Desc:
 * @Date: 3/23/22 10:03 AM
 */
@Slf4j
@CrossOrigin(origins = {"http://v.freaxjj.site", "https://v.freaxjj.site", "http://v.freaxjj.tk", "https://v.freaxjj.tk"})
@RestController
public class NeteaseController {
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private NeteaseService neteaseService;

    @GetMapping("/songs")
    public String getSongs(@NotBlank @RequestHeader String appId, @NotBlank @RequestHeader String appSec) throws Exception {
        //后期业务扩展，再上拦截器吧^_^ 也懒得做加签啥的了
        Map<String, String> apps = appConfig.getApps();
        if(!CollectionUtils.isEmpty(apps)) {
            if(!appSec.equals(apps.get(appId))) {
                log.error("客户端不允许接入: {} {}", appId, appSec);
                return null;
            }
        }

        String songs = neteaseService.getSongs();
        return songs;
    }

    @GetMapping("/refresh")
    public String refreshLogin() throws Exception {
        return neteaseService.refreshLogin();
    }
}
