package com.sangeng.exception;

import com.sangeng.common.Result;
import com.sangeng.common.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理器
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理所有异常
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        logger.error("系统异常：", e);
        return Result.error(Status.ERROR.getCode(), "系统异常，请联系管理员");
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        logger.warn("业务异常：{}", e.getMessage());
        return Result.error(Status.BUSINESS_ERROR.getCode(), e.getMessage());
    }

    /**
     * 处理参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("参数异常：{}", e.getMessage());
        return Result.error(Status.PARAM_ERROR.getCode(), e.getMessage());
    }
}