package com.joyfishs.dawa.common.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 插件配置
 * <p>
 * 当前仅启用乐观锁拦截器。
 * 分页拦截器由 pagehelper-spring-boot-starter 单独提供,不在这里配置。
 * </p>
 *
 * @author safe-edu
 * @since 2026-06-06
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 乐观锁拦截器
     * <p>
     * 配合实体上的 {@code @Version} 字段使用。
     * updateById / update 时自动检查 version,版本不一致会抛
     * {@link org.springframework.dao.OptimisticLockingFailureException}。
     * </p>
     * <p>
     * 适用场景:同一人员多条违章同时录入,扣分更新时防止覆盖。
     * </p>
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }
}
