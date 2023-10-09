package com.github.houbb.lock.core.support.lock;

import com.github.houbb.common.cache.api.service.ICommonCacheService;
import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.houbb.id.api.Id;
import com.github.houbb.id.core.util.IdThreadLocalHelper;
import com.github.houbb.lock.api.core.ILockKeyFormat;
import com.github.houbb.lock.api.core.ILockSupportContext;
import com.github.houbb.lock.core.support.format.LockKeyFormatContext;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.houbb.redis.config.core.constant.JedisConst;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁接口定义
 * @author binbin.hou
 * @since 0.0.1
 */
public class RedisLockSupport extends AbstractLockSupport {

    private final Log log = LogFactory.getLog(RedisLockSupport.class);

    @Override
    public boolean doTryLock(ILockSupportContext context) {
        long startTimeMills = System.currentTimeMillis();

        // 一次获取，直接成功
        boolean result = this.doRedisLock(context);
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
        while (System.currentTimeMillis() < endMills) {
            result = this.doRedisLock(context);
            if(result) {
                return true;
            }

            // 等待 10ms
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                log.debug("[LOCK] try lock wait 10 mills.");
            }
        }
        return false;
    }

    /**
     * 执行加锁
     * @param context 上下文
     * @return 结果
     */
    protected boolean doRedisLock(ILockSupportContext context) {
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
        final int lockExpireMills = (int) timeUnit.toMillis(lockTime);

        String result = commonCacheService.set(key, requestId, JedisConst.SET_IF_NOT_EXIST, JedisConst.SET_WITH_EXPIRE_TIME, lockExpireMills);
        log.debug("[LOCK] END TRY LOCK key: {}, requestId: {}, lockExpireMills: {}, result: {}", key, requestId, lockExpireMills, result);
        return JedisConst.OK.equalsIgnoreCase(result);
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

        final ICommonCacheService commonCacheService = context.cache();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = commonCacheService.eval(script, Collections.singletonList(key), Collections.singletonList(requestId));
        log.info("[LOCK] END UN LOCK key: {}, requestId: {}, result: {}", key, requestId, result);
        return JedisConst.RELEASE_SUCCESS.equals(result);
    }

}
