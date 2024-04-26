package com.freaxjj.neteasemusic.task;

import com.freaxjj.neteasemusic.helper.NeteaseHelper;
import com.freaxjj.neteasemusic.service.NeteaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.scheduling.annotation.Async;

/**
 * @Author: freaxjj
 * @Desc: 定时刷新歌曲数据，避免url过期
 * @Date: 11/4/21 12:56 PM
 */
@Slf4j
@Component
public class ScheduledTask {
    @Autowired
    private NeteaseService neteaseService;

    /**
     * 定时获取歌曲url
     */
    @Async
    @Scheduled(fixedRate = 580000)
    public void getSongsTimely() {
        //等待登录后获取歌曲url
        while (CollectionUtils.isEmpty(NeteaseHelper.cookies)){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        log.info("定时获取歌曲开始...");
        try {
            neteaseService.getSongsFromAPI();
        }catch (Exception e){
            log.error("通过API获取歌曲失败!");
            e.printStackTrace();
        }
    }
}
