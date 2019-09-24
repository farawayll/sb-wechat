/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.lang.wechat.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */
public class R extends LinkedHashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    public R() {
        put("code", StatusEnum.SUCCESS.getCode());
        put("msg", StatusEnum.SUCCESS.getMsg());
    }

    public static R error(int code, String msg) {
        R r = new R();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static R error(StatusEnum statusEnum) {
        R r = new R();
        r.put("code", statusEnum.getCode());
        r.put("msg", statusEnum.getMsg());
        return r;
    }

    public static R ok() {
        return new R();
    }

    public static R ok(String msg) {
        R r = new R();
        r.put("msg", msg);
        return r;
    }

    public static R ok(Object data) {
        R r = new R();
        Map dataMap = new HashMap();
        dataMap.put("data", data);
        r.putAll(dataMap);
        return r;
    }

}
