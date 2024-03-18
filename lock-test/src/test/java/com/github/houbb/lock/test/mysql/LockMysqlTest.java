package com.github.houbb.lock.test.mysql;

import com.github.houbb.lock.api.core.ILockSupport;
import com.github.houbb.lock.core.bs.LockBs;
import com.github.houbb.lock.mysql.support.lock.MysqlLockSupports;
import com.github.houbb.thread.pool.bs.JdbcPoolBs;
import org.junit.Ignore;
import org.junit.Test;

import javax.sql.DataSource;

@Ignore
public class LockMysqlTest {

    @Test
    public void mysqlLockMergeTest() {
        //datasource
        DataSource dataSource = JdbcPoolBs.newInstance()
                .url("jdbc:mysql://127.0.0.1:3306/test")
                .username("admin")
                .password("123456")
                .pooled();

        // 初始化 mysql lock
        ILockSupport lockSupport = MysqlLockSupports.merge(dataSource);

        // 设置引导类
        LockBs lockBs = LockBs.newInstance()
                .lockSupport(lockSupport)
                ;

        final String lockKey = "222";
        try {
            lockBs.tryLock(lockKey);

            // 业务处理
        } finally {
            lockBs.unlock(lockKey);
        }
    }

    //需要提前 插入 key
    //可以在 merge 模式之后执行
    @Test
    public void mysqlLockUpdateTest() {
        //datasource
        DataSource dataSource = JdbcPoolBs.newInstance()
                .url("jdbc:mysql://127.0.0.1:3306/test")
                .username("admin")
                .password("123456")
                .pooled();

        // 初始化 mysql lock
        ILockSupport lockSupport = MysqlLockSupports.update(dataSource);

        // 设置引导类
        LockBs lockBs = LockBs.newInstance()
                .lockSupport(lockSupport)
                ;

        final String lockKey = "222";
        try {
            lockBs.tryLock(lockKey);

            // 业务处理
        } finally {
            lockBs.unlock(lockKey);
        }
    }

    @Test
    public void mysqlLockInsertTest() {
        //datasource
        DataSource dataSource = JdbcPoolBs.newInstance()
                .url("jdbc:mysql://127.0.0.1:3306/test")
                .username("admin")
                .password("123456")
                .pooled();

        // 初始化 mysql lock
        ILockSupport lockSupport = MysqlLockSupports.insert(dataSource);

        // 设置引导类
        LockBs lockBs = LockBs.newInstance()
                .lockSupport(lockSupport)
                ;

        final String lockKey = "insertTest";
        try {
            lockBs.tryLock(lockKey);

            // 业务处理
        } finally {
            lockBs.unlock(lockKey);
        }
    }


}
