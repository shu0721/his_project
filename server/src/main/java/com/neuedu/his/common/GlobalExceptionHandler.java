package com.neuedu.his.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理：把后端异常收敛成统一返回体，避免前端拿到 500 页面。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public JsonResult<Void> handleBusiness(BusinessException e) {
        return JsonResult.fail(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public JsonResult<Void> handleOther(Exception e) {
        e.printStackTrace();
        return JsonResult.fail("服务异常：" + e.getMessage());
    }
}
