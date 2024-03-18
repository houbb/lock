package com.github.houbb.lock.mysql.support.lock;

import com.github.houbb.jdbc.api.dal.IMapper;
import com.github.houbb.lock.api.core.ILockSupportContext;
import com.github.houbb.lock.core.support.lock.BasicLockSupport;
import com.github.houbb.lock.mysql.constant.LockMysqlConst;
import com.github.houbb.redis.config.core.constant.JedisConst;

/**
 * 预设 Key 对应的记录已经提前插入，通过更新状态控制锁的争抢和释放。适合一些固定的任务等。
 *
 * 场景：提前设置好对应的 task 信息.
 *
 * @author d
 * @since 1.6.0
 */
public class MysqlLockSupportUpdate extends AbstractMysqlLockSupport {

    public MysqlLockSupportUpdate(IMapper mapper) {
        super(mapper);
    }

    @Override
    protected String buildLockSql(String key, String requestId, long lockExpireMills, ILockSupportContext context) {
        // 这里需要判断锁已经过期。
        // 或者锁的状态为初始化
        long now = System.currentTimeMillis();
        long expireAt = now + lockExpireMills;
        String sqlFormat = "UPDATE %s SET lock_holder='%s', lock_expire_time=%d, lock_status='P' " +
                "WHERE lock_key = '%s' AND (lock_status='I' OR lock_expire_time < %d)";

        return String.format(sqlFormat, LockMysqlConst.DISTRIBUTED_LOCK_T, requestId, expireAt, key, now);
    }

    @Override
    protected String buildUnLockSql(String key, String requestId, ILockSupportContext context) {
        String sqlFormat = "update %s SET lock_status = 'I' where lock_key='%s' and lock_holder='%s'";

        return String.format(sqlFormat, LockMysqlConst.DISTRIBUTED_LOCK_T, key, requestId);
    }

}
