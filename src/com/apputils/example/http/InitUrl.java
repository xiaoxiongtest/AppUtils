package com.apputils.example.http;

public class InitUrl {
    
    /**@Fields DEF_CACHETIME : 默认缓存时间24h */
    public static final Long DEF_CACHETIME = 86400000L;
    
    public String url;
    
    /**@Fields type : get、post区分标志位 */
    public int type;
    
    /**@Fields className : 解析对象 */
    public String className;

    
    /**@Fields cacheTime : 缓存时间，避免在有网的状况下无限制刷新 */
    public Long cacheTime;
    
}
