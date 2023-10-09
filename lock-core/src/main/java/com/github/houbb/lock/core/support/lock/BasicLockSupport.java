package com.github.houbb.lock.core.support.lock;

import com.github.houbb.heaven.util.lang.StringUtil;
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

    private final Log log = LogFactory.getLog(this.getClass());

    /**
     * 真正的加锁实现
     * @param context 上下文
     * @return 结果
     */
    protected abstract boolean actualLock(ILockSupportContext context);

    /**
     * 真正的解锁实现
     * @param context 上下文
     * @return 结果
     */
    protected abstract boolean actualUnLock(ILockSupportContext context);

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

        return actualUnLock(context);
    }

}
