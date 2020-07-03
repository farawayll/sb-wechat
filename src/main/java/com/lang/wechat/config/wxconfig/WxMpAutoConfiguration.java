package com.lang.wechat.config.wxconfig;

import com.lang.wechat.config.wxenums.RedisPreKeyType;
import com.lang.wechat.config.wxproperties.StorageProperties;
import com.lang.wechat.config.wxproperties.JedisProperties;
import com.lang.wechat.config.wxproperties.WxMpProperties;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.redis.JedisWxRedisOps;
import me.chanjar.weixin.common.redis.RedisTemplateWxRedisOps;
import me.chanjar.weixin.common.redis.WxRedisOps;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceHttpClientImpl;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.api.impl.WxMpServiceJoddHttpImpl;
import me.chanjar.weixin.mp.api.impl.WxMpServiceOkHttpImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import me.chanjar.weixin.mp.config.impl.WxMpRedisConfigImpl;
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
 * 微信公众号相关服务自动注册.
 */
@Configuration
@AllArgsConstructor
@ConditionalOnClass(WxMpService.class)
@EnableConfigurationProperties(WxMpProperties.class)
@ConditionalOnProperty(prefix = "wx.mp", value = "enabled", matchIfMissing = true)
public class WxMpAutoConfiguration {

	private final WxMpProperties wxMpProperties;
	private final ApplicationContext applicationContext;

	@Bean
	@ConditionalOnMissingBean(WxMpService.class)
	public WxMpService wxMpService(WxMpConfigStorage wxMpConfig) {
		WxMpService wxMpService;
		switch (wxMpProperties.getConfigStorage().getHttpClientType()) {
			case OkHttp:
				wxMpService = new WxMpServiceOkHttpImpl();
				break;
			case JoddHttp:
				wxMpService = new WxMpServiceJoddHttpImpl();
				break;
			case HttpClient:
				wxMpService = new WxMpServiceHttpClientImpl();
				break;
			default:
				wxMpService = new WxMpServiceImpl();
				break;
		}
		wxMpService.setWxMpConfigStorage(wxMpConfig);
		return wxMpService;
	}

	@Bean
	@ConditionalOnMissingBean(WxMpConfigStorage.class)
	public WxMpConfigStorage wxMpConfigStorage() {
		WxMpConfigStorage wxMpConfig;
		switch (wxMpProperties.getConfigStorage().getType()) {
			case Jedis:
				wxMpConfig = wxMpInJedisConfigStorage();
				break;
			case RedisTemplate:
				wxMpConfig = wxMpInRedisTemplateConfigStorage();
				break;
			default:
				wxMpConfig = wxMpInMemoryConfigStorage();
				break;
		}
		return wxMpConfig;
	}

	// 缓存
	private WxMpConfigStorage wxMpInMemoryConfigStorage() {
		WxMpDefaultConfigImpl wxMpDefaultConfig = new WxMpDefaultConfigImpl();
		setWxMpInfo(wxMpDefaultConfig);
		return wxMpDefaultConfig;
	}

	// Jedis
	private WxMpConfigStorage wxMpInJedisConfigStorage() {
		JedisProperties redisProperties = wxMpProperties.getConfigStorage().getRedis();
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
		WxMpRedisConfigImpl wxMpRedisConfig = new WxMpRedisConfigImpl(redisOps, RedisPreKeyType.MP.getPreKey());
		setWxMpInfo(wxMpRedisConfig);
		return wxMpRedisConfig;
	}

	// RedisTemplate
	private WxMpConfigStorage wxMpInRedisTemplateConfigStorage() {
		StringRedisTemplate redisTemplate = applicationContext.getBean(StringRedisTemplate.class);
		WxRedisOps redisOps = new RedisTemplateWxRedisOps(redisTemplate);
		WxMpRedisConfigImpl wxMpRedisConfig = new WxMpRedisConfigImpl(redisOps, RedisPreKeyType.MP.getPreKey());
		setWxMpInfo(wxMpRedisConfig);
		return wxMpRedisConfig;
	}

	private void setWxMpInfo(WxMpDefaultConfigImpl config) {
		WxMpProperties properties = wxMpProperties;
		StorageProperties storageProperties = properties.getConfigStorage();
		config.setAppId(properties.getAppId());
		config.setSecret(properties.getSecret());
		config.setToken(properties.getToken());
		config.setAesKey(properties.getAesKey());

		config.setHttpProxyHost(storageProperties.getHttpProxyHost());
		config.setHttpProxyUsername(storageProperties.getHttpProxyUsername());
		config.setHttpProxyPassword(storageProperties.getHttpProxyPassword());
		if (storageProperties.getHttpProxyPort() != null) {
			config.setHttpProxyPort(storageProperties.getHttpProxyPort());
		}
	}

	//@Bean
	//@Deprecated
	//public WxMpKefuService wxMpKefuService(WxMpService wxMpService) {
	//	return wxMpService.getKefuService();
	//}
	//
	//@Bean
	//@Deprecated
	//public WxMpMaterialService wxMpMaterialService(WxMpService wxMpService) {
	//	return wxMpService.getMaterialService();
	//}
	//
	//@Bean
	//@Deprecated
	//public WxMpMenuService wxMpMenuService(WxMpService wxMpService) {
	//	return wxMpService.getMenuService();
	//}
	//
	//@Bean
	//@Deprecated
	//public WxMpUserService wxMpUserService(WxMpService wxMpService) {
	//	return wxMpService.getUserService();
	//}
	//
	//@Bean
	//@Deprecated
	//public WxMpUserTagService wxMpUserTagService(WxMpService wxMpService) {
	//	return wxMpService.getUserTagService();
	//}
	//
	//@Bean
	//@Deprecated
	//public WxMpQrcodeService wxMpQrcodeService(WxMpService wxMpService) {
	//	return wxMpService.getQrcodeService();
	//}
	//
	//@Bean
	//@Deprecated
	//public WxMpCardService wxMpCardService(WxMpService wxMpService) {
	//	return wxMpService.getCardService();
	//}
	//
	//@Bean
	//@Deprecated
	//public WxMpDataCubeService wxMpDataCubeService(WxMpService wxMpService) {
	//	return wxMpService.getDataCubeService();
	//}
	//
	//@Bean
	//@Deprecated
	//public WxMpUserBlacklistService wxMpUserBlacklistService(WxMpService wxMpService) {
	//	return wxMpService.getBlackListService();
	//}
	//
	//@Bean
	//@Deprecated
	//public WxMpStoreService wxMpStoreService(WxMpService wxMpService) {
	//	return wxMpService.getStoreService();
	//}
	//
	//@Bean
	//@Deprecated
	//public WxMpTemplateMsgService wxMpTemplateMsgService(WxMpService wxMpService) {
	//	return wxMpService.getTemplateMsgService();
	//}
	//
	//@Bean
	//@Deprecated
	//public WxMpSubscribeMsgService wxMpSubscribeMsgService(WxMpService wxMpService) {
	//	return wxMpService.getSubscribeMsgService();
	//}
	//
	//@Bean
	//@Deprecated
	//public WxMpDeviceService wxMpDeviceService(WxMpService wxMpService) {
	//	return wxMpService.getDeviceService();
	//}
	//
	//@Bean
	//@Deprecated
	//public WxMpShakeService wxMpShakeService(WxMpService wxMpService) {
	//	return wxMpService.getShakeService();
	//}
	//
	//@Bean
	//@Deprecated
	//public WxMpMemberCardService wxMpMemberCardService(WxMpService wxMpService) {
	//	return wxMpService.getMemberCardService();
	//}
	//
	//@Bean
	//@Deprecated
	//public WxMpMassMessageService wxMpMassMessageService(WxMpService wxMpService) {
	//	return wxMpService.getMassMessageService();
	//}
	//
	//@Bean
	//@Deprecated
	//public WxMpAiOpenService wxMpAiOpenService(WxMpService wxMpService) {
	//	return wxMpService.getAiOpenService();
	//}
	//
	//@Bean
	//@Deprecated
	//public WxMpWifiService wxMpWifiService(WxMpService wxMpService) {
	//	return wxMpService.getWifiService();
	//}
	//
	//@Bean
	//@Deprecated
	//public WxMpMarketingService wxMpMarketingService(WxMpService wxMpService) {
	//	return wxMpService.getMarketingService();
	//}
	//
	//@Bean
	//@Deprecated
	//public WxMpCommentService wxMpCommentService(WxMpService wxMpService) {
	//	return wxMpService.getCommentService();
	//}
	//
	//@Bean
	//@Deprecated
	//public WxMpOcrService wxMpOcrService(WxMpService wxMpService) {
	//	return wxMpService.getOcrService();
	//}

}
