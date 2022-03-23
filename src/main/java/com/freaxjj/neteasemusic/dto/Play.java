package com.freaxjj.neteasemusic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: freaxjj
 * @Desc: 歌单实体
 * @Date: 3/22/22 10:15 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Play {
    private Long id;

    private List<Track> trackIds;
}
