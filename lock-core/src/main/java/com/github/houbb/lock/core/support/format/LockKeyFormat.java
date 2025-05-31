package com.github.houbb.lock.core.support.format;

import com.github.houbb.lock.api.core.ILockKeyFormat;
import com.github.houbb.lock.api.core.ILockKeyFormatContext;
import com.github.houbb.lock.core.constant.LockConst;

/**
 * 简单的格式化处理
 * @since 1.2.0
 * @author dh
 */
public class LockKeyFormat implements ILockKeyFormat {

    private final String namespace;

    public LockKeyFormat(String namespace) {
        this.namespace = namespace;
    }

    public LockKeyFormat() {
        this(LockConst.DEFAULT_LOCK_KEY_NAMESPACE);
    }

    @Override
    public String format(ILockKeyFormatContext formatContext) {
        String rawKey = formatContext.rawKey();
        String format = "%s:%s";

        return String.format(format, namespace, rawKey);
    }

}
