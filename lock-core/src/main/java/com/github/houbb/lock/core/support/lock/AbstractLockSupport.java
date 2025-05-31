package com.github.houbb.lock.core.support.lock;

import com.github.houbb.lock.api.core.ILockKeyFormat;
import com.github.houbb.lock.api.core.ILockSupport;
import com.github.houbb.lock.api.core.ILockSupportContext;
import com.github.houbb.lock.core.support.format.LockKeyFormatContext;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;

/**
 * @author d
 * @since 1.6.0
 */
public abstract class AbstractLockSupport implements ILockSupport {

    private final Log log = LogFactory.getLog(this.getClass());

    protected abstract boolean doTryLock(ILockSupportContext context);

    public abstract boolean doUnlock(ILockSupportContext context);

    @Override
    public boolean tryLock(ILockSupportContext context) {
        return doTryLock(context);
    }

    @Override
    public boolean unlock(ILockSupportContext context) {
        return doUnlock(context);
    }

    /**
     * 构建真正的 key
     * @param context 上下文
     * @return 结果
     * @since 1.2.0
     */
    protected String getActualKey(ILockSupportContext context) {
        final String rawKey = context.key();
        final ILockKeyFormat keyFormat = context.lockKeyFormat();
        LockKeyFormatContext formatContext = LockKeyFormatContext.newInstance()
                .rawKey(rawKey);

        String key = keyFormat.format(formatContext);
        log.info("[LOCK] format rawKey: {} to key: {}", rawKey, key);
        return key;
    }

}
