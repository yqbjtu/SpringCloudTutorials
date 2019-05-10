

package com.yq.config;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;



@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Slf4j
@Data
public class ThreadPool {

    private ThreadPoolTaskExecutor executor;

    /**
     * 线程池维护线程的最少数量
     */
    private int corePoolSize = 4;

    /**
     * 线程池维护线程的最大数量
     */
    private int maxPoolSize = 6;

    /**
     * 缓存队列
     */
    private int queueCapacity = 100;

    /**
     * 允许的空闲时间
     */
    private int keepAlive = 60;

    @Bean
    public Executor instanceExecutor() {
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("ThreadExecutor-");
        executor.setKeepAliveSeconds(keepAlive);
        executor.initialize();
        return executor;
    }
}
