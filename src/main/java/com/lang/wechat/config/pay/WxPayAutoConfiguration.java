package com.lang.wechat.config.pay;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信交易相关的配置类
 */
@Configuration
public class WxPayAutoConfiguration {

    /**
     * 微信公众号或者小程序等的appId
     */
    @Value("${wx.pay.appId}")
    private String appId;

    /**
     * 微信支付商户号
     */
    @Value("${wx.pay.mchId}")
    private String mchId;

    /**
     * 微信支付商户密钥
     */
    @Value("${wx.pay.mchKey}")
    private String mchKey;

    /**
     * p12证书的位置
     */
    @Value("${wx.pay.keyPath}")
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

