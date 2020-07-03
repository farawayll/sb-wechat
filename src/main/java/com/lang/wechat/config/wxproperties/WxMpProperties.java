package com.lang.wechat.config.wxproperties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;


/**
 * 微信接入相关配置属性.
 *
 * @author someone
 */
@Data
@ConfigurationProperties(prefix = "wx.mp")
public class WxMpProperties implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 设置微信公众号的appid.
	 */
	private String appId;

	/**
	 * 设置微信公众号的app secret.
	 */
	private String secret;

	/**
	 * 设置微信公众号的token.
	 */
	private String token;

	/**
	 * 设置微信公众号的EncodingAESKey.
	 */
	private String aesKey;

	/**
	 * 存储策略
	 */
	private StorageProperties configStorage = new StorageProperties();

}
