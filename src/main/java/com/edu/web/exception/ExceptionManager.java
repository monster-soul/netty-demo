package com.edu.web.exception;

import com.edu.web.ResponseResult;

/**
 * 异常管理
 *
 * @author DuHai
 * @since 2021/7/17 16:16
 **/

public interface ExceptionManager {

    /**
     * 处理指定异常
     *
     * @param ex
     */
    ResponseResult<Object> handler(Throwable ex);
}

