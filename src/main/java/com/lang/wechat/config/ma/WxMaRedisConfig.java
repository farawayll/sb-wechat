package com.lang.wechat.config.ma;

import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class WxMaRedisConfig extends WxMaDefaultConfigImpl {

    private final static String ACCESS_TOKEN_KEY = "wx_access_token_";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public String getAccessToken() {
        String urlString = "http://api.woodylibrary.com/api/v3/mina/getAccessToken";
        String dataStr = HttpUtil.post(urlString, new HashMap<>());
        JSONObject jsonObject = JSONUtil.parseObj(dataStr);
        String accessToken = jsonObject.getStr("access_token");
        return accessToken;
//		return redisTemplate.opsForValue().get(ACCESS_TOKEN_KEY.concat(appid));
    }

    @Override
    public boolean isAccessTokenExpired() {
        return false;
//		return redisTemplate.getExpire(ACCESS_TOKEN_KEY.concat(appid), TimeUnit.SECONDS) < 2L;
    }

//	@Override
//	public synchronized void updateAccessToken(String accessToken, int expiresInSeconds) {
//		redisTemplate.opsForValue().set(ACCESS_TOKEN_KEY.concat(appid), accessToken);
//		redisTemplate.expire(ACCESS_TOKEN_KEY.concat(appid), expiresInSeconds - 200, TimeUnit.SECONDS);
//	}
//
//	@Override
//	public void expireAccessToken() {
//		redisTemplate.expire(ACCESS_TOKEN_KEY.concat(appid), 0, TimeUnit.SECONDS);
//	}
}
