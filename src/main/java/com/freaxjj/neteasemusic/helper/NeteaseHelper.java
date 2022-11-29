package com.freaxjj.neteasemusic.helper;

import com.alibaba.fastjson.JSON;
import com.freaxjj.neteasemusic.config.NeteaseConfig;
import com.freaxjj.neteasemusic.consts.Consts;
import com.freaxjj.neteasemusic.dto.*;
import com.freaxjj.neteasemusic.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
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
    private String apiHost;
    public static List<String> cookies;

    public NeteaseHelper(@Autowired NeteaseConfig neteaseConfig, @Autowired HttpClientUtil httpClientUtil) {
        this.neteaseConfig = neteaseConfig;
        this.httpClientUtil = httpClientUtil;
        this.apiHost = neteaseConfig.getApiHost();
    }

    /**
     * 聚合
     *
     * @return 喜欢的歌原始数据
     */
    public SongDetail getSongs() throws Exception {
        Long startTime = System.currentTimeMillis();
        log.info("获取所有歌曲开始...");
        SongDetail songDetail = null;
        //获取歌单列表
        PlayList playList = getPlayList();
        if (Objects.nonNull(playList) && !CollectionUtils.isEmpty(playList.getPlaylist())) {
            Long playId = playList.getPlaylist().get(0).getId();
            //获取最喜欢的歌单
            PlayListDetail playListDetail = getPlayDetail(playId);
            if (Objects.nonNull(playListDetail) && Objects.nonNull(playListDetail.getPlaylist())) {
                List<Track> tracks = playListDetail.getPlaylist().getTrackIds();
                if (!CollectionUtils.isEmpty(tracks)) {
                    String trackStr = tracks.stream()
                            .map(track -> String.valueOf(track.getId()))
                            .collect(Collectors.joining(","));
                    //批量查询歌曲详情
                    songDetail = getSongsDetail(trackStr);

                    //批量查询歌曲url
                    SongURLS songUrlS = getSongUrl(trackStr);
                    Map<Long, String> songURLMap = songUrlS.getData().stream()
                            //不加这个转map会报错
                            .filter(s -> StringUtils.hasLength(s.getUrl()))
                            .collect(Collectors.toMap(SongURL::getId, SongURL::getUrl));
                    //批量设置歌曲url
                    List<Song> songs = songDetail.getSongs().stream().map(song -> {
                        song.setUrl(songURLMap.get(song.getId()));
                        return song;
                    }).filter(s -> Objects.nonNull(s)).collect(Collectors.toList());
                    songDetail.setSongs(songs);
                }
            }
        }
        log.info("获取歌曲{}条，耗时(s)：{}...", songDetail.getSongs().size(), (System.currentTimeMillis() - startTime) / 1000);
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
        String cookieStr = (String) resp.get(Consts.HTTP_RESP_COOKIE);
        cookies = Arrays.stream(cookieStr.split(";;")).collect(Collectors.toList());
        log.info("登录返回：{}", JSON.toJSONString(resp));
        return resp;
    }

    /**
     * 刷新登录
     *
     * @throws Exception
     */
    public String loginRefresh() throws Exception {
        Map<String, Object> params = new HashMap<>(2);
        Object refreshRes = doRequest(HttpMethod.POST, Consts.URL_LOGIN_REFRESH, Object.class, params);
        String res = JSON.toJSONString(refreshRes);
        log.info("刷新登录返回：{}", res);
        return res;
    }

    private PlayList getPlayList() throws Exception {
        Map<String, Object> params = new HashMap<>(4);
        params.put("uid", neteaseConfig.getUid());
        PlayList playList = doRequest(HttpMethod.POST, Consts.URL_PLAYLIST, PlayList.class, params);
        //log.info("获取歌单列表成功：{}", JSON.toJSONString(playList));
        return playList;
    }

    private PlayListDetail getPlayDetail(Long playId) throws Exception {
        Map<String, Object> params = new HashMap<>(4);
        params.put("id", playId);
        PlayListDetail playListDetail = doRequest(HttpMethod.POST, Consts.URL_PLAYLIST_DETAIL, PlayListDetail.class, params);
        //log.info("获取歌单详情成功：{}", JSON.toJSONString(playListDetail));
        return playListDetail;
    }

    private SongDetail getSongsDetail(String songIds) throws Exception {
        Map<String, Object> params = new HashMap<>(4);
        params.put("ids", songIds);
        SongDetail songDetail = doRequest(HttpMethod.POST, Consts.URL_SONG_DETAIL, SongDetail.class, params);
        //log.info("获取歌单歌曲详情成功：{}", JSON.toJSONString(songDetail));
        return songDetail;
    }

    private SongURLS getSongUrl(String songIds) throws Exception {
        Map<String, Object> params = new HashMap<>(4);
        params.put("id", songIds);
        SongURLS songURLS = doRequest(HttpMethod.POST, Consts.URL_SONG_URL, SongURLS.class, params);
        //log.info("获取歌曲详情成功：{}", JSON.toJSONString(songURL));
        return songURLS;
    }

    private <T> T doRequest(HttpMethod httpMethod, String url, Class<T> tClass, Map<String, Object> params) throws Exception {
        //加时间戳，防止netease api缓存
        params.put("timestamp", System.currentTimeMillis());
        params.put("realIP", "112.32.28.111");
        HttpEntity<Map<String, Object>> request;
        //跨域需要设置cookie
        if (!Consts.URL_LOGIN.equals(url)) {
            HttpHeaders headers = new HttpHeaders();
            headers.put(HttpHeaders.COOKIE, cookies);
            request = new HttpEntity<>(params, headers);
        } else {
            request = new HttpEntity<>(params);
        }
        //拼接url
        url = Consts.getUrl(apiHost, url);

        T resp = null;
        if (HttpMethod.GET.equals(httpMethod)) {
            resp = httpClientUtil.doGet(url, tClass, params);
        } else if (HttpMethod.POST.equals(httpMethod)) {
            if (Objects.isNull(request)) {
                resp = httpClientUtil.doPost(url, tClass, params);
            } else {
                resp = httpClientUtil.doPost(url, request, tClass);
            }
        }

        Map<String, Object> respMap = (Map<String, Object>) JSON.toJSON(resp);
        Object resCode = respMap.get(Consts.HTTP_RESP_CODE);
        //返回结果检查
        if (Consts.HTTP_RESP_OK.equals(resCode)) {
            return resp;
        } else if (Consts.HTTP_RESP_REDIRECT.equals(resCode) || Consts.HTTP_RESP_NEED_LOGIN.equals(resCode)){
            if(Consts.HTTP_RESP_REDIRECT.equals(resCode)){
                //刷新登录
                loginRefresh();
            }else if(Consts.HTTP_RESP_NEED_LOGIN.equals(resCode)){
                //重新登录
                login();
            }

            //重新调用
            resp = doRequest(httpMethod, url, tClass, params);
            respMap = (Map<String, Object>) JSON.toJSON(resp);
            resCode = respMap.get(Consts.HTTP_RESP_CODE);
            if (!Consts.HTTP_RESP_OK.equals(resCode)) {
                throw new Exception("请求接口" + url + "失败！接口返回：" + JSON.toJSONString(respMap));
            } else {
                return resp;
            }
        } else {
            throw new Exception("请求接口" + url + "失败！接口返回：" + JSON.toJSONString(resp));
        }

    }
}
