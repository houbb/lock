package com.github.houbb.lock.mysql.constant;

import com.github.houbb.redis.config.core.constant.JedisConst;

public class LockMysqlConst {

    /**
     * 表名称
     */
    public static final String DISTRIBUTED_LOCK_T = "t_distributed_lock";

    public static final String FAIL = "fail";

    public static final String SUCCESS = JedisConst.OK;

}
