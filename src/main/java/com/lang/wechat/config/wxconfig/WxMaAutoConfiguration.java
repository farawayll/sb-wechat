package com.lang.wechat.config.wxconfig;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceHttpClientImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceJoddHttpImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceOkHttpImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaRedisBetterConfigImpl;
import com.lang.wechat.config.wxenums.RedisPreKeyType;
import com.lang.wechat.config.wxproperties.StorageProperties;
import com.lang.wechat.config.wxproperties.JedisProperties;
import com.lang.wechat.config.wxproperties.WxMaProperties;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.redis.JedisWxRedisOps;
import me.chanjar.weixin.common.redis.RedisTemplateWxRedisOps;
import me.chanjar.weixin.common.redis.WxRedisOps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 微信小程序相关服务自动注册.
 */
@Configuration
@AllArgsConstructor
@ConditionalOnClass(WxMaService.class)
@EnableConfigurationProperties(WxMaProperties.class)
@ConditionalOnProperty(prefix = "wx.ma", value = "enabled", matchIfMissing = true)
public class WxMaAutoConfiguration {

	private final WxMaProperties wxMaProperties;
	private final ApplicationContext applicationContext;

	/**
	 * 小程序service.
	 *
	 * @return 小程序service
	 */
	@Bean
	@ConditionalOnMissingBean(WxMaService.class)
	public WxMaService service(WxMaConfig wxMaConfig) {
		WxMaService wxMaService;
		switch (wxMaProperties.getConfigStorage().getHttpClientType()) {
			case OkHttp:
				wxMaService = new WxMaServiceOkHttpImpl();
				break;
			case JoddHttp:
				wxMaService = new WxMaServiceJoddHttpImpl();
				break;
			case HttpClient:
				wxMaService = new WxMaServiceHttpClientImpl();
				break;
			default:
				wxMaService = new WxMaServiceImpl();
				break;
		}
		wxMaService.setWxMaConfig(wxMaConfig);
		return wxMaService;
	}

	@Bean
	@ConditionalOnMissingBean(WxMaConfig.class)
	public WxMaConfig wxMaConfig() {
		WxMaDefaultConfigImpl wxMaConfig;
		switch (wxMaProperties.getConfigStorage().getType()) {
			case Jedis:
				wxMaConfig = wxMaJedisConfigStorage();
				break;
			case RedisTemplate:
				wxMaConfig = wxMaRedisTemplateConfigStorage();
				break;
			default:
				wxMaConfig = wxMaDefaultConfigStorage();
				break;
		}
		return wxMaConfig;
	}

	// 缓存
	private WxMaDefaultConfigImpl wxMaDefaultConfigStorage() {
		WxMaDefaultConfigImpl wxMaDefaultConfig = new WxMaDefaultConfigImpl();
		setWxMaInfo(wxMaDefaultConfig);
		return wxMaDefaultConfig;
	}

	// Jedis
	private WxMaDefaultConfigImpl wxMaJedisConfigStorage() {
		JedisProperties redisProperties = wxMaProperties.getConfigStorage().getRedis();
		JedisPool jedisPool;
		if (StringUtils.isNotEmpty(redisProperties.getHost())) {
			JedisPoolConfig config = new JedisPoolConfig();
			if (redisProperties.getMaxActive() != null) {
				config.setMaxTotal(redisProperties.getMaxActive());
			}
			if (redisProperties.getMaxIdle() != null) {
				config.setMaxIdle(redisProperties.getMaxIdle());
			}
			if (redisProperties.getMaxWaitMillis() != null) {
				config.setMaxWaitMillis(redisProperties.getMaxWaitMillis());
			}
			if (redisProperties.getMinIdle() != null) {
				config.setMinIdle(redisProperties.getMinIdle());
			}
			config.setTestOnBorrow(true);
			config.setTestWhileIdle(true);
			jedisPool = new JedisPool(config, redisProperties.getHost(), redisProperties.getPort(),
					redisProperties.getTimeout(), redisProperties.getPassword(), redisProperties.getDatabase());
		} else {
			jedisPool = applicationContext.getBean(JedisPool.class);
		}
		WxRedisOps redisOps = new JedisWxRedisOps(jedisPool);
		WxMaRedisBetterConfigImpl wxMaRedisBetterConfig = new WxMaRedisBetterConfigImpl(redisOps, RedisPreKeyType.MA.getPreKey());
		setWxMaInfo(wxMaRedisBetterConfig);
		return wxMaRedisBetterConfig;
	}

	// RedisTemplate
	private WxMaDefaultConfigImpl wxMaRedisTemplateConfigStorage() {
		StringRedisTemplate redisTemplate = applicationContext.getBean(StringRedisTemplate.class);
		WxRedisOps redisOps = new RedisTemplateWxRedisOps(redisTemplate);
		WxMaRedisBetterConfigImpl wxMaRedisBetterConfig = new WxMaRedisBetterConfigImpl(redisOps, RedisPreKeyType.MA.getPreKey());
		setWxMaInfo(wxMaRedisBetterConfig);
		return wxMaRedisBetterConfig;
	}

	private void setWxMaInfo(WxMaDefaultConfigImpl config) {
		WxMaProperties properties = wxMaProperties;
		StorageProperties storageProperties = properties.getConfigStorage();
		config.setAppid(StringUtils.trimToNull(properties.getAppId()));
		config.setSecret(StringUtils.trimToNull(properties.getSecret()));
		config.setToken(StringUtils.trimToNull(properties.getToken()));
		config.setAesKey(StringUtils.trimToNull(properties.getAesKey()));

		config.setHttpProxyHost(storageProperties.getHttpProxyHost());
		config.setHttpProxyUsername(storageProperties.getHttpProxyUsername());
		config.setHttpProxyPassword(storageProperties.getHttpProxyPassword());
		if (storageProperties.getHttpProxyPort() != null) {
			config.setHttpProxyPort(storageProperties.getHttpProxyPort());
		}
	}
}
