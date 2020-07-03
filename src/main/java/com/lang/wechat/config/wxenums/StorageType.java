package com.lang.wechat.config.wxenums;

public enum StorageType {
	/**
	 * 内存.
	 */
	Memory,
	/**
	 * redis(JedisClient).
	 */
	Jedis,
	/**
	 * redis(RedisTemplate).
	 */
	RedisTemplate
}
