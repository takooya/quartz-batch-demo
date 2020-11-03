package com.takooya.exception;

import com.takooya.enums.ResultEnum;
import lombok.Getter;

/**
 * @author takooya
 */
public class BusinessException extends RuntimeException {
    @Getter
    private int status;
    @Getter
    private String message;

    public BusinessException(ResultEnum en) {
        this.status = en.getStatus();
        this.message = en.getMessage();
    }

    public BusinessException(int status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
}
