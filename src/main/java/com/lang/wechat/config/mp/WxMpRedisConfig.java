package com.lang.wechat.config.mp;

import cn.hutool.http.HttpUtil;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class WxMpRedisConfig extends WxMpDefaultConfigImpl {


    private final static String ACCESS_TOKEN_KEY = "wx_access_token_";

    @Autowired
	private StringRedisTemplate redisTemplate;

	@Override
	public String getAccessToken() {
		// todo：暂时从V3接口中获取accesstoken
		return HttpUtil.get("http://api.woodylibrary.com/api/v3/mp/push/getAccessToken");
//		return redisTemplate.opsForValue().get(ACCESS_TOKEN_KEY.concat(appId));
	}

	@Override
	public boolean isAccessTokenExpired() {
		return false;
//		return redisTemplate.getExpire(ACCESS_TOKEN_KEY.concat(appId), TimeUnit.SECONDS) < 2L;
	}

//	@Override
//	public synchronized void updateAccessToken(String accessToken, int expiresInSeconds) {
//		redisTemplate.opsForValue().set(ACCESS_TOKEN_KEY.concat(appId), accessToken);
//		redisTemplate.expire(ACCESS_TOKEN_KEY.concat(appId), expiresInSeconds - 200, TimeUnit.SECONDS);
//	}
//
//	@Override
//	public void expireAccessToken() {
//		redisTemplate.expire(ACCESS_TOKEN_KEY.concat(appId), 0, TimeUnit.SECONDS);
//	}
}
