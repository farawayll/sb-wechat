package com.lang.wechat.config.wxproperties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * 属性配置类.
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 * @date 2019-08-10
 */
@Data
@ConfigurationProperties(prefix = "wx.ma")
public class WxMaProperties implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 设置微信小程序的appId.
	 */
	private String appId;

	/**
	 * 设置微信小程序的Secret.
	 */
	private String secret;

	/**
	 * 设置微信小程序消息服务器配置的token.
	 */
	private String token;

	/**
	 * 设置微信小程序消息服务器配置的EncodingAESKey.
	 */
	private String aesKey;

	/**
	 * 消息格式，XML或者JSON.
	 */
	private String msgDataFormat;

	/**
	 * 存储策略
	 */
	private final StorageProperties configStorage = new StorageProperties();

}
