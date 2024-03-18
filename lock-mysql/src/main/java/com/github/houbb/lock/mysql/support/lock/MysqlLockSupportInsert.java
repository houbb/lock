package com.github.houbb.lock.mysql.support.lock;

import com.github.houbb.jdbc.api.dal.IMapper;
import com.github.houbb.lock.api.core.ILockSupportContext;
import com.github.houbb.lock.mysql.constant.LockMysqlConst;

/**
 * 说明：通过插入的方式抢占锁，删除数据释放锁。适合 key 一次性的锁资源增强，保证数据的干净。
 *
 * @author d
 * @since 1.6.0
 */
public class MysqlLockSupportInsert extends AbstractMysqlLockSupport {

    public MysqlLockSupportInsert(IMapper mapper) {
        super(mapper);
    }

    @Override
    protected String buildLockSql(String key, String requestId, long lockExpireMills, ILockSupportContext context) {
        // 通过唯一约束直接插入
        long expireAt = System.currentTimeMillis() + lockExpireMills;
        String sqlFormat = "insert into %s (lock_key, lock_holder, lock_expire_time) values ('%s', '%s', '%d')";
        return String.format(sqlFormat, LockMysqlConst.DISTRIBUTED_LOCK_T,
                key, requestId, expireAt);
    }

    @Override
    protected String buildUnLockSql(String key, String requestId, ILockSupportContext context) {
        // 释放锁：delete from distributed_lock where lock_key=#{key} and lock_holder=#{holder}
        String sqlFormat = "delete from %s where lock_key='%s' and lock_holder='%s'";

        return String.format(sqlFormat, LockMysqlConst.DISTRIBUTED_LOCK_T, key, requestId);
    }

}
