package com.avengereug.mall.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年10月27日 下午9:59:27
 */
public class R extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public static final int SUCCESS_CODE = 0;

    public R() {
        put("code", SUCCESS_CODE);
    }

    public static R error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static R error(String msg) {
        return error(500, msg);
    }

    public static R error(int code, String msg) {
        R r = new R();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static R ok(String msg) {
        R r = new R();
        r.put("msg", msg);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    public static R ok() {
        return new R();
    }

    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public Integer getCode() {
        return Integer.valueOf(this.get("code").toString());
    }

    public String getMsg() {
        return (String) this.get("msg");
    }

    /**
     * TODO 因为R继承了HashMap，我们直接给R添加一个泛型是不起作用的，就算我们调用了setData方法，最终还是获取不到这个数据，待确认为什么会有这种情况的发生。
     *
     * TODO 为了解决全局统一返回对象(服务间调用与前后端服务调用都使用R对象)，且支持服务间传输指定类型数据的情况，我们可以把R再次进行封装, 目前先写一个普通
     * 响应类来处理
     *
     *
     */
    /*private T data;

    public T getData() {
        return data;
    }

    public R<T> setData(T data) {
        this.data = data;
        return this;
    }*/



}
