CREATE TABLE t_distributed_lock
(
    id             bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    lock_key       varchar(128)        NOT NULL COMMENT '唯一约束',
    lock_holder    varchar(32)         NOT NULL DEFAULT '' COMMENT '锁的持有者标识',
    lock_expire_time bigint(20)          NOT NULL DEFAULT 0 COMMENT '锁的到期时间',
    create_time    timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_lock_key (lock_key)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='数据库分布式锁表';
