package com.takooya.exceptionHandler;

import com.takooya.common.RequestResult;
import com.takooya.exception.BusinessException;
import com.takooya.exception.QuartzManageException;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * @author takooya
 */
@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(value = QuartzManageException.class)
    public RequestResult handleError(QuartzManageException e) {
        return RequestResult.error(e.getStatus(), e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = SchedulerException.class)
    public RequestResult handleError(SchedulerException e) {
        e.printStackTrace();
        return RequestResult.error(500, e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = BusinessException.class)
    public RequestResult handleError(BusinessException e) {
        e.printStackTrace();
        return RequestResult.error(e.getStatus(), e.getMessage());
    }
}
