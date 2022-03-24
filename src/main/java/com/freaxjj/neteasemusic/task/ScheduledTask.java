package com.freaxjj.neteasemusic.task;

import com.freaxjj.neteasemusic.service.NeteaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
     * 凌晨30分定时刷新活动数据
     */
    @Scheduled(fixedRate = 1100000)
    public void refreshActivity() {
        try {
            neteaseService.login();
            neteaseService.saveSongs2Redis();
        }catch (Exception e){
            log.error("任务执行失败!");
            e.printStackTrace();
        }
    }
}
