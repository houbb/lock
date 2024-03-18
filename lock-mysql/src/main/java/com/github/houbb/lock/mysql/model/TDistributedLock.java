package com.github.houbb.lock.mysql.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 分布式锁独享
 *
 * id             bigint(20) unsigned NOT NULL AUTO_INCREMENT,
 *     lock_key       varchar(128)        NOT NULL COMMENT '唯一约束',
 *     lock_holder    varchar(32)         NOT NULL DEFAULT '' COMMENT '锁的持有者标识',
 *     lock_expire_time bigint(20)          NOT NULL DEFAULT 0 COMMENT '锁的到期时间',
 *     create_time    timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 *     update_time    timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
 *
 */
public class TDistributedLock implements Serializable {

    private Long id;

    private String lockKey;
    private String lockHolder;

    private Long lockExpireTime;

    /**
     * 锁状态
     */
    private String lockStatus;

    private String createUser;
    private String updateUser;
    private Date createTime;
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLockKey() {
        return lockKey;
    }

    public void setLockKey(String lockKey) {
        this.lockKey = lockKey;
    }

    public String getLockHolder() {
        return lockHolder;
    }

    public void setLockHolder(String lockHolder) {
        this.lockHolder = lockHolder;
    }

    public Long getLockExpireTime() {
        return lockExpireTime;
    }

    public void setLockExpireTime(Long lockExpireTime) {
        this.lockExpireTime = lockExpireTime;
    }

    public String getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(String lockStatus) {
        this.lockStatus = lockStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    @Override
    public String toString() {
        return "TDistributedLock{" +
                "id=" + id +
                ", lockKey='" + lockKey + '\'' +
                ", lockHolder='" + lockHolder + '\'' +
                ", lockExpireTime=" + lockExpireTime +
                ", lockStatus='" + lockStatus + '\'' +
                ", createUser='" + createUser + '\'' +
                ", updateUser='" + updateUser + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

}
