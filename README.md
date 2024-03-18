# 项目简介

[lock](https://github.com/houbb/lock) 为 java 设计的渐进式分布式锁，开箱即用，纵享丝滑。

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.houbb/lock/badge.svg)](http://mvnrepository.com/artifact/com.github.houbb/lock)
[![Build Status](https://www.travis-ci.org/houbb/lock.svg?branch=master)](https://www.travis-ci.org/houbb/lock?branch=master)
[![Coverage Status](https://coveralls.io/repos/github/houbb/lock/badge.svg?branch=master)](https://coveralls.io/github/houbb/lock?branch=master)

开源地址：[https://github.com/houbb/lock](https://github.com/houbb/lock) 

## 目的

- 开箱即用，支持注解式和过程式调用 

- 支持可重入锁获取

- 基于 mysql 的分布式锁

- 基于 redis 的分布式锁

- 内置支持多种 redis 的整合方式

- 渐进式设计，可独立于 spring 使用

- 整合 spring

- 整合 spring-boot

# 变更日志

> [变更日志](CHANGELOG.md)

# 快速开始 

## 需要 

jdk1.7+

maven 3.x+

## 基于 mysql 的分布式锁

有时候我们的应用架构没有引入 redis，对于分布式锁的性能要求够用就行，可以通过 mysql 等数据库来实现分布式锁。

### mysql 创建表

选择自己的库，创建 mysql 对应的锁表

```sql
DROP TABLE IF EXISTS t_distributed_lock;
CREATE TABLE t_distributed_lock
(
    id               bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    lock_key         varchar(128) NOT NULL COMMENT '唯一约束',
    lock_holder      varchar(32)  NOT NULL DEFAULT '' COMMENT '锁的持有者标识',
    lock_expire_time bigint(20) NOT NULL DEFAULT 0 COMMENT '锁的到期时间',
    lock_status      char(1)      NOT NULL DEFAULT 'I' COMMENT '锁状态 I:初始化;P:处理中',
    create_user      varchar(32)  NOT NULL DEFAULT '' COMMENT '创建者',
    update_user      varchar(32)  NOT NULL DEFAULT '' COMMENT '更新者',
    create_time      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_lock_key (lock_key)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='数据库分布式锁表';
```

### maven 引入

```xml
<dependency>
    <groupId>com.github.houbb</groupId>
    <artifactId>lock-mysql</artifactId>
    <version>1.7.0</version>
</dependency>
```

### 执行脚本

首先在加锁的库执行建表语句：

```sql
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
```

### 入门例子

代码例子

`MysqlLockSupports.merge(dataSource)` 用于指定 mysql 锁的模式。

insert: 通过插入的方式抢占锁，删除数据释放锁。适合 key 一次性的锁资源增强。比如交易中等一次性的锁抢占。
update: 预设 Key 对应的记录已经提前插入，通过更新状态控制锁的争抢和释放。适合一些固定的任务等。
merge: key 可以不提前初始化，通过更新状态控制锁的争抢和释放。适合一些固定的任务等。

```java
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
```

## 基于 redis 的分布式锁

### maven 引入 

```xml
<dependency>
    <groupId>com.github.houbb</groupId>
    <artifactId>lock-core</artifactId>
    <version>1.7.0</version>
</dependency>
```

### 入门例子

基于本地 redis 的测试案例。

```java
public void helloTest() {
    ILock lock = LockBs.newInstance();
    String key = "ddd";
    try {
        // 加锁
        boolean lockFlag = lock.tryLock(key);
        System.out.println("业务处理");
    } catch (Exception e) {
        throw new RuntimeException(e);
    } finally {
        // 释放锁
        lock.unlock(key);
    }
}
```

## tryLock() 方法说明

尝试获取指定 key 的锁，返回是否成功。

```java
    /**
     * 尝试获取锁，避免参数过多
     * @param context 上下文
     * @return 结果
     */
    boolean tryLock(final ILockContext context);

    /**
     * 尝试加锁，如果失败，在 waitLockTime 到达之前，会一直尝试。
     *
     * @param key key
     * @param timeUnit 时间单位
     * @param waitLockTime 等待锁时间
     * @param lockTime 加锁时间
     * @param reentrant 是否可以重入获取
     * @return 返回
     */
    boolean tryLock(String key, TimeUnit timeUnit, long lockTime, long waitLockTime, boolean reentrant);

    /**
     * 尝试加锁。reentrant=true，默认可重入
     *
     * @param key key
     * @param timeUnit 时间单位
     * @param waitLockTime 等待锁时间
     * @param lockTime 加锁时间
     * @return 返回
     */
    boolean tryLock(String key, TimeUnit timeUnit, long lockTime, long waitLockTime);

    /**
     * 尝试加锁。waitLockTime=0，只进行一次尝试。
     * @param key key
     * @param timeUnit 时间单位
     * @param lockTime 加锁时间
     * @return 返回
     */
    boolean tryLock(String key, TimeUnit timeUnit, long lockTime);

    /**
     * 尝试加锁。timeUnit = TimeUnit.SECONDS，时间单位默认为秒。
     * @param key key
     * @param lockTime 加锁时间
     * @return 返回
     */
    boolean tryLock(String key, long lockTime);

    /**
     * 尝试加锁。lockTime=10，默认等待10S
     * @param key key
     * @return 返回
     */
    boolean tryLock(String key);
```

提供了较多方法，只是为了使用更加便捷。

`boolean tryLock(final ILockContext context);` 中的入参，和参数一一对应，默认值也相同。

context 的引入，为了避免后续的配置项较多，方法会膨胀的问题。

```java
ILockContext lockContext = LockContext.newInstance()
        .key(key)
        .lockTime(LockConst.DEFAULT_LOCK_TIME)
        .waitLockTime(LockConst.DEFAULT_WAIT_LOCK_TIME)
        .reentrant(LockConst.DEFAULT_REENTRANT)
        .timeUnit(LockConst.DEFAULT_TIME_UNIT);

boolean lockFlag = lock.tryLock(lockContext);
```

## unlock() 方法说明

释放指定 key 的锁，返回是否成功。

```java
/**
 * 解锁
 *
 * ps: 目前释放锁不会进行重试。所有的 key 有过期时间。
 * @param key key
 * @return 是否释放锁成功
 */
boolean unlock(String key);
```

建议在 finally 中调用，保障锁的正常释放。

## 锁的可重入性

> 概念：可重入性（reentrancy）是指一个线程在拥有一个资源（通常是一个锁）的情况下，再次获取该资源时不会造成死锁。可重入性在多线程编程中非常重要，因为它可以避免因线程之间的相互依赖而导致的死锁。

### 可重入的例子

我们支持一个线程可以多次获取一个锁，默认是可重入的。

```java
@Test
public void reTest() {
    ILock lock = LockBs.newInstance();
    String key = "ddd";
    try {
        // 加锁
        boolean lockFlag = lock.tryLock(key);
        //1. 首次获取锁成功
        Assert.assertTrue(lockFlag);
        //2. 重新获取锁成功
        boolean reLockFlag = lock.tryLock(key);
        Assert.assertTrue(reLockFlag);
    } catch (Exception e) {
        throw new RuntimeException(e);
    } finally {
        // 释放锁
        lock.unlock(key);
    }
}
```

### 不可重入的例子

当然，我们也支持不可重入的形式，指定对应的配置即可。

```java
public void noReTest() {
    ILock lock = LockBs.newInstance();
    String key = "ddd";
    try {
        ILockContext lockContext = LockContext.newInstance()
                .key(key)
                .waitLockTime(5)
                .reentrant(false);  // 指定不可重入

        boolean lockFlag = lock.tryLock(lockContext);
        //1. 首次获取锁成功
        Assert.assertTrue(lockFlag);
        //2. 不是重入，第二次获取失败
        boolean reLockFlag = lock.tryLock(lockContext);
        Assert.assertFalse(reLockFlag);
    } catch (Exception e) {
        throw new RuntimeException(e);
    } finally {
        // 释放锁
        lock.unlock(key);
    }
}
```

不可重入锁，第二次获取就会失败。

## 配置化

为了便于拓展，LockBs 的配置支持自定义：

```java
LockBs.newInstance()
        .id(Ids.uuid32())   //id 生成策略
        .cache(JedisRedisServiceFactory.pooled("127.0.0.1", 6379)) //缓存策略
        .lockSupport(new RedisLockSupport())    // 锁实现策略
        .lockKeyFormat(new LockKeyFormat())     // 针对 key 的格式化处理策略
        .lockKeyNamespace(LockConst.DEFAULT_LOCK_KEY_NAMESPACE) // 加锁 key 的命名空间，避免不同应用冲突
        .lockReleaseFailHandler(new LockReleaseFailHandler())   //释放锁失败处理
        ;
```

# 整合 spring

## maven 引入

```xml
<dependency>
    <groupId>com.github.houbb</groupId>
    <artifactId>lock-spring</artifactId>
    <version>1.7.0</version>
</dependency>
```

## 指定 bean 使用

### 启用分布式锁

`@EnableLock` 启用分布式锁。

`@EnableRedisConfig` 启用 redis 的默认配置。

```xml
@Configurable
@ComponentScan(basePackages = "com.github.houbb.lock.test.service")
@EnableLock
@EnableRedisConfig
public class SpringConfig {
}
```

`EnableLock` 注解说明，和引导类对应：

```java
public @interface EnableLock {

    /**
     * 唯一标识生成策略
     * @return 结果
     */
    String id() default "lockId";

    /**
     * 缓存实现策略 bean 名称
     *
     * 默认引入 redis-config 中的配置
     *
     * @return 实现
     */
    String cache() default "springRedisService";

    /**
     * 加锁 key 格式化策略
     * @return 策略
     */
    String lockKeyFormat() default "lockKeyFormat";

    /**
     * 锁 key 的默认命名空间
     * @return 命名空间
     */
    String lockKeyNamespace() default LockConst.DEFAULT_LOCK_KEY_NAMESPACE;

    /**
     * 锁释放失败处理类
     * @return 结果
     */
    String lockReleaseFailHandler() default "lockReleaseFailHandler";

}
```

其中 `springRedisService` 使用的是 [redis-config](https://github.com/houbb/redis-config) 中的实现。

对应注解 `@EnableRedisConfig`，redis 的配置信息如下：

| 配置 | 说明 | 默认值
|:---|:---|:----|
| redis.address | redis 地址 | 127.0.0.1 |
| redis.port | redis 端口 | 6379 |
| redis.password | redis 密码 | |

当然，你可以使用自己想用的其他缓存实现，只需要实现对应的接口标准即可。

### 使用 LockBs

我们可以直接 `LockBs` 的引导类，这种适合一些更加灵活的场景。

```java
@ContextConfiguration(classes = SpringConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SpringServiceRawTest {

    @Autowired
    private UserService userService;

    @Autowired
    private LockBs lockBs;

    @Test
    public void queryLogTest() {
        final String key = "name";
        try {
            boolean lockFlag = lockBs.tryLock(key);
            final String value = userService.rawUserName(1L);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        } finally {
            lockBs.unlock(key);
        }
    }

}
```

## aop 注解使用

### 指定方法注解

当然，我们可以在方法上直接指定注解 `@Lock`，使用更加方便。

直接使用，AOP 切面生效即可。

```java
@Service
public class UserService {

    @Lock
    public String queryUserName(Long userId) {
    }

    @Lock(value = "#user.name")
    public void queryUserName2(User user) {
    }
}
```

`@Lock` 属性说明，value 用于指定 key，支持 SPEL 表达式。

**如果 aop 中拦截获取锁失败，默认会抛出异常。**

其他属性，和引导类的方法参数一一对应。

```java
public @interface Lock {

    /**
     * 缓存的 key 策略，支持 SpEL
     * @return 结果
     */
    String value() default "";

    /**
     * 时间单位
     * @return 单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 等待锁时间
     * @return 等待锁时间
     */
    long waitLockTime() default LockConst.DEFAULT_WAIT_LOCK_TIME;

    /**
     * 业务加锁时间
     * @return 加锁时间
     */
    long lockTime() default LockConst.DEFAULT_LOCK_TIME;

    /**
     * 是否可以重入获取
     * @return 结果
     */
    boolean reentrant() default LockConst.DEFAULT_REENTRANT;

}
```

# spring boot 整合

## maven 引入

```xml
<dependency>
    <groupId>com.github.houbb</groupId>
    <artifactId>lock-springboot-starter</artifactId>
    <version>1.7.0</version>
</dependency>
```

## 使用

所有的配置会自动生效。

使用方式同 spring。

# 后期 Road-MAP

- [x] 支持锁的可重入

持有锁的线程可以多次获取锁

- [x] 分布式锁注解支持

- [ ] spring 的锁支持拓展性扩展，而不是局限于 redis-lock

- [ ] watch-dog，添加锁的自动续租？

# 拓展阅读

[Redis 分布式锁](https://houbb.github.io/2018/09/08/redis-learn-42-distributed-lock-redis)

[java 从零实现 redis 分布式锁](https://houbb.github.io/2018/09/08/redis-learn-43-distributed-lock-redis-java-impl)

# 缓存相关工具

[cache: 手写渐进式 redis](https://github.com/houbb/cache)

[common-cache: 通用缓存标准定义](https://github.com/houbb/common-cache)

[redis-config: 兼容各种常见的 redis 配置模式](https://github.com/houbb/redis-config)

[lock: 开箱即用的分布式锁](https://github.com/houbb/lock)

[resubmit: 防重复提交](https://github.com/houbb/resubmit)

[rate-limit: 限流](https://github.com/houbb/rate-limit/)
