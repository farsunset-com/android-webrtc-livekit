package com.example.webrtc.config;

public class AppConfig {


    /**
     * webrtc-push-server 启动后的访问地址
     */
    public static final String WEBRTC_PUSH_SERVER_HOST = "39.99.150.41";
    public static final String WEBRTC_PUSH_SERVER_URI = "http://"+WEBRTC_PUSH_SERVER_HOST+":8080";

    public static final String LIVEKIT_SERVER_URI = "wss://livekit.farsunset.com";

    /**
     * 用户头像图片URL，URL中需要带uid占位符
     */
    public static final String USER_LOGO_URI = "https://api.bugu.farsunset.com/file/hoxin-user-icon/{uid}";

    /*
    当前用户ID
     */
    public static final long UID = 10000;


    /*
    当前用户名称
     */
    public static final String NAME = "张三";
}
