package com.takooya.exception;

import com.takooya.enums.ResultEnum;
import lombok.Getter;

public class QuartzManageException extends RuntimeException {
    @Getter
    private int code;
    @Getter
    private String message;

    public QuartzManageException(ResultEnum en) {
        this.code = en.getStatus();
        this.message = en.getMessage();
    }

    public QuartzManageException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
