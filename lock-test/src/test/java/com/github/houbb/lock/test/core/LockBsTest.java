package com.github.houbb.lock.test.core;


import com.github.houbb.id.core.core.Ids;
import com.github.houbb.lock.api.core.ILock;
import com.github.houbb.lock.api.core.ILockContext;
import com.github.houbb.lock.core.bs.LockBs;
import com.github.houbb.lock.core.bs.LockContext;
import com.github.houbb.lock.core.constant.LockConst;
import com.github.houbb.lock.core.support.format.LockKeyFormat;
import com.github.houbb.lock.core.support.handler.LockReleaseFailHandler;
import com.github.houbb.redis.config.core.factory.JedisRedisServiceFactory;
import org.junit.Assert;
import org.junit.Test;

public class LockBsTest {

    @Test
    public void helloTest() {
        ILock lock = LockBs.newInstance();
        String key = "ddd";
        try {
            // 加锁
            boolean lockFlag = lock.tryLock(key);
            Assert.assertTrue(lockFlag);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 释放锁
            lock.unlock(key);
        }
    }

    @Test
    public void tryLockContextTest() {
        ILock lock = LockBs.newInstance();
        String key = "ddd";
        try {
            // 加锁
            ILockContext lockContext = LockContext.newInstance()
                    .key(key)
                    .lockTime(LockConst.DEFAULT_LOCK_TIME)
                    .waitLockTime(LockConst.DEFAULT_WAIT_LOCK_TIME)
                    .reentrant(LockConst.DEFAULT_REENTRANT)
                    .timeUnit(LockConst.DEFAULT_TIME_UNIT);

            boolean lockFlag = lock.tryLock(lockContext);
            Assert.assertTrue(lockFlag);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 释放锁
            lock.unlock(key);
        }
    }

    @Test
    public void reTest() {
        ILock lock = LockBs.newInstance();
        String key = "ddd";
        try {
            // 加锁
            boolean lockFlag = lock.tryLock(key);
            //1. 首次获取锁成功
            Assert.assertTrue(lockFlag);
            //2. 重新获取锁成功
            boolean reLockFlag = lock.tryLock(key);
            Assert.assertTrue(reLockFlag);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 释放锁
            lock.unlock(key);
        }
    }

    @Test
    public void noReTest() {
        ILock lock = LockBs.newInstance();
        String key = "ddd";
        try {
            ILockContext lockContext = LockContext.newInstance()
                    .key(key)
                    .waitLockTime(5)
                    .reentrant(false);  // 指定不可重入

            boolean lockFlag = lock.tryLock(lockContext);
            //1. 首次获取锁成功
            Assert.assertTrue(lockFlag);
            //2. 不是重入，第二次获取失败
            boolean reLockFlag = lock.tryLock(lockContext);
            Assert.assertFalse(reLockFlag);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 释放锁
            lock.unlock(key);
        }
    }

    @Test
    public void configTest() {
        LockBs.newInstance()
                .id(Ids.uuid32())   //id 生成策略
                .lockKeyFormat(new LockKeyFormat())     // 针对 key 的格式化处理策略
                .lockReleaseFailHandler(new LockReleaseFailHandler())   //释放锁失败处理
                ;
    }

}
