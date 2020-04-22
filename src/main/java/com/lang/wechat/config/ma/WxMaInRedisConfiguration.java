package com.lang.wechat.config.ma;

import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class WxMaInRedisConfiguration extends WxMaDefaultConfigImpl {

    private final static String MA_ACCESS_TOKEN = "wx:ma:access_token";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取access_token
     */
    @Override
    public String getAccessToken() {
        return stringRedisTemplate.opsForValue().get(MA_ACCESS_TOKEN);
    }

    /**
     * access_token是否过期 提前5秒
     */
    @Override
    public boolean isAccessTokenExpired() {
        return stringRedisTemplate.getExpire(MA_ACCESS_TOKEN) < 5L;
    }

    /**
     * 如果过期则更新access_token 并设置过期时间为2小时(微信官方的access_token过期时间为2小时,并且wxJava已实现)
     */
    @Override
    public synchronized void updateAccessToken(String accessToken, int expiresInSeconds) {
        stringRedisTemplate.opsForValue().set(MA_ACCESS_TOKEN, accessToken);
        stringRedisTemplate.expire(MA_ACCESS_TOKEN, expiresInSeconds, TimeUnit.SECONDS);
    }

    /**
     * 强制过期access_token
     */
    @Override
    public void expireAccessToken() {
        stringRedisTemplate.expire(MA_ACCESS_TOKEN, 0, TimeUnit.SECONDS);
    }
}
