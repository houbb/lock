package com.github.houbb.lock.core.support.lock;

import com.github.houbb.common.cache.api.service.ICommonCacheService;
import com.github.houbb.lock.api.core.ILockSupportContext;
import com.github.houbb.redis.config.core.constant.JedisConst;

/**
 * 分布式锁接口定义
 * @author binbin.hou
 * @since 0.0.1
 */
public class CommonCacheLockSupport extends BasicLockSupport {

    @Override
    protected boolean doActualLock(String key, String requestId, long lockExpireMills, ILockSupportContext context) {
        final ICommonCacheService commonCacheService = context.cache();

        String result = commonCacheService.set(key, requestId,
                JedisConst.SET_IF_NOT_EXIST,
                JedisConst.SET_WITH_EXPIRE_TIME,
                (int) lockExpireMills);
        log.debug("[LOCK] END TRY LOCK key: {}, requestId: {}, lockExpireMills: {}, result: {}",
                key, requestId, lockExpireMills, result);
        return JedisConst.OK.equalsIgnoreCase(result);
    }

    @Override
    protected boolean doActualUnLock(String key, String requestId, ILockSupportContext context) {
        final ICommonCacheService commonCacheService = context.cache();

        boolean result = commonCacheService.removeEx(key, requestId);
        log.debug("[LOCK] END UNLOCK key: {}, requestId: {}, result: {}",
                key, requestId, result);
        return result;
    }

}
