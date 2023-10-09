package com.github.houbb.lock.mysql.bs;

import com.github.houbb.common.cache.api.service.ICommonCacheService;
import com.github.houbb.common.cache.core.service.CommonCacheServiceMap;
import com.github.houbb.id.api.Id;
import com.github.houbb.id.core.core.Ids;
import com.github.houbb.lock.api.core.ILockKeyFormat;
import com.github.houbb.lock.api.core.ILockReleaseFailHandler;
import com.github.houbb.lock.api.core.ILockSupport;
import com.github.houbb.lock.core.bs.LockBs;
import com.github.houbb.lock.core.constant.LockConst;
import com.github.houbb.lock.core.support.format.LockKeyFormat;
import com.github.houbb.lock.core.support.handler.LockReleaseFailHandler;
import com.github.houbb.lock.mysql.support.lock.MysqlLockSupport;

/**
 * @author d
 * @since 1.6.0
 */
public class MySqlLockBs extends LockBs {

    public static MySqlLockBs newInstance() {
        return new MySqlLockBs();
    }

    public MySqlLockBs() {
        init();
    }

    private void init() {
        cache(new CommonCacheServiceMap());

        lockSupport(new MysqlLockSupport());
    }

}
