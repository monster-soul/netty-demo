package com.edu.web.annotation;

import java.lang.annotation.*;

/**
 * 用于标注此控制器的方法返回结果是否进行包装
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface WrapControllerResult {
    /**
     * 包装成功结果
     * @return
     */
    boolean wrapOnSuccess() default true;

    /**
     * 包装异常结果
     * @return
     */
    boolean wrapOnError() default true;
}
