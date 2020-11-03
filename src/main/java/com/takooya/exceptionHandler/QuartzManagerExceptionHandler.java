package com.takooya.exceptionHandler;

import com.takooya.common.RequestResult;
import com.takooya.exception.QuartzManageException;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class QuartzManagerExceptionHandler {

    @ExceptionHandler(value = QuartzManageException.class)
    public RequestResult handleError(QuartzManageException e) {
        return RequestResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = SchedulerException.class)
    public RequestResult handleError(SchedulerException e) {
        e.printStackTrace();
        return RequestResult.error(500, e.getMessage());
    }
}
