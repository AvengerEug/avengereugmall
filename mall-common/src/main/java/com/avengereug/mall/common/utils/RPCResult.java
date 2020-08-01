package com.avengereug.mall.common.utils;

import lombok.Data;

@Data
public final class RPCResult<T> {

    private T result;
    private int code;
    private String msg;

    public RPCResult<T> ok(T result) {
        this.code = 0;
        this.result = result;
        return this;
    }
}
