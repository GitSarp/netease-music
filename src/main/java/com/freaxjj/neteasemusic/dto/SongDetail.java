package com.freaxjj.neteasemusic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: freaxjj
 * @Desc:
 * @Date: 3/23/22 12:09 AM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongDetail extends BaseDTO{
    private List<Song> songs;
}
