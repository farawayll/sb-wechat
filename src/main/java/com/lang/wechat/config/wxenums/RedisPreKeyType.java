package com.lang.wechat.config.wxenums;

public enum RedisPreKeyType {

	MA("wxma"),
	MP("wxmp");

	private String preKey;

	RedisPreKeyType(String preKey) {
		this.preKey = preKey;
	}

	public String getPreKey() {
		return preKey;
	}

	public void setPreKey(String preKey) {
		this.preKey = preKey;
	}
}
