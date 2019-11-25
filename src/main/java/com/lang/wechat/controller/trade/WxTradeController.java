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
import com.lang.wechat.entity.WxPayRequestDTO;
import com.lang.wechat.entity.WxRefundRequestDTO;
import com.lang.wechat.util.R;
import com.lang.wechat.util.StatusEnum;
import com.lang.wechat.util.TradeConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * 支付(获取支付的预支付对象)
     */
    @PostMapping("/createOrder")
    public R createOrder(@RequestBody WxPayRequestDTO wxPayRequestDTO) {
        try {
            /**
             * 预支付对象的参数，以下都是必传参数。JSAPI支付和小程序支付必传"JSAPI"和openId
             */
            WxPayUnifiedOrderRequest request = WxPayUnifiedOrderRequest.newBuilder()
                    .body(wxPayRequestDTO.getBody())
                    .totalFee(wxPayRequestDTO.getTotalFee())
                    .spbillCreateIp(wxPayRequestDTO.getSpbillCreateIp())
                    .notifyUrl(wxPayRequestDTO.getNotifyUrl())
                    .tradeType(JSAPI)
                    .openid(wxPayRequestDTO.getOpenid())
                    .outTradeNo(wxPayRequestDTO.getOutTradeNo())
                    .build();
            /**
             * 获取预支付对象
             * 1.WxJavaSDK源码会根据上面的预支付参数通过MD5算法帮我们生成sign,然后一并将数据传至微信服务器;
             * 2.数据包到微信服务器后，它也会数据包中的预支付参数生成sign,然后与传过去的数据包中的sign对比是否一致,防止数据篡改
             */
            WxPayMpOrderResult payResult = wxPayService.createOrder(request);
            /**
             * 预支付对象字段说明
             * 1.appId：微信分配的小程序ID(前端保存的有，如果没有从后端返回)
             * 2.timeStamp：时间戳从1970年1月1日00:00:00至今的秒数,即当前的时间
             * 3.nonceStr：随机字符串，不长于32位
             * 4.packageValue(package)：数据包，统一下单接口返回的 prepay_id 参数值，提交格式如：prepay_id=wx2017033010242291fcfe0db70013231072
             * 5.signType：签名类型，默认为MD5
             * 6.paySign：签名，WxJavaSDK源码生成签名返回到前端，前端带着签名去支付，防止数据篡改
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
        if (TradeConstant.SUCCESS.equals(wxPayOrderNotifyResult.getReturnCode()) && TradeConstant.SUCCESS.equals(wxPayOrderNotifyResult.getResultCode())) {
            // 处理支付回调逻辑
        }
        return TradeConstant.NOTIFY_SUCCESS;
    }

    /**
     * 退款
     */
    @PostMapping("/refund")
    public R refund(WxRefundRequestDTO wxRefundRequestDTO) {
        try {
            WxPayRefundRequest request = WxPayRefundRequest.newBuilder()
                    .outRefundNo(wxRefundRequestDTO.getOutRefundNo())
                    .outTradeNo(wxRefundRequestDTO.getOutTradeNo())
                    .totalFee(wxRefundRequestDTO.getTotalFee())
                    .refundFee(wxRefundRequestDTO.getRefundFee())
                    // 以下两个非必传(退款描述和退款回调地址，但是一般都要传)
                    .refundDesc(wxRefundRequestDTO.getRefundDesc())
                    .notifyUrl(wxRefundRequestDTO.getNotifyUrl())
                    .build();
            WxPayRefundResult wxPayRefundResult = wxPayService.refund(request);
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
        WxPayRefundNotifyResult refundNotifyResult = null;
        try {
            refundNotifyResult = wxPayService.parseRefundNotifyResult(xmlData);
        } catch (WxPayException e) {
            log.error("接受退款回调失败！", e);
        }
        log.info("退款回调数据：{}", JSONUtil.toJsonPrettyStr(refundNotifyResult));
        if (TradeConstant.SUCCESS.equals(refundNotifyResult.getReturnCode())) {
            // 处理退款回调逻辑
        }
        return TradeConstant.NOTIFY_SUCCESS;
    }
}
