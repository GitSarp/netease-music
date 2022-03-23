package com.freaxjj.neteasemusic.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: freaxjj
 * @Desc:
 * @Date: 3/23/22 12:26 AM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteSong {
    private String name;
    private String artist;
    private String cover;

    /**
     * url 1200s过期
     */
    private String url;
}
