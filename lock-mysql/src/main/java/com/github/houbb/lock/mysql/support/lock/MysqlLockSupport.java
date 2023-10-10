package com.github.houbb.lock.mysql.support.lock;

import com.github.houbb.common.cache.api.service.ICommonCacheService;
import com.github.houbb.lock.api.core.ILockSupportContext;
import com.github.houbb.lock.core.support.lock.BasicLockSupport;
import com.github.houbb.lock.mysql.constant.LockMysqlConst;
import com.github.houbb.redis.config.core.constant.JedisConst;

/**
 * @author d
 * @since 1.6.0
 */
public class MysqlLockSupport extends BasicLockSupport {

    @Override
    protected boolean doActualLock(String key,
                                   String requestId,
                                   long lockExpireMills,
                                   ILockSupportContext context) {
        final ICommonCacheService commonCacheService = context.cache();

        String result = commonCacheService.set(key,
                requestId,
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

        // 释放锁：delete from distributed_lock where lock_key=#{key} and lock_holder=#{holder}
        String sqlFormat = "delete from %s where lock_key='%s' and lock_holder='%s'";
        String sql = String.format(sqlFormat, LockMysqlConst.DISTRIBUTED_LOCK_T, key, requestId);

        Object result = commonCacheService.eval(sql);
        log.debug("[LOCK] END UN LOCK key: {}, requestId: {}, result: {}", key, requestId, result);

        return JedisConst.RELEASE_SUCCESS.equals(result);
    }

}
