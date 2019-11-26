package com.lang.wechat.config.mp;

import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class MyWxMpRedisConfig extends WxMpDefaultConfigImpl {

    private final static String MP_ACCESS_TOKEN = "wx:mp:access_token";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取token
     */
    @Override
    public String getAccessToken() {
        return stringRedisTemplate.opsForValue().get(MP_ACCESS_TOKEN);
    }

    /**
     * token是否过期
     * 提前5秒
     */
    @Override
    public boolean isAccessTokenExpired() {
        return stringRedisTemplate.getExpire(MP_ACCESS_TOKEN) < 5L;
    }

    /**
     * 更新token 并设置过期时间
     * 微信官方的access_token过期时间为2小时
     */
    @Override
    public synchronized void updateAccessToken(String accessToken, int expiresInSeconds) {
        stringRedisTemplate.opsForValue().set(MP_ACCESS_TOKEN, accessToken);
        stringRedisTemplate.expire(MP_ACCESS_TOKEN, expiresInSeconds, TimeUnit.SECONDS);
    }

    /**
     * 强制过期token
     */
    @Override
    public void expireAccessToken() {
        stringRedisTemplate.expire(MP_ACCESS_TOKEN, 0, TimeUnit.SECONDS);
    }
}
