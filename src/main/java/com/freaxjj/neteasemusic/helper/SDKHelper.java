package com.freaxjj.neteasemusic.helper;

import com.alibaba.fastjson.JSONObject;
import com.freaxjj.neteasemusic.config.NeteaseConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.yumbo.util.music.musicImpl.netease.NeteaseCloudMusicInfo;

@Slf4j
@Component
public class SDKHelper {
    @Autowired
    private NeteaseConfig neteaseConfig;

    public void login() {
        try {
            final NeteaseCloudMusicInfo neteaseCloudMusicInfo = new NeteaseCloudMusicInfo();// 得到封装网易云音乐信息的工具类
            neteaseCloudMusicInfo.setCookieString(neteaseConfig.getCookie());
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone", neteaseConfig.getPhone());
            jsonObject.put("md5_password", neteaseConfig.getPassword());
            final JSONObject loginRes = neteaseCloudMusicInfo.login(jsonObject);
            log.info(loginRes.toJSONString());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
