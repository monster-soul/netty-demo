package com.edu.web.exception;

import com.edu.web.ResponseResult;

import java.util.List;

/**
 * 当需要对异常处理进行扩展是需要继承此处理程序
 *
 * @author DuHai
 * @since 2021/7/17 16:07
 **/

public interface ExceptionHandler {

    /**
     * 异常类型
     *
     * @return
     */
    List<Class<? extends Throwable>> getThrowableClass();

    /**
     * 当匹配此类型异常时触发处理方法
     *
     * @param ex
     * @return
     */
    ResponseResult<Object> handler(Throwable ex);
}

