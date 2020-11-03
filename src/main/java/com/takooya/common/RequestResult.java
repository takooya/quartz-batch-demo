package com.takooya.common;

import com.takooya.enums.ResultEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestResult<T> {
    private T data;
    private int status;
    private String message;

    public RequestResult(T data) {
        this(ResultEnum.SUCCESS);
        this.data = data;
    }

    public RequestResult(ResultEnum info) {
        this(info.getStatus(), info.getMessage());
    }

    public RequestResult(ResultEnum info, T data) {
        this(info);
        this.data = data;
    }

    public RequestResult(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static <T> RequestResult<T> success(T data) {
        return new RequestResult<>(ResultEnum.SUCCESS, data);
    }

    public static RequestResult error(int status, String message) {
        return new RequestResult(status, message);
    }
}
