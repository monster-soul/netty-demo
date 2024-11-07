package com.edu.web.response;

import com.edu.controller.BaseController;
import com.edu.web.ResponseResult;
import com.edu.web.annotation.WrapControllerResult;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 允许在执行 {@code HttpMessageConverter}之后自定义响应
 */
@ControllerAdvice
public class ResponseAdvisor implements ResponseBodyAdvice<Object> {
    /**
     * @param returnType
     * @param converterType
     * @return
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        WrapControllerResult annotation = null;
        //1、方法注解优先
        annotation = returnType.getMethod().getAnnotation(WrapControllerResult.class);
        if (annotation == null) {
            Class<?> declaringClass = returnType.getMethod().getDeclaringClass();
            //2、申明类注解次之
            WrapControllerResult[] annotations_declaringClass = declaringClass.getAnnotationsByType(WrapControllerResult.class);
            if (annotations_declaringClass.length >= 1) {
                annotation = annotations_declaringClass[0];
            }
            if (annotation == null) {
                //3、父类最后
                do {
                    declaringClass = declaringClass.getSuperclass();
                    if (declaringClass != null) {
                        annotations_declaringClass = declaringClass.getAnnotationsByType(WrapControllerResult.class);
                        if (annotations_declaringClass.length >= 1) {
                            annotation = annotations_declaringClass[0];
                        }
                    }
                } while (declaringClass != null && declaringClass != BaseController.class && annotation != null);
            }
        }
        if (annotation != null) {
            if (annotation.wrapOnSuccess()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * @param body
     * @param returnType
     * @param selectedContentType
     * @param selectedConverterType
     * @param request
     * @param response
     * @return
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ResponseResult) {
            return body;
        }
        return ResponseResult.success(body);
    }
}
