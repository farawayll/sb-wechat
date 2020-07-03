package com.lang.wechat.config.wxconfig;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.lang.wechat.config.wxproperties.WxPayProperties;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信支付自动配置
 */
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(WxPayProperties.class)
@ConditionalOnClass(WxPayService.class)
@ConditionalOnProperty(prefix = "wx.pay", value = "enabled", matchIfMissing = true)
public class WxPayAutoConfiguration {

	private final WxPayProperties properties;

	/**
	 * 构造微信支付服务对象.
	 *
	 * @return 微信支付service
	 */
	@Bean
	@ConditionalOnMissingBean(WxPayService.class)
	public WxPayService wxPayService() {
		final WxPayService wxPayService = new WxPayServiceImpl();
		WxPayConfig payConfig = new WxPayConfig();
		payConfig.setAppId(StringUtils.trimToNull(this.properties.getAppId()));
		payConfig.setMchId(StringUtils.trimToNull(this.properties.getMchId()));
		payConfig.setMchKey(StringUtils.trimToNull(this.properties.getMchKey()));
		//payConfig.setSubAppId(StringUtils.trimToNull(this.properties.getSubAppId()));
		//payConfig.setSubMchId(StringUtils.trimToNull(this.properties.getSubMchId()));
		payConfig.setKeyPath(StringUtils.trimToNull(this.properties.getKeyPath()));
		//以下是apiv3以及支付分相关
		payConfig.setServiceId(StringUtils.trimToNull(this.properties.getServiceId()));
		payConfig.setPayScoreNotifyUrl(StringUtils.trimToNull(this.properties.getPayScoreNotifyUrl()));
		payConfig.setPrivateKeyPath(StringUtils.trimToNull(this.properties.getPrivateKeyPath()));
		payConfig.setPrivateCertPath(StringUtils.trimToNull(this.properties.getPrivateCertPath()));
		payConfig.setCertSerialNo(StringUtils.trimToNull(this.properties.getCertSerialNo()));
		payConfig.setApiV3Key(StringUtils.trimToNull(this.properties.getApiv3Key()));

		wxPayService.setConfig(payConfig);
		return wxPayService;
	}

}
