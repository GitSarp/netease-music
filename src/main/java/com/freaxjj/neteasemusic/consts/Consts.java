package com.freaxjj.neteasemusic.consts;

/**
 * @Author: freaxjj
 * @Desc:
 * @Date: 3/22/22 10:53 PM
 */
public class Consts {
    public static final String INSECURE_HTTP = "http:";
    public static final String SECURE_HTTP = "https:";

    /**
     * 请求接口
     */
    public static final String URL_LOGIN = "/login/cellphone";
    public static final String URL_LOGIN_REFRESH = "/login/refresh";
    public static final String URL_PLAYLIST = "/user/playlist";
    public static final String URL_PLAYLIST_DETAIL = "/playlist/detail";
    public static final String URL_SONG_DETAIL = "/song/detail";
    public static final String URL_SONG_URL = "/song/url";

    public static String getUrl(String host, String uri) {
        return host.concat(uri);
    }

    /**
     * 接口返回
     */
    public static final Integer HTTP_RESP_OK = 200;
    public static final Integer HTTP_RESP_REDIRECT = 301;
    public static final Integer HTTP_RESP_NEED_LOGIN = 20001;
    public static final String HTTP_RESP_CODE = "code";
    public static final String HTTP_RESP_COOKIE = "cookie";

    /**
     * redis key
     */
    public static final String REDIS_KEY_SONGS = "key-songs";
    public static final String REDIS_KEY_COOKIE = "key-cookie";

    /**
     * url expire
     */
    public static final Integer TIME_URL_EXPIRE = 600;

}
