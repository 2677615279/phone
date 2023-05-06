package com.past.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池 配置类
 */
@Configuration
public class ExecutorConfig {

    /**
     * 定义 线程池
     * @return executor
     */
    @Bean("executor")
    public Executor executor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数量：当前机器的核心数
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());

        // 最大线程数量：当前机器的核心数 * 2
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 2);

        // 队列大小
        executor.setQueueCapacity(Integer.MAX_VALUE);

        // 线程池中的线程名前缀
        executor.setThreadNamePrefix("export-");

        // 终止策略 默认 直接拒绝
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

        // 执行初始化
        executor.initialize();

        return executor;
    }

}
