package com.lang.wechat.config.wxproperties;

import lombok.Data;

import java.io.Serializable;

/**
 * 仅在使用Jedis时生效
 */
@Data
public class JedisProperties implements Serializable {

	private static final long serialVersionUID = 1L;

	// 以下均可自行指定，不指定默认从spring容器中获取
	/**
	 * 主机地址.
	 */
	private String host;

	/**
	 * 端口号.
	 */
	private int port;

	/**
	 * 密码.
	 */
	private String password;

	/**
	 * 超时.
	 */
	private int timeout;

	/**
	 * 数据库.
	 */
	private int database;

	private Integer maxActive;
	private Integer maxIdle;
	private Integer maxWaitMillis;
	private Integer minIdle;
}
