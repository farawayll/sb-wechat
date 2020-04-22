package com.lang.wechat.controller.pay;

import cn.hutool.json.JSONUtil;
import com.github.binarywang.wxpay.bean.entpay.EntPayRequest;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.lang.wechat.entity.WxEntPayEntity;
import com.lang.wechat.entity.WxPayEntity;
import com.lang.wechat.entity.WxRefundEntity;
import com.lang.wechat.util.R;
import com.lang.wechat.util.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信支付(代码测试通过,配置好参数之后复制粘贴即可使用)
 */
@RestController
@Slf4j
public class WxPayController {

    @Autowired
    private WxPayService wxPayService;

    /**
     * 支付(获取支付的预支付对象)
     */
    @PostMapping("/createOrder")
    public R createOrder(@RequestBody WxPayEntity wxPayEntity) {
        try {
            /**
             * 获取预支付对象的参数，以下都是必传参数。
             * NATIVE:原生扫码支付。对应的wxPayService.createOrder(request)的结果是:WxPayNativeOrderResult。
             * APP:APP支付。对应的wxPayService.createOrder(request)的结果是:WxPayAppOrderResult。
             * JSAPI:公众号支付/小程序支付。对应的wxPayService.createOrder(request)的结果是:WxPayMpOrderResult。
             * MWEB:H5支付。对应的wxPayService.createOrder(request)的结果是:WxPayMwebOrderResult。
             */
            WxPayUnifiedOrderRequest request = WxPayUnifiedOrderRequest.newBuilder()
                    .body(wxPayEntity.getBody()) // 支付内容
                    .totalFee(wxPayEntity.getTotalFee()) // 支付金额
                    .spbillCreateIp(wxPayEntity.getSpbillCreateIp()) // 用户ip地址
                    .notifyUrl(wxPayEntity.getNotifyUrl()) // 回调地址
                    .tradeType(WxPayConstants.TradeType.JSAPI) // 交易类型
                    .openid(wxPayEntity.getOpenid()) // 用户openid
                    .outTradeNo(wxPayEntity.getOutTradeNo()) // 支付订单号
                    .build();
            /**
             * 获取预支付对象并返回给前端。
             * 1.根据以上数据包通过MD5算法生成sign签名(WxJava已替我们完成),然后将数据包和sign签名一并传至微信服务器。
             * 2.数据包和sign签名到微信服务器后，微信服务器会再次将数据包通过MD5算法生成sign签名,然后与传过去的sign签名比对是否一致,防止数据篡改。
             */
            // 公众号支付/小程序支付的result对象
            WxPayMpOrderResult payResult = wxPayService.createOrder(request);
            /**
             * 预支付对象字段说明。
             * 1.appId：微信分配的小程序ID(前端有，如果没有从后端返回)。
             * 2.timeStamp：时间戳从1970年1月1日00:00:00至今的秒数,即当前的时间。
             * 3.nonceStr：随机字符串，不长于32位。
             * 4.packageValue(package)：数据包，统一下单接口返回的 prepay_id 参数值，提交格式如：prepay_id=wx2017033010242291f0db70013231072。
             * 5.signType：签名类型，默认为MD5。
             * 6.paySign：签名，WxJavaSDK源码生成签名返回到前端，前端带着签名去支付，防止数据篡改。
             */
            return R.ok(payResult);
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
        WxPayOrderNotifyResult wxPayOrderNotifyResult = null;
        try {
            wxPayOrderNotifyResult = wxPayService.parseOrderNotifyResult(xmlData);
        } catch (Exception e) {
            log.error("接受支付回调失败！", e);
        }
        log.info("支付回调数据：{}", JSONUtil.toJsonPrettyStr(wxPayOrderNotifyResult));
        if ("SUCCESS".equals(wxPayOrderNotifyResult.getReturnCode()) && "SUCCESS".equals(wxPayOrderNotifyResult.getResultCode())) {
            // 处理支付回调逻辑
        }
        return WxPayNotifyResponse.success("成功！");
    }

    /**
     * 退款
     */
    @PostMapping("/refund")
    public R refund(WxRefundEntity wxRefundEntity) {
        try {
            WxPayRefundRequest request = WxPayRefundRequest.newBuilder()
                    .outRefundNo(wxRefundEntity.getOutRefundNo()) // 退费订单号
                    .outTradeNo(wxRefundEntity.getOutTradeNo()) // 原支付订单号
                    .totalFee(wxRefundEntity.getTotalFee()) // 原订单金额
                    .refundFee(wxRefundEntity.getRefundFee()) // 退费金额
                    .refundDesc(wxRefundEntity.getRefundDesc()) // 退费描述
                    .notifyUrl(wxRefundEntity.getNotifyUrl()) // 退费回调地址
                    .build();
            wxPayService.refund(request);
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
            WxPayRefundNotifyResult refundNotifyResult = wxPayService.parseRefundNotifyResult(xmlData);
            log.info("退款回调数据：{}", JSONUtil.toJsonPrettyStr(refundNotifyResult));
            if ("SUCCESS".equals(refundNotifyResult.getReturnCode())) {
                // 处理退款回调逻辑
            }
        } catch (WxPayException e) {
            log.error("接受退款回调失败！", e);
        }
        return WxPayNotifyResponse.success("成功！");
    }

    /**
     * 企业转账
     */
    @PostMapping("/entPay")
    public R entPay(@RequestBody WxEntPayEntity wxEntPayEntity) {
        try {
            EntPayRequest request = EntPayRequest.newBuilder()
                    .partnerTradeNo(wxEntPayEntity.getPartnerTradeNo())
                    .openid(wxEntPayEntity.getOpenId())
                    .amount(wxEntPayEntity.getAmount())
                    .spbillCreateIp(wxEntPayEntity.getSpbillCreateIp())
                    .checkName(WxPayConstants.CheckNameOption.NO_CHECK)
                    .description(wxEntPayEntity.getDescription())
                    .build();
            wxPayService.getEntPayService().entPay(request);
            return R.ok();
        } catch (Exception e) {
            log.error("企业转账失败！", e);
            return R.error(StatusEnum.FAIL);
        }

    }
}
