package com.atguigu.common.exception;

import lombok.Data;

@Data
public class GLException extends RuntimeException {
    private Integer code;

    public GLException() {
        super();
    }

    public GLException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public GLException(BizCodeEnum enums) {
        super(enums.getMsg());
        this.code = enums.getCode();
    }
}
