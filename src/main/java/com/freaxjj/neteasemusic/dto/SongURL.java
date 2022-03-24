package com.freaxjj.neteasemusic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: freaxjj
 * @Desc:
 * @Date: 3/23/22 12:46 AM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongURL extends BaseDTO{
    private Long id;
    private String url;
    private Long expi;
}
