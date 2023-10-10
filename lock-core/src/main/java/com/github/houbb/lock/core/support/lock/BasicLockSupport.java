package com.github.houbb.lock.core.support.lock;

import com.github.houbb.common.cache.api.service.ICommonCacheService;
import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.houbb.id.api.Id;
import com.github.houbb.id.core.util.IdThreadLocalHelper;
import com.github.houbb.lock.api.core.ILockSupportContext;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;

import java.util.concurrent.TimeUnit;

/**
 * 基础实现
 *
 * @author d
 * @since 1.6.0
 */
public abstract class BasicLockSupport extends AbstractLockSupport {

    protected final Log log = LogFactory.getLog(this.getClass());

    /**
     * 真正的加锁实现
     * @param context 上下文
     * @return 结果
     */
    protected boolean actualLock(ILockSupportContext context) {
        final ICommonCacheService commonCacheService = context.cache();
        final String key = this.getActualKey(context);

        //1.5.0 是否支持可重入
        boolean reentrant = context.reentrant();
        String holdRequestId = IdThreadLocalHelper.get();
        if(reentrant && StringUtil.isNotEmpty(holdRequestId)) {
            String cacheValue = commonCacheService.get(key);
            log.debug("[LOCK] TRY LOCK reentrant key {}, holdRequestId: {}, cacheValue: {}", key, holdRequestId, cacheValue);

            if(holdRequestId.equals(cacheValue)) {
                return true;
            }
        }

        // 生成当前线程的唯一标识
        final Id id = context.id();
        final String requestId = id.id();
        IdThreadLocalHelper.put(requestId);
        log.debug("[LOCK] BEGIN TRY LOCK key: {} requestId: {}", key, requestId);

        final TimeUnit timeUnit = context.timeUnit();
        final long lockTime = context.lockTime();
        final long lockExpireMills = timeUnit.toMillis(lockTime);

        // 执行真正的加锁
        return doActualLock(key, requestId, lockExpireMills, context);
    }

    /**
     * 执行加锁
     * @param key key
     * @param requestId 请求标识
     * @param lockExpireMills 加锁过期时间
     * @param context 上下文
     * @return 结果
     */
    protected abstract boolean doActualLock(String key, String requestId, long lockExpireMills,
                                            ILockSupportContext context);

    /**
     * 真正的解锁实现
     * @param context 上下文
     * @return 结果
     */
    protected abstract boolean doActualUnLock(String key, String requestId, ILockSupportContext context);

    @Override
    protected boolean doTryLock(ILockSupportContext context) {
        long startTimeMills = System.currentTimeMillis();

        // 一次获取，直接成功
        boolean result = this.actualLock(context);
        if(result) {
            return true;
        }

        // 时间判断
        final TimeUnit timeUnit = context.timeUnit();
        final long waitLockTime = context.waitLockTime();
        if(waitLockTime <= 0) {
            return false;
        }
        long durationMills = timeUnit.toMillis(waitLockTime);
        long endMills = startTimeMills + durationMills;

        // 循环等待
        final int tryLockIntervalMills = context.tryLockIntervalMills();
        while (System.currentTimeMillis() < endMills) {
            result = this.actualLock(context);
            if(result) {
                return true;
            }

            // 等待 10ms
            try {
                TimeUnit.MILLISECONDS.sleep(tryLockIntervalMills);
            } catch (InterruptedException e) {
                log.debug("[LOCK] try lock wait {} mills.", tryLockIntervalMills);
            }
        }
        return false;
    }

    @Override
    public boolean doUnlock(ILockSupportContext context) {
        final String key = this.getActualKey(context);

        String requestId = IdThreadLocalHelper.get();
        log.info("[LOCK] BEGIN UN LOCK key: {} requestId: {}", key, requestId);

        if(StringUtil.isEmpty(requestId)) {
            log.warn("[LOCK] UNLOCK requestId not found, ignore");
            return false;
        }

        return doActualUnLock(key, requestId, context);
    }

}
