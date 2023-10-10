package com.github.houbb.lock.mysql.bs;

import com.github.houbb.common.cache.api.service.ICommonCacheService;
import com.github.houbb.jdbc.api.dal.IMapper;
import com.github.houbb.jdbc.mapping.bs.JdbcBs;
import com.github.houbb.lock.core.bs.LockBs;
import com.github.houbb.lock.mysql.support.cache.LockMySqlCacheService;
import com.github.houbb.lock.mysql.support.lock.MysqlLockSupport;
import com.github.houbb.thread.pool.bs.JdbcPoolBs;

/**
 * @author d
 * @since 1.6.0
 */
public class LockMySqlBs extends LockBs {

    public static LockMySqlBs newInstance() {
        return new LockMySqlBs();
    }

    private LockMySqlBs() {
    }

    /**
     * 线程池引导类
     */
    private final JdbcPoolBs jdbcPoolBs = JdbcPoolBs.newInstance();

    public LockMySqlBs driverClass(String driverClass) {
        jdbcPoolBs.driverClass(driverClass);
        return this;
    }

    public LockMySqlBs url(String url) {
        jdbcPoolBs.url(url);
        return this;
    }

    public LockMySqlBs username(String username) {
        jdbcPoolBs.username(username);
        return this;
    }

    public LockMySqlBs password(String password) {
        jdbcPoolBs.password(password);
        return this;
    }

    public LockMySqlBs init() {
        // 缓存
        IMapper mapper = JdbcBs.newInstance(jdbcPoolBs.pooled()).initMapper();
        ICommonCacheService commonCacheService = new LockMySqlCacheService(mapper);
        cache(commonCacheService);

        lockSupport(new MysqlLockSupport());

        return this;
    }

}
