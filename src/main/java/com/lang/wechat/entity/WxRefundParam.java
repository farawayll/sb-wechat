package com.lang.wechat.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信退款申请参数
 */
@Data
public class WxRefundParam implements Serializable {

    private static final long serialVersionUID = 8013852287918156122L;

    private String orderNum;

    private String refundNum;

    private int totalFee;

    private int refundFee;

    private String refundDesc;

    private String notifyUrl;
}
