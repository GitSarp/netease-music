package com.freaxjj.neteasemusic.utils;

import com.alibaba.fastjson.JSON;
import com.freaxjj.neteasemusic.dto.PlayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: freaxjj
 * @Desc:
 * @Date: 3/22/22 10:04 PM
 */
@Slf4j
@Component
public class HttpClientUtil {
    private static RestTemplate restTemplate = new RestTemplate();

    public <T> T doGet(String url, Class<T> resp, Map<String, ?> params){
        T result=null;
        try {
            result=restTemplate.getForObject(url, resp, params);
        }catch (Exception e){
            log.error("请求异常："+e.getMessage());
        }
        return result;
    }

    public <T> T doPost(String url, Object request, Class<T> resp, Map<String, ?> params) {
        T result=null;
        try {
            result=restTemplate.postForObject(url, request, resp, params);
        }catch (Exception e){
            log.error("请求异常："+e.getMessage());
        }
        return result;
    }

    public <T> T doPost(String url, Object request, Class<T> resp){
        T result=null;
        try {
            result=restTemplate.postForObject(url, request, resp);
        }catch (Exception e){
            log.error("请求异常："+e.getMessage());
        }
        return result;
    }

    public static void main(String[] args) {
        HttpClientUtil httpClientUtil = new HttpClientUtil();

//        Map<String, String> params = new HashMap<>(2);
//        params.put("phone", "");
//        params.put("md5_password", "");
//        Object resp = httpClientUtil.doPost("http://localhost:3000/login/cellphone", Object.class, params);
//        log.info("登录成功：{}", JSON.toJSONString(resp));

        Map<String, String> params = new HashMap<>(2);
        params.put("uid", "");
        PlayList playList = httpClientUtil.doGet("http://localhost:3000/user/playlist?uid={uid}", PlayList.class, params);
        log.info("获取歌单列表成功：{}", JSON.toJSONString(playList));

//        Map<String, String> params = new HashMap<>(2);
//        params.put("id", "");
//        PlayListDetail playListDetail = httpClientUtil.doGet("http://localhost:3000/playlist/detail?id={id}", PlayListDetail.class, params);
//        log.info("获取歌单详情成功：{}", JSON.toJSONString(playListDetail));

//        Map<String, String> params = new HashMap<>(2);
//        params.put("ids", "");
//        SongDetail songDetail = httpClientUtil.doGet("http://localhost:3000/song/detail?ids={ids}", SongDetail.class, params);
//        log.info("获取歌曲详情成功：{}", JSON.toJSONString(songDetail));

    }
}
