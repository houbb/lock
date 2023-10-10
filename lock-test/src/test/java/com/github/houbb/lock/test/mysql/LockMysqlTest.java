package com.github.houbb.lock.test.mysql;

import com.github.houbb.lock.mysql.bs.LockMySqlBs;
import org.junit.Test;

public class LockMysqlTest {

    @Test
    public void mysqlLockTest() {
        LockMySqlBs lockMySqlBs = LockMySqlBs.newInstance()
                .url("jdbc:mysql://127.0.0.1:3306/test")
                .password("123456")
                .init();

        final String lockKey = "222";
        try {
            lockMySqlBs.tryLock(lockKey);

            // 业务处理
        } finally {
            lockMySqlBs.unlock(lockKey);
        }
    }

}
