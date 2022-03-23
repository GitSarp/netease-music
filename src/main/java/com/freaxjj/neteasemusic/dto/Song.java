package com.freaxjj.neteasemusic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: freaxjj
 * @Desc:
 * @Date: 3/23/22 12:10 AM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Song {
    private Long id;
    private String name;
    private List<Artist> ar;
    private Album al;
}
