package com.lang.wechat.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 预支付交易会话标识对象
 */
@Data
public class WxPayPrepared implements Serializable {

    private static final long serialVersionUID = 6767078871950017099L;

    private String appId;

    private String timeStamp;

    private String nonceStr;

    private String payPackage;

    private String signType;

    private String paySign;

}