package com.edu.web.exception;

import com.edu.enums.ResponseStatusEnum;
import com.edu.web.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice(annotations = ResponseBody.class)
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    /**
     * 处理基础类
     *
     * @param e
     * @return
     */

    @ExceptionHandler(value = {BaseException.class})
    @ResponseBody
    public ResponseResult<String> handleBaseException(Exception e) {
        log.error(e.getMessage(), e);
        return  ResponseResult.fail(ResponseStatusEnum.RESPONSE_Error,e.getMessage());
    }


    /**
     * @param e
     * @return
     */
    @ExceptionHandler(value = {RuntimeException.class, Exception.class})
    @ResponseBody
    public ResponseResult<String> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return  ResponseResult.fail(ResponseStatusEnum.RESPONSE_Error,e.getMessage());
    }
}