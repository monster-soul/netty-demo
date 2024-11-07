package com.edu.init;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.event.EventPublishingRunListener;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

/**
 * 应用初始化
 * 由{@link EventPublishingRunListener#started}触发执行
 * 可直接实现本接口注册为Bean使用
 * 此时可进行以下事情：
 * 1、手动注册Bean
 * 2、手动修改、替换、删除Bean
 * 3、配置修改
 * <p>
 * 注意：
 * 1、此时不可以进行网络操作
 * 2、此时所有的自动装载bean都已加载完毕
 *
 * @author pengxiangjun
 * @since 2024/3/29 16:12
 **/
public interface OnInitializeApplication extends ApplicationListener<ApplicationStartedEvent>, Ordered {

    /**
     * 应用启动后执行
     *
     * @param event
     */
    void onInitializeApplication(ApplicationStartedEvent event);

    @Override
    default void onApplicationEvent(ApplicationStartedEvent event) {
        onInitializeApplication(event);
    }

    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
