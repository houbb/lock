package com.github.houbb.lock.api.core;

import com.github.houbb.common.cache.api.service.ICommonCacheService;
import com.github.houbb.id.api.Id;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁接口定义
 * @author binbin.hou
 * @since 0.0.1
 */
public interface ILockSupportContext {

    /**
     * 标识
     *
     * @return 标识策略
     * @since 0.0.4
     */
    Id id();

    /**
     * 缓存
     * @return 缓存策略
     * @since 0.0.4
     */
    ICommonCacheService cache();

    /**
     * 时间单位
     * @return 时间单位
     */
    TimeUnit timeUnit();

    /**
     * 加锁时间
     * @return 时间
     * @since 1.2.0
     */
    long lockTime();

    /**
     * 等待加锁时间
     * @return 等待时间
     * @since 1.2.0
     */
    long waitLockTime();

    /**
     * 缓存对应的 key
     * @return 结果
     * @since 1.2.0
     */
    String key();

    /**
     * 锁 key 格式化
     * @since 1.2.0
     * @return 格式化
     */
    ILockKeyFormat lockKeyFormat();

    /**
     * 锁 key 的默认命名空间
     * @return 锁 key 的默认命名空间
     * @since 1.4.0
     */
    String lockKeyNamespace();

    /**
     * 锁释放失败处理类
     * @since 1.2.0
     * @return 失败处理
     */
    ILockReleaseFailHandler lockReleaseFailHandler();

    /**
     * 是否可以重入获取
     * @return 结果
     * @since 1.5.0
     */
    boolean reentrant();

    /**
     * 尝试加锁等待间隔
     * @return 间隔
     * @since 1.6.0
     */
    int tryLockIntervalMills();

}
