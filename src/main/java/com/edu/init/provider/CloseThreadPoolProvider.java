package com.edu.init.provider;

import com.edu.config.ThreadPoolSingleton;
import com.edu.init.OnInitializeApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author peng
 * @version 1.0
 * @Description:
 * @date 2024/10/30
 */
@Slf4j
@Component
public class CloseThreadPoolProvider implements OnInitializeApplication {

    @Override
    public void onInitializeApplication(ApplicationStartedEvent event) {
        ThreadPoolExecutor executor = ThreadPoolSingleton.getInstance();

        // 注册关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("关闭线程池 thread pool...");
            this.shutdownExecutor(executor);
        }));
    }

    private void shutdownExecutor(ThreadPoolExecutor executor) {
        executor.shutdown(); // 停止接收新任务

        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                log.warn("线程池未在 60 秒内终止，尝试中断所有任务...");
                executor.shutdownNow(); // 中断所有正在执行的任务
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    log.error("线程池未终止，可能有任务未完成");
                }
            }
        } catch (InterruptedException e) {
            log.error("线程池关闭过程中被中断", e);
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }


}
