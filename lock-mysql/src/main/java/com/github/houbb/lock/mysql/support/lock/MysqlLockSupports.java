package com.github.houbb.lock.mysql.support.lock;

import com.github.houbb.jdbc.api.dal.IMapper;
import com.github.houbb.jdbc.common.dal.DefaultMapper;
import com.github.houbb.lock.api.core.ILockSupport;

import javax.sql.DataSource;

public class MysqlLockSupports {

    private static IMapper buildDefaultMapper(final DataSource dataSource) {
        return new DefaultMapper(dataSource);
    }

    public static ILockSupport insert(final DataSource dataSource) {
        return insert(buildDefaultMapper(dataSource));
    }

    public static ILockSupport insert(final IMapper mapper) {
        return new MysqlLockSupportInsert(mapper);
    }

    public static ILockSupport update(final DataSource dataSource, boolean concurrency) {
        return update(buildDefaultMapper(dataSource), concurrency);
    }

    public static ILockSupport update(final DataSource dataSource) {
        return update(dataSource, false);
    }

    public static ILockSupport update(final IMapper mapper) {
        return new MysqlLockSupportUpdate(mapper);
    }

    public static ILockSupport update(final IMapper mapper, boolean concurrency) {
        return new MysqlLockSupportUpdate(mapper, concurrency);
    }

    public static ILockSupport merge(final DataSource dataSource) {
        return merge(buildDefaultMapper(dataSource));
    }

    public static ILockSupport merge(final DataSource dataSource, boolean concurrency) {
        return merge(buildDefaultMapper(dataSource), concurrency);
    }

    public static ILockSupport merge(final IMapper mapper, boolean concurrency) {
        return new MysqlLockSupportMerge(mapper, concurrency);
    }

    public static ILockSupport merge(final IMapper mapper) {
        return merge(mapper, false);
    }

}
