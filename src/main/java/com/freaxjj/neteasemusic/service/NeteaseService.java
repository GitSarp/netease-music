package com.freaxjj.neteasemusic.service;

import com.alibaba.fastjson.JSON;
import com.freaxjj.neteasemusic.consts.Consts;
import com.freaxjj.neteasemusic.dto.Artist;
import com.freaxjj.neteasemusic.dto.Song;
import com.freaxjj.neteasemusic.dto.SongDetail;
import com.freaxjj.neteasemusic.helper.NeteaseHelper;
import com.freaxjj.neteasemusic.utils.RedisUtil;
import com.freaxjj.neteasemusic.vo.FavoriteSong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: freaxjj
 * @Desc:
 * @Date: 3/23/22 8:50 AM
 */
@Slf4j
@Service
public class NeteaseService {
    @Autowired
    private NeteaseHelper neteaseHelper;

    /**
     * 从redis获取数据
     * @return
     */
    public String getSongsFromRedis() {
        return RedisUtil.StringOps.get(Consts.REDIS_KEY_SONGS);
    }

    /**
     * 调用接口获取歌曲数据，并存入redis
     * 由定时任务调用
     */
    public String getSongs() throws Exception {
        //从redis获取数据
        String songsInfo = getSongsFromRedis();
        if(StringUtils.hasLength(songsInfo)){
            return songsInfo;
        }

        //获取所有歌曲
        SongDetail songDetail = neteaseHelper.getSongs();
        //转化成前端需要的格式
        if(Objects.nonNull(songDetail) && !CollectionUtils.isEmpty(songDetail.getSongs())) {
            List<FavoriteSong> songs = new ArrayList<>();
            songDetail.getSongs().forEach(song -> {
                FavoriteSong favoriteSong = transformSong(song);
                songs.add(favoriteSong);
            });
            //存入redis
            songsInfo = JSON.toJSONString(songs);
            //设置有效期为一半(预留40s为处理时间,(1200-40) / 2),客户端时间间隔应小于等于此值
            RedisUtil.StringOps.setEx(Consts.REDIS_KEY_SONGS, songsInfo, Consts.TIME_URL_EXPIRE, TimeUnit.SECONDS);
        }
        return songsInfo;
    }

    /**
     * 登录接口
     * 由定时任务调用
     * @throws Exception
     */
    public void login() throws Exception {
        Map resp = neteaseHelper.login();
        if(Objects.isNull(resp) || !Consts.HTTP_RESP_OK.equals(resp.get(Consts.HTTP_RESP_CODE))) {
            throw new Exception("登录错误！");
        }
    }

    public String refreshLogin() throws Exception {
        return neteaseHelper.loginRefresh();
    }

    /**
     * 转化成前端需要的格式
     * @param song
     * @return
     */
    private FavoriteSong transformSong(Song song) {
        FavoriteSong favoriteSong = new FavoriteSong();
        favoriteSong.setName(song.getName());
        favoriteSong.setCover(song.getAl().getPicUrl());
        String artist = song.getAr().stream().map(Artist::getName).collect(Collectors.joining(","));
        favoriteSong.setArtist(artist);
        String url = song.getUrl();
        if(Objects.nonNull(url) && url.startsWith(Consts.INSECURE_HTTP)){
            favoriteSong.setUrl(Consts.SECURE_HTTP.concat(url.substring(5)));
        }else {
            favoriteSong.setUrl(url);
        }
        return favoriteSong;
    }
}
