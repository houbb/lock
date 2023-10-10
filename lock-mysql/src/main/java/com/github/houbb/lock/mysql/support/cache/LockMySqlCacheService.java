package com.github.houbb.lock.mysql.support.cache;

import com.github.houbb.common.cache.api.service.AbstractCommonCacheService;
import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.houbb.jdbc.api.dal.IMapper;
import com.github.houbb.lock.mysql.constant.LockMysqlConst;
import com.github.houbb.lock.mysql.exception.LockMysqlException;
import com.github.houbb.lock.mysql.model.TDistributedLock;

import java.util.List;

public class LockMySqlCacheService extends AbstractCommonCacheService {

    private final IMapper mapper;

    public LockMySqlCacheService(IMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void set(String key, String value, long expireMills) {
        long expireAt = System.currentTimeMillis() + expireMills;
        String sqlFormat = "update %s set lock_expire_time='%d', lock_holder='%s' where lock_key = '%s'";
        String sql = String.format(sqlFormat, LockMysqlConst.DISTRIBUTED_LOCK_T,
                expireAt, value, key);

        executeUpdateWithEx(sql);
    }

    /**
     * // 过期的数据，通过一个定时任务清理，而不是放在核心逻辑中。
     *
     * @param key 键
     * @param value 值
     * @param nxxx NX
     * @param expx PX
     * @param time 过期时间，单位毫秒
     * @return
     */
    @Override
    public String set(String key, String value, String nxxx, String expx, int time) {
        // 通过唯一约束直接插入
        long expireAt = System.currentTimeMillis() + time;
        String sqlFormat = "insert into %s (lock_key, lock_holder, lock_expire_time) values ('%s', '%s', '%d')";
        String sql = String.format(sqlFormat, LockMysqlConst.DISTRIBUTED_LOCK_T,
                key, value, expireAt);

        int result = mapper.executeUpdate(sql);
        if(result == 1) {
            return LockMysqlConst.SUCCESS;
        }

        return LockMysqlConst.FAIL;
    }

    @Override
    public String get(String key) {
        TDistributedLock distributedLock = queryByKey(key);
        if(distributedLock != null) {
            return distributedLock.getLockHolder();
        }

        return null;
    }

    @Override
    public boolean contains(String key) {
        String sqlFormat = "select count(*) from %s where lock_key = '%s'";
        String sql = String.format(sqlFormat, LockMysqlConst.DISTRIBUTED_LOCK_T, key);

        //TODO: 添加第二个默认值 添加 map 的查询结果
        long count = mapper.selectCount(sql, null);
        return count > 0;
    }

    @Override
    public void remove(String key) {
        String sql = String.format("delete from %s where lock_key='%s'", LockMysqlConst.DISTRIBUTED_LOCK_T,
                key);

        executeUpdateWithEx(sql);
    }

    @Override
    public long ttl(String key) {
        long expireAt = expireAt(key);
        long currentTime = System.currentTimeMillis();

        return Math.max(0, expireAt - currentTime);
    }

    @Override
    public void expireAt(String key, long unixTime) {
        String sqlFormat = "update %s set lock_expire_time='%d' where lock_key = '%s'";
        String sql = String.format(sqlFormat, LockMysqlConst.DISTRIBUTED_LOCK_T,
                unixTime, key);

        executeUpdateWithEx(sql);
    }

    @Override
    public long expireAt(String key) {
        TDistributedLock distributedLock = queryByKey(key);
        if(distributedLock != null) {
            return distributedLock.getLockExpireTime();
        }

        return 0;
    }

    @Override
    public Object eval(String script, int keyCount, String... params) {
        // 直接执行
        return mapper.executeUpdate(script);
    }

    private int executeUpdateWithEx(String sql) {
        int result = mapper.executeUpdate(sql);
        if(result != 1) {
            throw new LockMysqlException("执行更新失败");
        }
        return result;
    }

    private TDistributedLock queryByKey(String key) {
        String sqlFormat = "select * from %s where lock_key = '%s'";
        String sql = String.format(sqlFormat, LockMysqlConst.DISTRIBUTED_LOCK_T, key);

        List<TDistributedLock> lockList = mapper.selectList(sql, TDistributedLock.class);
        if(CollectionUtil.isNotEmpty(lockList)) {
            return lockList.get(0);
        }

        return null;
    }

}
