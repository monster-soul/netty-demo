//package com.edu.web.exception.impl;
//
//import com.edu.core.IocManager;
//import com.edu.init.OnInitializeApplication;
//import com.edu.web.ResponseResult;
//import com.edu.web.exception.ExceptionHandler;
//import com.edu.web.exception.ExceptionManager;
//import com.edu.web.exception.ExceptionUtils;
//import org.springframework.boot.context.event.ApplicationStartedEvent;
//import org.springframework.core.Ordered;
//import org.springframework.stereotype.Component;
//
//import java.util.Collections;
//import java.util.List;
//
///**
// * 同一异常处理
// *
// * @author DuHai
// * @since 2021/7/17 16:12
// **/
//@Component
//public class ExceptionManagerImpl implements ExceptionManager, OnInitializeApplication {
//
//    private final IocManager iocManager;
//    private List<ExceptionHandler> handlers;
//
//    public ExceptionManagerImpl(IocManager iocManager) {
//        this.iocManager = iocManager;
//    }
//
//    @Override
//    public void onInitializeApplication(ApplicationStartedEvent event) {
//        this.handlers = this.iocManager.getBeanAll(ExceptionHandler.class);
//    }
//
//    @Override
//    public int getOrder() {
//        return Ordered.LOWEST_PRECEDENCE;
//    }
//
//    @Override
//    public ResponseResult<Object> handler(Throwable ex) {
//        List<Throwable> causeThrows = ExceptionUtils.getCauseThrowableList(ex);
//        Collections.reverse(causeThrows);
//        for (ExceptionHandler handler : this.handlers) {
//            for (Throwable causeThrow : causeThrows) {
//                if (handler.getThrowableClass().contains(causeThrow.getClass())) {
//                    ResponseResult<Object> result = handler.handler(causeThrow);
//                    result.buildError(ex);
//                    if (result != null) {
//                        return result;
//                    }
//                }
//            }
//        }
//        return null;
//    }
//}
