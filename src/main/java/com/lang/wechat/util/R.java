package com.lang.wechat.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class R<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private int code;

	private String message;

	private T data;

	/**
	 * 失败
	 */
	public static <T> R<T> error() {
		return getResult(null, Results.FAIL.getCode(), Results.FAIL.getMsg());
	}

	public static <T> R<T> error(String msg) {
		return getResult(null, Results.FAIL.getCode(), msg);
	}

	public static <T> R<T> error(Results results) {
		return getResult(null, results.getCode(), results.getMsg());
	}

	/**
	 * 成功
	 */
	public static <T> R<T> ok() {
		return getResult(null, Results.SUCCESS.getCode(), Results.SUCCESS.getMsg());
	}

	public static <T> R<T> ok(T data) {
		return getResult(data, Results.SUCCESS.getCode(), Results.SUCCESS.getMsg());
	}

	private static <T> R<T> getResult(T data, int code, String msg) {
		return new R<>(code, msg, data);
	}
}
