package com.lang.wechat.controller.pay;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.github.binarywang.wxpay.bean.payscore.RiskFund;
import com.github.binarywang.wxpay.bean.payscore.TimeRange;
import com.github.binarywang.wxpay.bean.payscore.WxPayScoreRequest;
import com.github.binarywang.wxpay.bean.payscore.WxPayScoreResult;
import com.github.binarywang.wxpay.service.WxPayService;
import com.lang.wechat.config.wxenums.PayScoreRiskFundType;
import com.lang.wechat.util.R;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
public class WxPayScoreController {

	private WxPayService wxPayService;
	private Environment env;

	/**
	 * 创建支付分订单
	 *
	 * @return
	 */
	@GetMapping("/createPayScoreOrder")
	public R<Map<String, String>> createPayScoreOrder() {
		try {
			Date start = new Date();
			// 服务时间段
			String startTime = DateUtil.format(start, DatePattern.PURE_DATETIME_PATTERN);
			String endTime = DateUtil.format(DateUtil.offsetDay(start, 30), DatePattern.PURE_DATETIME_PATTERN);
			TimeRange timeRange = new TimeRange();
			// 不能new Date()时间，报错：服务开始时间不能早于调用接口时间，或传入固定值OnAccept表示用户确认订单成功时间为服务开始时间
			//timeRange.setStartTime(startTime);
			timeRange.setStartTime("OnAccept");
			timeRange.setEndTime(endTime);
			// 订单风险金
			RiskFund riskFund = new RiskFund();
			riskFund.setName(PayScoreRiskFundType.DEPOSIT.getName());
			riskFund.setAmount(9900);
			riskFund.setDescription("违约金");
			// 准备创建订单
			WxPayScoreRequest wxPayScoreRequest = WxPayScoreRequest.builder()
					// 商户服务订单号
					.outOrderNo("PS".concat(startTime).concat(RandomUtil.randomNumbers(4)))
					// 服务信息
					.serviceIntroduction("免押享借书")
					// 服务时间段
					.timeRange(timeRange)
					// 订单风险金
					.riskFund(riskFund)
					// 用户标识
					.openid("oV60Q0UVgSSkyMkxNEKzxbVZ1UAI")
					// 是否需要用户确认
					.needUserConfirm(Boolean.TRUE)
					// 回调地址
					.notifyUrl(env.getProperty("wx.pay.payScoreNotifyUrl"))
					.build();
			WxPayScoreResult payScoreResult = this.wxPayService.getPayScoreService().createServiceOrder(wxPayScoreRequest);
			return R.ok(payScoreResult.getPayScoreSignInfo());
		} catch (Exception e) {
			log.error("创建支付分订单异常！");
			return R.error("创建支付分订单异常！");
		}

	}
}
