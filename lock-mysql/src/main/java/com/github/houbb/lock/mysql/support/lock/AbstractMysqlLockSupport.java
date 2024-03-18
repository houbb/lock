package com.github.houbb.lock.mysql.support.lock;

import com.github.houbb.jdbc.api.dal.IMapper;
import com.github.houbb.lock.api.core.ILockSupportContext;
import com.github.houbb.lock.core.support.lock.BasicLockSupport;

/**
 * 说明：实时通过插入的方式抢占锁，删除数据释放锁。
 *
 * @author d
 * @since 1.6.0
 */
public abstract class AbstractMysqlLockSupport extends BasicLockSupport {

    protected final IMapper mapper;

    public AbstractMysqlLockSupport(IMapper mapper) {
        this.mapper = mapper;
    }

    protected abstract String  buildLockSql(String key,
                                   String requestId,
                                   long lockExpireMills,
                                   ILockSupportContext context);

    protected abstract String  buildUnLockSql(String key,
                                            String requestId,
                                            ILockSupportContext context);

    protected boolean executeSql(String sql) {
        int result = mapper.executeUpdate(sql);
        if(result == 1) {
            return true;
        }

        return false;
    }

    @Override
    protected boolean doActualLock(String key,
                                   String requestId,
                                   long lockExpireMills,
                                   ILockSupportContext context) {
        String lockSql = buildLockSql(key, requestId, lockExpireMills, context);
        boolean result = executeSql(lockSql);

        log.debug("[LOCK] END LOCK key: {}, requestId: {}, result: {}", key, requestId, result);
        return result;
    }

    @Override
    protected boolean doActualUnLock(String key, String requestId, ILockSupportContext context) {
        String lockSql = buildUnLockSql(key, requestId, context);
        boolean result = executeSql(lockSql);

        log.debug("[LOCK] END UN LOCK key: {}, requestId: {}, result: {}", key, requestId, result);

        return result;
    }

}
