package com.xmut.warehouse.common.exception;

import com.xmut.warehouse.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice // 全局捕获@RestController异常
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public R<?> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return R.fail(e.getMessage());
    }

    /**
     * 捕获系统异常（所有未处理的异常）
     */
    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e) {
        log.error("系统异常：", e);
        return R.fail(500, "服务器内部异常，请联系管理员");
    }
}

/**
 * 自定义业务异常（便于业务逻辑中抛出）
 */
class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
