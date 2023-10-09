package com.github.houbb.lock.mysql.support.lock;

import com.github.houbb.lock.api.core.ILockSupportContext;
import com.github.houbb.lock.core.support.lock.BasicLockSupport;

/**
 * @author d
 * @since 1.6.0
 */
public class MysqlLockSupport extends BasicLockSupport {

    @Override
    protected boolean actualLock(ILockSupportContext context) {
        // 插入？

        // 更新？

        return false;
    }

    @Override
    protected boolean actualUnLock(ILockSupportContext context) {
        // 释放锁：delete from distributed_lock where lock_key=#{key} and lock_holder=#{holder}

        return false;
    }
}
