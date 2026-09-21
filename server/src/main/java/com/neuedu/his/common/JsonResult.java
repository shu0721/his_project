package com.neuedu.his.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回体
 * <p>
 * 前端约定：result=true 时业务数据在 data；result=false 时错误信息在 errMsg。
 *
 * @param <T> 业务数据类型
 */
@Data
public class JsonResult<T> implements Serializable {

    /** 本次请求是否成功 */
    private Boolean result;

    /** 失败时的错误信息 */
    private String errMsg;

    /** 成功时的业务数据 */
    private T data;

    /** 登录成功后下发的 JWT */
    private String token;

    public JsonResult() {
    }

    public JsonResult(T data) {
        this.result = true;
        this.data = data;
    }

    public JsonResult(T data, String token) {
        this.result = true;
        this.data = data;
        this.token = token;
    }

    public JsonResult(String errMsg) {
        this.result = false;
        this.errMsg = errMsg;
    }

    /** 成功（无数据） */
    public static <T> JsonResult<T> ok() {
        JsonResult<T> r = new JsonResult<>();
        r.setResult(true);
        return r;
    }

    /** 成功（带数据） */
    public static <T> JsonResult<T> ok(T data) {
        return new JsonResult<>(data);
    }

    /** 成功（带数据与 token） */
    public static <T> JsonResult<T> ok(T data, String token) {
        return new JsonResult<>(data, token);
    }

    /** 失败 */
    public static <T> JsonResult<T> fail(String errMsg) {
        return new JsonResult<>(errMsg);
    }
}
