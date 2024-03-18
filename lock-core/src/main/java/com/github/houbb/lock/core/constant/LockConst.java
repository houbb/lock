package com.github.houbb.lock.core.constant;

import java.util.concurrent.TimeUnit;

/**
 * 通用锁常量
 *
 * @author binbin.hou
 * @since 0.0.3
 */
public final class LockConst {

    private LockConst() {
    }

    /**
     * 加锁 key 对应的命名空间
     * @since 1.4.0
     */
    public static final String DEFAULT_LOCK_KEY_NAMESPACE = "DIS_LOCK";

    /**
     * 默认时间单位
     * @since 1.5.0
     */
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

    /**
     * 默认加锁时间
     * @since 1.5.0
     */
    public static final int DEFAULT_LOCK_TIME = 60;

    /**
     * 默认等待加锁时间
     * @since 1.5.0
     */
    public static final int DEFAULT_WAIT_LOCK_TIME = 60;

    /**
     * 默认可重入标识
     * @since 1.5.0
     */
    public static final boolean DEFAULT_REENTRANT = true;

    /**
     * 尝试加锁的等待间隔
     * @since 1.6.0
     */
    public static final int DEFAULT_TRY_LOCK_INTERVAL_MILLS = 10;

}
