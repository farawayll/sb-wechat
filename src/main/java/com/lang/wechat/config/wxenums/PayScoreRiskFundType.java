package com.lang.wechat.config.wxenums;

public enum PayScoreRiskFundType {
	/**
	 * 【先免模式】（评估不通过可交押金）可填名称为【DEPOSIT：押金；ADVANCE：预付款；CASH_DEPOSIT：保证金】
	 * 【先享模式】（评估不通过不可使用服务）可填名称为【ESTIMATE_ORDER_COST：预估订单费用】
	 */

	DEPOSIT("DEPOSIT"),
	ADVANCE("ADVANCE"),
	CASH_DEPOSIT("CASH_DEPOSIT"),
	ESTIMATE_ORDER_COST("ESTIMATE_ORDER_COST");

	private String name;

	PayScoreRiskFundType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
