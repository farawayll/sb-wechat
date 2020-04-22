package com.lang.wechat.config.mp;

import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class WxMpInRedisConfiguration extends WxMpDefaultConfigImpl {

    private final static String MP_ACCESS_TOKEN = "wx:mp:access_token";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取access_token
     */
    @Override
    public String getAccessToken() {
        return stringRedisTemplate.opsForValue().get(MP_ACCESS_TOKEN);
    }

    /**
     * access_token是否过期 提前5秒
     */
    @Override
    public boolean isAccessTokenExpired() {
        return stringRedisTemplate.getExpire(MP_ACCESS_TOKEN) < 5L;
    }

    /**
     * 如果过期则更新access_token 并设置过期时间为2小时(微信官方的access_token过期时间为2小时,并且wxJava已实现)
     */
    @Override
    public synchronized void updateAccessToken(String accessToken, int expiresInSeconds) {
        stringRedisTemplate.opsForValue().set(MP_ACCESS_TOKEN, accessToken);
        stringRedisTemplate.expire(MP_ACCESS_TOKEN, expiresInSeconds, TimeUnit.SECONDS);
    }

    /**
     * 强制过期access_token
     */
    @Override
    public void expireAccessToken() {
        stringRedisTemplate.expire(MP_ACCESS_TOKEN, 0, TimeUnit.SECONDS);
    }
}
