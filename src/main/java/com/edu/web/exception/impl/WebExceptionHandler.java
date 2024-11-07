//package com.edu.web.exception.impl;
//
//
//import com.edu.enums.ResponseStatusEnum;
//import com.edu.web.ResponseResult;
//import com.edu.web.exception.ExceptionHandler;
//import org.springframework.http.HttpHeaders;
//import org.springframework.stereotype.Component;
//import org.springframework.web.HttpMediaTypeNotSupportedException;
//
//import java.util.Arrays;
//import java.util.List;
//
///**
// * @author DuHai
// * @since 2022/11/5 13:41
// **/
//@Component
//public class WebExceptionHandler implements ExceptionHandler {
//
//    @Override
//    public List<Class<? extends Throwable>> getThrowableClass() {
//        return Arrays.asList(HttpMediaTypeNotSupportedException.class);
//    }
//
//    @Override
//    public ResponseResult<Object> handler(Throwable ex) {
//        HttpMediaTypeNotSupportedException httpMediaTypeNotSupportedException = (HttpMediaTypeNotSupportedException) ex;
//        ResponseResult<Object> result = ResponseResult.fail(ResponseStatusEnum.RESPONSE_ServerError);
//        result.setMsg(String.format("不支持的媒体类型[%s]:[%s]", HttpHeaders.CONTENT_TYPE, httpMediaTypeNotSupportedException.getContentType().toString()));
//        return result;
//    }
//}
