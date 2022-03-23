package com.freaxjj.neteasemusic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: freaxjj
 * @Desc: 歌单列表
 * @Date: 3/22/22 10:13 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayList extends BaseDTO{
    private List<Play> playlist;
}
