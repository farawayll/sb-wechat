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
public class WxMpConfiguration {

    @Value("${wechat.mp.appId}")
    private String appId;

    @Value("${wechat.mp.secret}")
    private String secret;

    @Value("${wechat.mp.token}")
    private String token;

    @Value("${wechat.mp.aesKey}")
    private String aesKey;

    @Autowired
    private MyWxMpRedisConfig mpConfig;

    @Bean
    public WxMpService wxMpService() {
        mpConfig.setAppId(appId);
		mpConfig.setSecret(secret);
        mpConfig.setToken(token);
        mpConfig.setAesKey(aesKey);

        WxMpServiceImpl wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(mpConfig);
        return wxMpService;
    }
}
