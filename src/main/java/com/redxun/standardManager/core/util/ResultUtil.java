package com.redxun.standardManager.core.util;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
public class ResultUtil implements Serializable {
    private static final long serialVersionUID = 1324153990591853955L;
    private Integer code;
    private String msg;
    private Object object;

    public ResultUtil(Integer code, String msg, Object object) {
        this.code = code;
        this.msg = msg;
        this.object = object;
    }

    public static JSONObject result(int code, String msg, Object object) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("message", msg);
        jsonObject.put("data", object);
        return jsonObject;
    }
    public static JSONObject result(Boolean success, String msg, Object object) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", success);
        jsonObject.put("message", msg);
        jsonObject.put("data", object);
        return jsonObject;
    }
}
