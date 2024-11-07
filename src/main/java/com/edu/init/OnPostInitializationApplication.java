package com.edu.init;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.EventPublishingRunListener;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

/**
 * 应用初始化之后
 * 由{@link EventPublishingRunListener#running}触发执行
 * 可直接实现本接口注册为Bean使用
 * 此时可进行以下事情：
 * 1、获取指定条件的Bean,因为这时候所有的Bean已经注册并初始化完毕
 * 2、运行一些初始化事项，例如：调用接口、数据库等网络操作
 * <p>
 * 注意：
 * 1、此时不应该再进行Bean的注册和修改
 *
 * @author pengxiangjun
 * @since 2024/3/28 10:01
 **/
public interface OnPostInitializationApplication
        extends ApplicationListener<ApplicationReadyEvent>, Ordered, DisposableBean {

    /**
     * 应用初始化之后执行
     *
     * @param event
     */
    void onPostInitializationApplication(ApplicationReadyEvent event);

    @Override
    default void onApplicationEvent(ApplicationReadyEvent event) {
        onPostInitializationApplication(event);
    }

    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    default void destroy() throws Exception {
        //这个主要用于观察，哪些初始化程序被关闭，可以用于监听IocManager.replace替换后初始化程序的变化
        LoggerFactory.getLogger(this.getClass()).debug(String.format("%s被释放了", this.getClass().getSimpleName()));
    }
}
