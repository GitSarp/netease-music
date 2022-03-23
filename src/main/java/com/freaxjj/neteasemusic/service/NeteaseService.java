package com.freaxjj.neteasemusic.service;

import com.alibaba.fastjson.JSON;
import com.freaxjj.neteasemusic.consts.Consts;
import com.freaxjj.neteasemusic.dto.Artist;
import com.freaxjj.neteasemusic.dto.Song;
import com.freaxjj.neteasemusic.dto.SongDetail;
import com.freaxjj.neteasemusic.dto.SongURLDetail;
import com.freaxjj.neteasemusic.helper.NeteaseHelper;
import com.freaxjj.neteasemusic.utils.RedisUtil;
import com.freaxjj.neteasemusic.vo.FavoriteSong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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
     * 返回歌曲信息给前端
     * @param appId
     * @param appSec
     * @return
     */
    public List<FavoriteSong> getSongs(String appId, String appSec) {
        //从redis获取数据
        Map<Object,Object> songs = RedisUtil.HashOps.hGetAllObj(Consts.REDIS_KEY_SONGS);
        List<FavoriteSong> favoriteSongs = songs.entrySet().stream()
                .map(e -> (FavoriteSong)e).collect(Collectors.toList());
        return favoriteSongs;
    }

    /**
     * 调用接口获取歌曲数据，并存入redis
     * 由定时任务调用
     */
    public void saveSongs2Redis() throws Exception {
        //获取所有歌曲
        SongDetail songDetail = neteaseHelper.getSongs();
        //转化成前端需要的格式
        if(Objects.nonNull(songDetail) && !CollectionUtils.isEmpty(songDetail.getSongs())) {
            songDetail.getSongs().forEach(song -> {
                FavoriteSong favoriteSong;
                try {
                    favoriteSong = getSongURLDetail(song);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                //存入redis
                RedisUtil.HashOps.hPutObj(Consts.REDIS_KEY_SONGS, song.getId(), favoriteSong);
            });
        }
    }

    /**
     * 登录接口
     * 由定时任务调用
     * @throws Exception
     */
    public void login() throws Exception {
        Map resp = neteaseHelper.login();
        log.info("登录成功：{}", JSON.toJSONString(resp));
        if(Objects.isNull(resp) || !Consts.HTTP_RESP_OK.equals(resp.get(Consts.HTTP_RESP_CODE))) {
            throw new Exception("登录错误！");
        }
    }

    /**
     * 转化成前端需要的格式
     * @param song
     * @return
     */
    private FavoriteSong getSongURLDetail(Song song) throws Exception {
        FavoriteSong favoriteSong = new FavoriteSong();
        favoriteSong.setName(song.getName());
        favoriteSong.setCover(song.getAl().getPicUrl());
        String artist = song.getAr().stream().map(Artist::getName).collect(Collectors.joining(","));
        favoriteSong.setArtist(artist);
        SongURLDetail songURLDetail = neteaseHelper.getSongUrlAvailable(song.getId());
        favoriteSong.setUrl(songURLDetail.getUrl());
        return favoriteSong;
    }
}
