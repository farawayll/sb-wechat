package com.lang.wechat.config.mp;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 服务号模板消息配置类
 */
@Configuration
public class MyWxMpConfig {

    @Value("${wx.mp.appId}")
    private String appId;

    @Value("${wx.mp.secret}")
    private String secret;

    @Value("${wx.mp.token}")
    private String token;

    @Value("${wx.mp.aesKey}")
    private String aesKey;

    @Autowired
    private MyWxMpRedisConfig mpRedisConfig;

    @Bean
    public WxMpService wxMpService() {
        mpRedisConfig.setAppId(appId);
		mpRedisConfig.setSecret(secret);
        mpRedisConfig.setToken(token);
        mpRedisConfig.setAesKey(aesKey);

        WxMpServiceImpl wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(mpRedisConfig);
        return wxMpService;
    }
}
