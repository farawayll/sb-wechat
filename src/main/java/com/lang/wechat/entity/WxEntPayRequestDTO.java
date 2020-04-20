package com.lang.wechat.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信支付统一下单参数
 */
@Data
public class WxEntPayRequestDTO implements Serializable {

    private static final long serialVersionUID = 8013852287918156122L;

    /**
     * 转账单号
     */
    private String partnerTradeNo;

    /**
     * 用户openId
     */
    private String openId;

    /**
     * 退费金额 单位分
     */
    private Integer amount;

    /**
     * 用户IP
     */
    private String spbillCreateIp;

    /**
     * 转账描述
     */
    private String description;

}
