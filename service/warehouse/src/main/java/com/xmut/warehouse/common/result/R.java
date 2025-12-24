package com.xmut.warehouse.common.result;

import lombok.Data;
import java.io.Serializable;

/**
 * 全局统一响应格式
 */
@Data
public class R<T> implements Serializable {
    // 响应状态码：200-成功，400-参数错误，401-未登录/Token过期，403-无权限，500-服务器异常
    private int code;
    // 响应提示信息
    private String msg;
    // 响应数据
    private T data;

    // 私有构造方法
    private R() {}

    // 成功响应（无数据）
    public static <T> R<T> success() {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMsg("操作成功");
        return r;
    }

    // 成功响应（带数据）
    public static <T> R<T> success(T data) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMsg("操作成功");
        r.setData(data);
        return r;
    }

    // 失败响应（自定义提示）
    public static <T> R<T> fail(String msg) {
        R<T> r = new R<>();
        r.setCode(400);
        r.setMsg(msg);
        return r;
    }

    // 失败响应（自定义状态码+提示）
    public static <T> R<T> fail(int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }


}
