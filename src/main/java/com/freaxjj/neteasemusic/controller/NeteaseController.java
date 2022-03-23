package com.freaxjj.neteasemusic.controller;

import com.freaxjj.neteasemusic.config.AppConfig;
import com.freaxjj.neteasemusic.service.NeteaseService;
import com.freaxjj.neteasemusic.vo.FavoriteSong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * @Author: freaxjj
 * @Desc:
 * @Date: 3/23/22 10:03 AM
 */
@Slf4j
@RestController
public class NeteaseController {
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private NeteaseService neteaseService;


    @GetMapping("/songs")
    public List<FavoriteSong> getSongs(@NotBlank @RequestHeader String appId, @NotBlank @RequestHeader String appSec) {
        //后期业务扩展，再上拦截器吧^_^
        Map<String, String> apps = appConfig.getApps();
        if(!CollectionUtils.isEmpty(apps)) {
            if(!appSec.equals(apps.get(appId))) {
                log.error("客户端不允许接入: {} {}", appId, appSec);
                return null;
            }
        }

        List<FavoriteSong> songs = neteaseService.getSongs(appId, appSec);
        log.info("获取到共{}首歌曲！", songs.size());
        return songs;
    }
}
