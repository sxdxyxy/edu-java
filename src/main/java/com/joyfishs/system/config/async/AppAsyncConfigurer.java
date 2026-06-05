package com.joyfishs.system.config.async;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import com.joyfishs.utils.SpringUtil;

@Configuration
@ComponentScan({"com.joyfishs.dawa.person.service","com.joyfishs.dawa.student.service"})
@EnableAsync
public class AppAsyncConfigurer implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        //线程池
        ScheduledExecutorService executor = SpringUtil.getBean("scheduledExecutorService");
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}
