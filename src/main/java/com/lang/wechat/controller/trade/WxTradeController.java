package com.lang.wechat.controller.trade;

import cn.hutool.json.JSONUtil;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.lang.wechat.entity.WxPayParam;
import com.lang.wechat.entity.WxPayPrepared;
import com.lang.wechat.entity.WxRefundParam;
import com.lang.wechat.util.R;
import com.lang.wechat.util.StatusEnum;
import com.lang.wechat.util.TradeConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.github.binarywang.wxpay.constant.WxPayConstants.TradeType.JSAPI;

/**
 * 微信支付(代码测试通过,配置好参数之后复制粘贴即可使用)
 */
@RestController
@Slf4j
public class WxTradeController {

    @Autowired
    private WxPayService wxPayService;

    @Value("${wechat.pay.mchKey}")
    private String mchKey;

    /**
     * 预支付
     */
    @PostMapping("/unifiedOrder")
    public R unifiedOrder(@RequestBody WxPayParam wxPayParam) {
        try {
            /**
             * 预支付对象的参数，以下都是必传参数。JSAPI支付和小程序支付必传"JSAPI"和openId
             */
            WxPayUnifiedOrderRequest request = new WxPayUnifiedOrderRequest();
            request.setTradeType(JSAPI);
            request.setOpenid(wxPayParam.getOpenid());
            request.setNonceStr(wxPayParam.getNonceStr());
            request.setOutTradeNo(wxPayParam.getOutTradeNo());
            request.setBody(wxPayParam.getBody());
            request.setTotalFee(wxPayParam.getTotalFee());
            request.setSpbillCreateIp(wxPayParam.getSpbillCreateIp());
            request.setNotifyUrl(wxPayParam.getNotifyUrl());
            request.setAttach(wxPayParam.getAttach());
            /**
             * 获取预支付对象
             * 1.WxJavaSDK源码会根据上面的预支付参数通过MD5算法帮我们生成sign,然后一并将数据传至微信服务器;
             * 2.数据包到微信服务器后，它也会数据包中的预支付参数生成sign,然后与传过去的数据包中的sign对比是否一致,防止数据篡改
             */
            WxPayMpOrderResult payResult = wxPayService.createOrder(request);
            /**
             * 返回给前端预支付交易会话表示,以下都是预支付对象必备数据
             * //0.微信分配的小程序ID(前端保存的有，如果没有从后端返回)
             * 1.时间戳从1970年1月1日00:00:00至今的秒数,即当前的时间
             * 2.随机字符串，不长于32位
             * 3.数据包：统一下单接口返回的 prepay_id 参数值，提交格式如：prepay_id=wx2017033010242291fcfe0db70013231072
             * 4.签名类型，默认为MD5
             * 5.签名：WxJavaSDK源码生成签名返回到前端，前端带着签名去支付，防止数据篡改
             */
            WxPayPrepared wxPayPrepared = new WxPayPrepared();
            wxPayPrepared.setAppId(payResult.getAppId());
            wxPayPrepared.setTimeStamp(String.valueOf(System.currentTimeMillis() / 1000L));
            wxPayPrepared.setNonceStr(payResult.getNonceStr());
            wxPayPrepared.setPayPackage(payResult.getPackageValue());
            wxPayPrepared.setSignType(payResult.getSignType());
            wxPayPrepared.setPaySign(payResult.getPaySign());
            return R.ok(wxPayPrepared);
        } catch (WxPayException e) {
            log.error("获取微信预支付对象失败！", e);
            return R.error(StatusEnum.FAIL);
        }
    }

    /**
     * 支付回调
     */
    @PostMapping("/payNotify")
    public String payNotify(@RequestBody String xmlData) {
        try {
            // 以下2选1
            //WxPayOrderNotifyResult wxPayOrderNotifyResult = wxPayService.parseOrderNotifyResult(xmlData);
            WxPayOrderNotifyResult wxPayOrderNotifyResult = WxPayOrderNotifyResult.fromXML(xmlData);
            log.info("支付回调：{}", JSONUtil.toJsonPrettyStr(wxPayOrderNotifyResult));
            if ("SUCCESS".equals(wxPayOrderNotifyResult.getReturnCode())
                    && "SUCCESS".equals(wxPayOrderNotifyResult.getResultCode())) {
                return TradeConstant.SUCCESS;
            }
        } catch (Exception e) {
            log.error("接受支付回调失败！", e);
        }
        return TradeConstant.FAIL;
    }

    /**
     * 退款(代码测试通过)
     */
    @PostMapping("/refund")
    public R refund(WxRefundParam wxRefundParam) {
        try {
            WxPayRefundRequest request = new WxPayRefundRequest();
            request.setOutTradeNo(wxRefundParam.getOrderNum());
            request.setOutRefundNo(wxRefundParam.getRefundNum());
            request.setRefundFee(wxRefundParam.getRefundFee());
            request.setTotalFee(wxRefundParam.getRefundFee());
            request.setRefundDesc(wxRefundParam.getRefundDesc());
            request.setNotifyUrl(wxRefundParam.getNotifyUrl());
            WxPayRefundResult wxPayRefundResult = wxPayService.refund(request);
            log.info("退款详情：{}", JSONUtil.toJsonPrettyStr(wxPayRefundResult));
            return R.ok();
        } catch (WxPayException e) {
            log.error("退款失败！", e);
            return R.error(StatusEnum.FAIL);
        }
    }

    /**
     * 退款回调
     */
    @PostMapping("/refundNotify")
    public String wxRefundNotify(@RequestBody String xmlData) {
        try {
            // 以下2选1
            //WxPayRefundNotifyResult wxPayRefundNotifyResult = wxPayService.parseRefundNotifyResult(xmlData);
            WxPayRefundNotifyResult wxPayRefundNotifyResult = WxPayRefundNotifyResult.fromXML(xmlData, mchKey);
            log.info("退款回调：{}", JSONUtil.toJsonPrettyStr(wxPayRefundNotifyResult));
            if ("SUCCESS".equals(wxPayRefundNotifyResult.getReturnCode())) {
                return TradeConstant.SUCCESS;
            }
        } catch (WxPayException e) {
            log.error("接受退款回调失败！", e);
        }
        return TradeConstant.FAIL;
    }
}
