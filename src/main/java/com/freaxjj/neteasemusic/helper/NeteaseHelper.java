package com.freaxjj.neteasemusic.helper;

import com.alibaba.fastjson.JSON;
import com.freaxjj.neteasemusic.config.NeteaseConfig;
import com.freaxjj.neteasemusic.consts.Consts;
import com.freaxjj.neteasemusic.dto.*;
import com.freaxjj.neteasemusic.utils.HttpClientUtil;
import com.freaxjj.neteasemusic.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: freaxjj
 * @Desc: 调用netease api接口
 * @Date: 3/22/22 10:37 PM
 */
@Slf4j
@Component
public class NeteaseHelper {
    private NeteaseConfig neteaseConfig;
    private HttpClientUtil httpClientUtil;
    public  String apiHost;

    public NeteaseHelper(@Autowired NeteaseConfig neteaseConfig, @Autowired HttpClientUtil httpClientUtil){
        this.neteaseConfig = neteaseConfig;
        this.httpClientUtil = httpClientUtil;
        this.apiHost = neteaseConfig.getApiHost();
    }

    /**
     * 聚合
     * @return 喜欢的歌原始数据
     */
    public SongDetail getSongs() throws Exception {
        SongDetail songDetail = null;
        //获取歌单列表
        PlayList playList = getPlayList();
        if(Objects.nonNull(playList) && !CollectionUtils.isEmpty(playList.getPlaylist())) {
            Long playId = playList.getPlaylist().get(0).getId();
            //获取最喜欢的歌单
            PlayListDetail playListDetail = getPlayDetail(playId);
            if(Objects.nonNull(playListDetail) && Objects.nonNull(playListDetail.getPlaylist())) {
                List<Track> tracks = playListDetail.getPlaylist().getTrackIds();
                if(!CollectionUtils.isEmpty(tracks)) {
                    String trackStr = tracks.stream()
                            .map(track -> String.valueOf(track.getId()))
                            .collect(Collectors.joining(","));
                    songDetail = getSongsDetail(trackStr);
                }
            }
        }
        return songDetail;
    }

    /**
     * 登录
     */
    public Map login() throws Exception {
        Map<String, Object> params = new HashMap<>(4);
        params.put("phone", neteaseConfig.getPhone());
        params.put("md5_password", neteaseConfig.getPassword());
        Map resp = doRequest(HttpMethod.POST, Consts.URL_LOGIN, Map.class, params);
        //保存cookie
        //RedisUtil.StringOps.setObj(Consts.REDIS_KEY_COOKIE, resp.get(Consts.HTTP_RESP_COOKIE));
        return resp;
    }

    private PlayList getPlayList() throws Exception {
        Map<String, Object> params = new HashMap<>(2);
        params.put("uid", neteaseConfig.getUid());
        PlayList playList = doRequest(HttpMethod.POST, Consts.URL_PLAYLIST, PlayList.class, params);
        log.info("获取歌单列表成功：{}", JSON.toJSONString(playList));
        return playList;
    }

    private PlayListDetail getPlayDetail(Long playId) throws Exception {
        Map<String, Object> params = new HashMap<>(2);
        params.put("id", playId);
        PlayListDetail playListDetail = doRequest(HttpMethod.GET, Consts.URL_PLAYLIST_DETAIL, PlayListDetail.class, params);
        log.info("获取歌单详情成功：{}", JSON.toJSONString(playListDetail));
        return playListDetail;
    }

    private SongDetail getSongsDetail(String songIds) throws Exception {
        Map<String, Object> params = new HashMap<>(2);
        params.put("ids", songIds);
        SongDetail songDetail = doRequest(HttpMethod.POST, Consts.URL_SONG_DETAIL, SongDetail.class, params);
        log.info("获取歌单歌曲详情成功：{}", JSON.toJSONString(songDetail));
        return songDetail;
    }

    private SongURL getSongUrl(Long songId) throws Exception {
        Map<String, Object> params = new HashMap<>(2);
        params.put("id", songId);
        SongURL songURL = doRequest(HttpMethod.POST, Consts.URL_SONG_URL, SongURL.class, params);
        log.info("获取歌曲详情成功：{}", JSON.toJSONString(songURL));
        return songURL;
    }

    public SongURLDetail getSongUrlAvailable(Long songId) throws Exception {
        SongURL songURL = getSongUrl(songId);
        return songURL.getData().stream().filter(s -> Consts.HTTP_RESP_OK.equals(s.getCode())).findFirst().get();
    }

    private <T> T doRequest(HttpMethod httpMethod, String url, Class<T> tClass, Map<String, Object> params) throws Exception {
        //加时间戳，防止netease api缓存报错
        params.put("timestamp", System.currentTimeMillis());
        //设置cookie
        HttpEntity<Map<String,Object>> request = null;
//        if(!Consts.URL_LOGIN.equals(url)) {
//            HttpHeaders headers = new HttpHeaders();
//            headers.put(Consts.HTTP_RESP_COOKIE, (List<String>) RedisUtil.StringOps.getObj(Consts.REDIS_KEY_COOKIE));
//            request = new HttpEntity<>(params,headers);
//        }
        //拼接url
        url = Consts.getUrl(apiHost, url);

        T resp = null;
        if(HttpMethod.GET.equals(httpMethod)) {
            resp = httpClientUtil.doGet(url, tClass, params);
        }else if(HttpMethod.POST.equals(httpMethod)) {
            if(Objects.isNull(request)) {
                resp = httpClientUtil.doPost(url, tClass, params);
            } else {
                resp = httpClientUtil.doPost(url, request, tClass);
            }
        }

        Map<String, Object> respMap = (Map<String, Object>) JSON.toJSON(resp);
        if(CollectionUtils.isEmpty(respMap) || !Consts.HTTP_RESP_OK.equals(respMap.get(Consts.HTTP_RESP_CODE))) {
            throw new Exception("请求接口"+ url +"失败！接口返回：" + JSON.toJSONString(respMap));
        }
        return resp;
    }
}
