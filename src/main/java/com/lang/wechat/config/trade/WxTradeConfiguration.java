package com.lang.wechat.config.trade;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信支付配置类
 */
@Configuration
public class WxTradeConfiguration {

    /**
     * appId
     */
    @Value("${wechat.pay.appId}")
    private String appId;

    /**
     * 商户号
     */
    @Value("${wechat.pay.mchId}")
    private String mchId;

    /**
     * 商户密钥
     */
    @Value("${wechat.pay.mchKey}")
    private String mchKey;

    /**
     * p12密钥
     */
    @Value("${wechat.pay.keyPath}")
    private String keyPath;

    @Bean
    public WxPayService wxPayService() {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(appId);
        payConfig.setMchId(mchId);
        payConfig.setMchKey(mchKey);
        payConfig.setKeyPath(keyPath);

        WxPayServiceImpl wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        return wxPayService;
    }

}

