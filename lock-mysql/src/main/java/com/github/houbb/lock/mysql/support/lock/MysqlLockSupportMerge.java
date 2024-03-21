package com.github.houbb.lock.mysql.support.lock;

import com.github.houbb.jdbc.api.dal.IMapper;
import com.github.houbb.lock.api.core.ILockSupportContext;
import com.github.houbb.lock.mysql.constant.LockMysqlConst;

/**
 * 预设 Key 对应的记录已经插入，这里只需要进行更新。
 *
 * 场景：不知道是否已经提前设置好 task，好处是不强制依赖 task 的初始化。
 *
 * @author d
 * @since 1.6.0
 */
public class MysqlLockSupportMerge extends AbstractMysqlLockSupport {

    private final boolean concurrency;

    public MysqlLockSupportMerge(IMapper mapper, boolean concurrency) {
        super(mapper);
        this.concurrency = concurrency;
    }

    public MysqlLockSupportMerge(IMapper mapper) {
        this(mapper, false);
    }

    @Override
    protected String buildLockSql(String key, String requestId, long lockExpireMills, ILockSupportContext context) {
        long count = countByKey(key);

        if(count > 0) {
            log.debug("[Lock] mysql merge mode, count={}, use update", count);

            return buildLockSqlForUpdate(key, requestId, lockExpireMills, context);
        }

        return buildLockSqlForInsert(key, requestId, lockExpireMills, context);
    }

    private String buildLockSqlForUpdate(String key, String requestId, long lockExpireMills, ILockSupportContext context) {
        // 这里需要判断锁已经过期。
        // 或者锁的状态为初始化
        long now = System.currentTimeMillis();
        long expireAt = now + lockExpireMills;

        // 如果是并发
        String sqlFormat = "";
        if(concurrency) {
            sqlFormat = "UPDATE %s SET lock_holder='%s', lock_expire_time=%d, lock_status='P' " +
                    "WHERE lock_key = '%s' AND (lock_status='I' OR lock_expire_time < %d)";
        } else {
            // 必须要求状态为 I
            sqlFormat = "UPDATE %s SET lock_holder='%s', lock_expire_time=%d, lock_status='P' " +
                    "WHERE lock_key = '%s' AND lock_status='I' AND lock_expire_time < %d";
        }

        return String.format(sqlFormat, LockMysqlConst.DISTRIBUTED_LOCK_T, requestId, expireAt, key, now);
    }

    private String buildLockSqlForInsert(String key, String requestId, long lockExpireMills, ILockSupportContext context) {
        // 通过唯一约束直接插入
        long expireAt = System.currentTimeMillis() + lockExpireMills;
        String sqlFormat = "insert into %s (lock_key, lock_holder, lock_expire_time) values ('%s', '%s', '%d')";
        return String.format(sqlFormat, LockMysqlConst.DISTRIBUTED_LOCK_T,
                key, requestId, expireAt);
    }

    protected long countByKey(String key) {
        String sqlFormat = "select count(*) from %s where lock_key = '%s'";
        String sql = String.format(sqlFormat, LockMysqlConst.DISTRIBUTED_LOCK_T, key);

        // TODO: 第二个参数后续可以引入默认值
        return mapper.selectCount(sql);
    }

    @Override
    protected String buildUnLockSql(String key, String requestId, ILockSupportContext context) {
        String sqlFormat = "update %s SET lock_status = 'I' where lock_key='%s' and lock_holder='%s'";

        return String.format(sqlFormat, LockMysqlConst.DISTRIBUTED_LOCK_T, key, requestId);
    }

}
