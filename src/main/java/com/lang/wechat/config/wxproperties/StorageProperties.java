package com.lang.wechat.config.wxproperties;

import com.lang.wechat.config.wxenums.HttpClientType;
import com.lang.wechat.config.wxenums.StorageType;
import lombok.Data;

import java.io.Serializable;

@Data
public class StorageProperties implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 存储类型.
	 */
	private StorageType type = StorageType.RedisTemplate;

	/**
	 * redis连接配置.仅在使用Jedis时生效
	 */
	private final JedisProperties redis = new JedisProperties();

	/**
	 * http客户端类型.
	 */
	private HttpClientType httpClientType = HttpClientType.HttpClient;

	/**
	 * http代理主机.
	 */
	private String httpProxyHost;

	/**
	 * http代理端口.
	 */
	private Integer httpProxyPort;

	/**
	 * http代理用户名.
	 */
	private String httpProxyUsername;

	/**
	 * http代理密码.
	 */
	private String httpProxyPassword;
}
