package com.github.houbb.lock.spring.annotation;

import com.github.houbb.lock.core.constant.LockConst;
import com.github.houbb.lock.spring.config.LockAopConfig;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用注解
 * @author binbin.hou
 * @since 0.0.2
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LockAopConfig.class)
@EnableAspectJAutoProxy
public @interface EnableLock {

    /**
     * 唯一标识生成策略
     * @return 结果
     * @since 1.1.0
     */
    String id() default "lockId";

    /**
     * 缓存实现策略 bean 名称
     *
     * 默认引入 redis-config 中的配置
     *
     * @return 实现
     * @since 1.1.0
     */
    String cache() default "springRedisService";

    /**
     * 加锁 key 格式化策略
     * @return 策略
     * @since 1.2.0
     */
    String lockKeyFormat() default "lockKeyFormat";

    /**
     * 锁释放失败处理类
     * @return 结果
     * @since 1.2.0
     */
    String lockReleaseFailHandler() default "lockReleaseFailHandler";

}
