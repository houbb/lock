package com.github.houbb.lock.core.support.lock;

import com.github.houbb.common.cache.api.service.ICommonCacheService;
import com.github.houbb.id.api.Id;
import com.github.houbb.lock.api.core.ILockKeyFormat;
import com.github.houbb.lock.api.core.ILockReleaseFailHandler;
import com.github.houbb.lock.api.core.ILockSupportContext;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁接口定义
 * @author binbin.hou
 * @since 0.0.1
 */
public class LockSupportContext implements ILockSupportContext {

    public static LockSupportContext newInstance() {
        return new LockSupportContext();
    }

    /**
     * 标识策略
     * @since 0.0.4
     */
    private Id id;

    /**
     * 缓存策略
     * @since 0.0.4
     */
    private ICommonCacheService cache;

    /**
     * 时间单位
     */
    private TimeUnit timeUnit;

    /**
     * 加锁时间
     * @since 1.2.0
     */
    private long lockTime;

    /**
     * 等待加锁时间
     * @since 1.2.0
     */
    private long waitLockTime;

    /**
     * 缓存对应的 key
     * @since 1.2.0
     */
    private String key;

    /**
     * 锁 key 格式化
     * @since 1.2.0
     */
    private ILockKeyFormat lockKeyFormat;

    /**
     * 锁释放失败处理类
     * @since 1.2.0
     */
    private ILockReleaseFailHandler lockReleaseFailHandler;


    /**
     * 是否可以重入获取
     * @since 1.5.0
     */
    private boolean reentrant;

    /**
     * 尝试加锁等待时间间隔
     * @since 1.6.0
     */
    private int tryLockIntervalMills;

    @Override
    public Id id() {
        return id;
    }

    public LockSupportContext id(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public ICommonCacheService cache() {
        return cache;
    }

    public LockSupportContext cache(ICommonCacheService cache) {
        this.cache = cache;
        return this;
    }

    @Override
    public TimeUnit timeUnit() {
        return timeUnit;
    }

    public LockSupportContext timeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    @Override
    public long lockTime() {
        return lockTime;
    }

    public LockSupportContext lockTime(long lockTime) {
        this.lockTime = lockTime;
        return this;
    }

    @Override
    public long waitLockTime() {
        return waitLockTime;
    }

    public LockSupportContext waitLockTime(long waitLockTime) {
        this.waitLockTime = waitLockTime;
        return this;
    }

    @Override
    public String key() {
        return key;
    }

    public LockSupportContext key(String key) {
        this.key = key;
        return this;
    }

    @Override
    public ILockKeyFormat lockKeyFormat() {
        return lockKeyFormat;
    }

    public LockSupportContext lockKeyFormat(ILockKeyFormat lockKeyFormat) {
        this.lockKeyFormat = lockKeyFormat;
        return this;
    }

    @Override
    public ILockReleaseFailHandler lockReleaseFailHandler() {
        return lockReleaseFailHandler;
    }

    public LockSupportContext lockReleaseFailHandler(ILockReleaseFailHandler lockReleaseFailHandler) {
        this.lockReleaseFailHandler = lockReleaseFailHandler;
        return this;
    }

    @Override
    public boolean reentrant() {
        return reentrant;
    }

    public LockSupportContext reentrant(boolean reentrant) {
        this.reentrant = reentrant;
        return this;
    }

    @Override
    public int tryLockIntervalMills() {
        return tryLockIntervalMills;
    }

    public LockSupportContext tryLockIntervalMills(int tryLockIntervalMills) {
        this.tryLockIntervalMills = tryLockIntervalMills;
        return this;
    }
}
