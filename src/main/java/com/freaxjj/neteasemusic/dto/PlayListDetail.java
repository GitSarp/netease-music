package com.freaxjj.neteasemusic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: freaxjj
 * @Desc:
 * @Date: 3/22/22 11:19 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayListDetail extends BaseDTO {
    private Play playlist;
}
