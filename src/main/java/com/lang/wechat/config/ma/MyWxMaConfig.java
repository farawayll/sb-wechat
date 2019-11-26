package com.lang.wechat.config.ma;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 小程序模板消息配置类
 */
@Configuration
public class MyWxMaConfig {

	@Value("${wx.ma.appId}")
	private String appId;

	@Value("${wx.ma.secret}")
	private String secret;

	@Value("${wx.ma.token}")
	private String token;

	@Value("${wx.ma.aesKey}")
	private String aesKey;

	@Value("${wx.ma.msgDataFormat}")
	private String msgDataFormat;

	@Autowired
	private MyWxMaRedisConfig maRedisConfig;

	@Bean
	public WxMaService wxMaService() {
		maRedisConfig.setAppid(appId);
		maRedisConfig.setSecret(secret);
		maRedisConfig.setToken(token);
		maRedisConfig.setAesKey(aesKey);
		maRedisConfig.setMsgDataFormat(msgDataFormat);

		WxMaServiceImpl wxMaService = new WxMaServiceImpl();
		wxMaService.setWxMaConfig(maRedisConfig);
		return wxMaService;
	}
}
