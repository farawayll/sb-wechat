package com.lang.wechat.config.mp;

import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class MyWxMpRedisConfig extends WxMpDefaultConfigImpl {

	private final static String ACCESS_TOKEN_KEY = "wx:access:token:";

	@Autowired
	private StringRedisTemplate redisTemplate;

	/**
	 * 获取token
	 *
	 * @return String
	 */
	@Override
	public String getAccessToken() {
		return redisTemplate.opsForValue().get(ACCESS_TOKEN_KEY);
	}

	/**
	 * token是否过期
	 * redisTemplate.getExpire(key)的值单位默认为秒
	 * 提前5秒
	 *
	 * @return Boolean
	 */
	@Override
	public boolean isAccessTokenExpired() {
		return redisTemplate.getExpire(ACCESS_TOKEN_KEY) < 5L;
	}

	/**
	 * 更新token 并设置过期时间
	 * 正常情况，微信官方的accesstoken过期时间为2小时，这里缩短5秒
	 * @param accessToken
	 * @param expiresInSeconds
	 */
	@Override
	public synchronized void updateAccessToken(String accessToken, int expiresInSeconds) {
		redisTemplate.opsForValue().set(ACCESS_TOKEN_KEY, accessToken);
		redisTemplate.expire(ACCESS_TOKEN_KEY, expiresInSeconds - 5, TimeUnit.SECONDS);
	}

	/**
	 * 强制过期token
	 */
	@Override
	public void expireAccessToken() {
		redisTemplate.expire(ACCESS_TOKEN_KEY, 0, TimeUnit.SECONDS);
	}
}
