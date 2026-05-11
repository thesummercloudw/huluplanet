# 呼噜星球 · Plan 1: 地基模块 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 搭建呼噜星球项目基础架构，实现微信登录、JWT 鉴权、家庭管理、猫咪 CRUD 全链路闭环。

**Architecture:** Spring Boot 3.x 后端提供 RESTful API，微信小程序原生前端消费。JWT 无状态鉴权 + Redis 缓存 token 黑名单。家庭隔离通过 HTTP Header `X-Family-Id` + 拦截器实现。MyBatis-Plus 操作 MySQL 8.0。

**Tech Stack:** Java 17, Spring Boot 3.2, MyBatis-Plus 3.5, MySQL 8.0, Redis 7, 微信小程序原生（wxml/wxss/JS）, MobX-miniprogram

---

## 文件结构

### 后端 (catplanet-server)

```
catplanet-server/
├── pom.xml
├── src/main/java/com/catplanet/
│   ├── CatplanetApplication.java
│   ├── common/
│   │   ├── result/Result.java                    # 统一响应包装
│   │   ├── result/ResultCode.java                # 响应码枚举
│   │   ├── exception/BizException.java           # 业务异常
│   │   ├── exception/GlobalExceptionHandler.java # 全局异常处理
│   │   ├── config/RedisConfig.java               # Redis 配置
│   │   ├── config/WebMvcConfig.java              # 拦截器注册
│   │   ├── config/MybatisPlusConfig.java         # MP 配置（自动填充等）
│   │   ├── interceptor/JwtAuthInterceptor.java   # JWT 鉴权拦截器
│   │   ├── interceptor/FamilyIsolationInterceptor.java # 家庭隔离拦截器
│   │   ├── util/JwtUtil.java                     # JWT 工具类
│   │   ├── util/SnowflakeIdGenerator.java        # 雪花 ID 生成器
│   │   └── context/UserContext.java              # 线程级用户上下文
│   ├── module/
│   │   ├── auth/
│   │   │   ├── controller/AuthController.java
│   │   │   ├── service/AuthService.java
│   │   │   ├── service/impl/AuthServiceImpl.java
│   │   │   ├── dto/WxLoginRequest.java
│   │   │   └── dto/LoginResponse.java
│   │   ├── user/
│   │   │   ├── controller/UserController.java
│   │   │   ├── service/UserService.java
│   │   │   ├── service/impl/UserServiceImpl.java
│   │   │   ├── mapper/UserMapper.java
│   │   │   ├── entity/User.java
│   │   │   └── dto/UserProfileResponse.java
│   │   ├── family/
│   │   │   ├── controller/FamilyController.java
│   │   │   ├── service/FamilyService.java
│   │   │   ├── service/impl/FamilyServiceImpl.java
│   │   │   ├── mapper/FamilyMapper.java
│   │   │   ├── mapper/FamilyMemberMapper.java
│   │   │   ├── entity/Family.java
│   │   │   ├── entity/FamilyMember.java
│   │   │   ├── dto/CreateFamilyRequest.java
│   │   │   ├── dto/JoinFamilyRequest.java
│   │   │   └── dto/FamilyDetailResponse.java
│   │   └── cat/
│   │       ├── controller/CatController.java
│   │       ├── service/CatService.java
│   │       ├── service/impl/CatServiceImpl.java
│   │       ├── mapper/CatMapper.java
│   │       ├── entity/Cat.java
│   │       ├── dto/CreateCatRequest.java
│   │       ├── dto/UpdateCatRequest.java
│   │       └── dto/CatDetailResponse.java
│   └── resources/
│       ├── application.yml
│       ├── application-dev.yml
│       └── db/migration/V1__init_schema.sql
└── src/test/java/com/catplanet/
    ├── module/auth/AuthControllerTest.java
    ├── module/family/FamilyServiceTest.java
    └── module/cat/CatServiceTest.java
```

### 前端 (catplanet-mini)

```
catplanet-mini/
├── app.js
├── app.json
├── app.wxss
├── project.config.json
├── sitemap.json
├── utils/
│   ├── request.js          # 封装 wx.request + JWT
│   ├── auth.js             # 登录态管理
│   └── store.js            # MobX store 初始化
├── store/
│   ├── user.js             # 用户状态
│   └── family.js           # 家庭状态
├── pages/
│   ├── login/
│   │   ├── login.wxml
│   │   ├── login.wxss
│   │   ├── login.js
│   │   └── login.json
│   ├── onboarding/
│   │   ├── onboarding.wxml
│   │   ├── onboarding.wxss
│   │   ├── onboarding.js
│   │   └── onboarding.json
│   ├── planet/
│   │   ├── planet.wxml
│   │   ├── planet.wxss
│   │   ├── planet.js
│   │   └── planet.json
│   └── cat-detail/
│       ├── cat-detail.wxml
│       ├── cat-detail.wxss
│       ├── cat-detail.js
│       └── cat-detail.json
└── components/
    ├── cat-card/
    │   ├── cat-card.wxml
    │   ├── cat-card.wxss
    │   ├── cat-card.js
    │   └── cat-card.json
    ├── family-chip/
    │   ├── family-chip.wxml
    │   ├── family-chip.wxss
    │   ├── family-chip.js
    │   └── family-chip.json
    └── tab-bar/
        ├── tab-bar.wxml
        ├── tab-bar.wxss
        ├── tab-bar.js
        └── tab-bar.json
```

---

## Task 1: 后端项目脚手架

**Files:**
- Create: `catplanet-server/pom.xml`
- Create: `catplanet-server/src/main/java/com/catplanet/CatplanetApplication.java`
- Create: `catplanet-server/src/main/resources/application.yml`
- Create: `catplanet-server/src/main/resources/application-dev.yml`

- [ ] **Step 1: 创建 pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
    </parent>

    <groupId>com.catplanet</groupId>
    <artifactId>catplanet-server</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>catplanet-server</name>
    <description>呼噜星球后端服务</description>

    <properties>
        <java.version>17</java.version>
        <mybatis-plus.version>3.5.6</mybatis-plus.version>
        <jjwt.version>0.12.5</jjwt.version>
    </properties>

    <dependencies>
        <!-- Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <!-- Redis -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <!-- MyBatis-Plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>
        <!-- MySQL -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jjwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <!-- HTTP Client (调用微信接口) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>
        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!-- H2 for test -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: 创建启动类**

```java
package com.catplanet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CatplanetApplication {
    public static void main(String[] args) {
        SpringApplication.run(CatplanetApplication.class, args);
    }
}
```

- [ ] **Step 3: 创建 application.yml**

```yaml
server:
  port: 8080

spring:
  profiles:
    active: dev
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: Asia/Shanghai
    default-property-inclusion: non_null

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: assign_id
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

catplanet:
  jwt:
    secret: ${JWT_SECRET:catplanet-dev-secret-key-must-be-at-least-256-bits-long}
    expiration: 604800000  # 7 天
  wx:
    appid: ${WX_APPID:wx_test_appid}
    secret: ${WX_SECRET:wx_test_secret}
```

- [ ] **Step 4: 创建 application-dev.yml**

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/catplanet?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: ${DB_PASSWORD:root}
    driver-class-name: com.mysql.cj.jdbc.Driver
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
```

- [ ] **Step 5: 验证项目编译通过**

Run: `cd catplanet-server && mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 6: Commit**

```bash
git add catplanet-server/
git commit -m "chore: init Spring Boot 3.2 project scaffold"
```

---

## Task 2: 数据库 Schema + 通用基础设施

**Files:**
- Create: `catplanet-server/src/main/resources/db/migration/V1__init_schema.sql`
- Create: `catplanet-server/src/main/java/com/catplanet/common/result/Result.java`
- Create: `catplanet-server/src/main/java/com/catplanet/common/result/ResultCode.java`
- Create: `catplanet-server/src/main/java/com/catplanet/common/exception/BizException.java`
- Create: `catplanet-server/src/main/java/com/catplanet/common/exception/GlobalExceptionHandler.java`
- Create: `catplanet-server/src/main/java/com/catplanet/common/util/SnowflakeIdGenerator.java`
- Create: `catplanet-server/src/main/java/com/catplanet/common/context/UserContext.java`
- Create: `catplanet-server/src/main/java/com/catplanet/common/config/MybatisPlusConfig.java`
- Create: `catplanet-server/src/main/java/com/catplanet/common/config/RedisConfig.java`

- [ ] **Step 1: 创建数据库初始化 SQL**

```sql
-- V1__init_schema.sql
CREATE TABLE IF NOT EXISTS `user` (
    `user_id`    BIGINT       NOT NULL COMMENT '雪花ID',
    `openid`     VARCHAR(64)  NOT NULL COMMENT '微信openid',
    `unionid`    VARCHAR(64)  DEFAULT NULL COMMENT '微信unionid',
    `nickname`   VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    `avatar`     VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
    `phone`      VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`    TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `family` (
    `family_id`   BIGINT      NOT NULL COMMENT '雪花ID',
    `name`        VARCHAR(64) NOT NULL COMMENT '家庭名',
    `cover_emoji` VARCHAR(8)  DEFAULT '🏠' COMMENT '封面emoji',
    `creator_id`  BIGINT      NOT NULL COMMENT '创建者ID',
    `invite_code` VARCHAR(16) NOT NULL COMMENT '邀请码',
    `created_at`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT     NOT NULL DEFAULT 0,
    PRIMARY KEY (`family_id`),
    UNIQUE KEY `uk_invite_code` (`invite_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家庭表';

CREATE TABLE IF NOT EXISTS `family_member` (
    `id`                  BIGINT      NOT NULL AUTO_INCREMENT,
    `family_id`           BIGINT      NOT NULL,
    `user_id`             BIGINT      NOT NULL,
    `role`                VARCHAR(16) NOT NULL DEFAULT 'member' COMMENT 'owner/admin/member',
    `nickname_in_family`  VARCHAR(32) DEFAULT NULL COMMENT '家庭内昵称',
    `joined_at`           DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_family_user` (`family_id`, `user_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家庭成员关系表';

CREATE TABLE IF NOT EXISTS `cat` (
    `cat_id`          BIGINT       NOT NULL COMMENT '雪花ID',
    `family_id`       BIGINT       NOT NULL COMMENT '所属家庭',
    `name`            VARCHAR(32)  NOT NULL,
    `avatar`          VARCHAR(512) DEFAULT NULL,
    `breed`           VARCHAR(32)  DEFAULT NULL COMMENT '品种',
    `gender`          VARCHAR(8)   DEFAULT 'unknown' COMMENT 'male/female/unknown',
    `birthday`        DATE         DEFAULT NULL,
    `is_neutered`     TINYINT      DEFAULT 0 COMMENT '0/1',
    `weight_kg`       DECIMAL(4,2) DEFAULT NULL COMMENT '最新体重',
    `personality_tags` JSON        DEFAULT NULL COMMENT '性格标签数组',
    `adoption_date`   DATE         DEFAULT NULL COMMENT '到家日',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`         TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`cat_id`),
    KEY `idx_family_id` (`family_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='猫咪表';
```

- [ ] **Step 2: 创建统一响应 Result + ResultCode**

```java
// Result.java
package com.catplanet.common.result;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;
    private String traceId;

    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMessage(ResultCode.SUCCESS.getMessage());
        r.setData(data);
        return r;
    }

    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> fail(ResultCode resultCode) {
        Result<T> r = new Result<>();
        r.setCode(resultCode.getCode());
        r.setMessage(resultCode.getMessage());
        return r;
    }

    public static <T> Result<T> fail(int code, String message) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }
}
```

```java
// ResultCode.java
package com.catplanet.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {
    SUCCESS(0, "success"),
    UNAUTHORIZED(401, "未登录或 token 已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    BAD_REQUEST(400, "请求参数错误"),
    FAMILY_NOT_FOUND(10001, "家庭不存在"),
    FAMILY_MEMBER_EXISTS(10002, "已是家庭成员"),
    INVITE_CODE_INVALID(10003, "邀请码无效"),
    CAT_NOT_FOUND(10004, "猫咪不存在"),
    WX_LOGIN_FAIL(10005, "微信登录失败"),
    INTERNAL_ERROR(500, "服务器内部错误");

    private final int code;
    private final String message;
}
```

- [ ] **Step 3: 创建业务异常 + 全局异常处理**

```java
// BizException.java
package com.catplanet.common.exception;

import com.catplanet.common.result.ResultCode;
import lombok.Getter;

@Getter
public class BizException extends RuntimeException {
    private final ResultCode resultCode;

    public BizException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public BizException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }
}
```

```java
// GlobalExceptionHandler.java
package com.catplanet.common.exception;

import com.catplanet.common.result.Result;
import com.catplanet.common.result.ResultCode;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public Result<?> handleBizException(BizException e) {
        log.warn("BizException: {}", e.getMessage());
        return Result.fail(e.getResultCode().getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .findFirst().orElse("参数错误");
        return Result.fail(ResultCode.BAD_REQUEST.getCode(), msg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleConstraintViolation(ConstraintViolationException e) {
        return Result.fail(ResultCode.BAD_REQUEST.getCode(), e.getMessage());
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBindException(BindException e) {
        String msg = e.getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .findFirst().orElse("参数绑定错误");
        return Result.fail(ResultCode.BAD_REQUEST.getCode(), msg);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e) {
        log.error("Unhandled exception", e);
        return Result.fail(ResultCode.INTERNAL_ERROR);
    }
}
```

- [ ] **Step 4: 创建 UserContext（线程级用户上下文）**

```java
// UserContext.java
package com.catplanet.common.context;

public class UserContext {
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> FAMILY_ID = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static void setFamilyId(Long familyId) {
        FAMILY_ID.set(familyId);
    }

    public static Long getFamilyId() {
        return FAMILY_ID.get();
    }

    public static void clear() {
        USER_ID.remove();
        FAMILY_ID.remove();
    }
}
```

- [ ] **Step 5: 创建 SnowflakeIdGenerator**

```java
// SnowflakeIdGenerator.java
package com.catplanet.common.util;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class SnowflakeIdGenerator implements IdentifierGenerator {

    private static final long EPOCH = 1704067200000L; // 2024-01-01 00:00:00
    private static final long WORKER_BITS = 10L;
    private static final long SEQUENCE_BITS = 12L;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;
    private static final long WORKER_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_BITS;

    private final long workerId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SnowflakeIdGenerator() {
        this.workerId = 1L;
    }

    @Override
    public synchronized Number nextId(Object entity) {
        long now = System.currentTimeMillis();
        if (now == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                now = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = now;
        return ((now - EPOCH) << TIMESTAMP_SHIFT) | (workerId << WORKER_SHIFT) | sequence;
    }

    private long waitNextMillis(long lastTimestamp) {
        long now = System.currentTimeMillis();
        while (now <= lastTimestamp) {
            now = System.currentTimeMillis();
        }
        return now;
    }
}
```

- [ ] **Step 6: 创建 MybatisPlusConfig + RedisConfig**

```java
// MybatisPlusConfig.java
package com.catplanet.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

```java
// RedisConfig.java
package com.catplanet.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}
```

- [ ] **Step 7: 验证编译通过**

Run: `cd catplanet-server && mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 8: Commit**

```bash
git add .
git commit -m "feat: add common infrastructure (Result, Exception, UserContext, Config)"
```

---

## Task 3: JWT 工具类 + 鉴权拦截器

**Files:**
- Create: `catplanet-server/src/main/java/com/catplanet/common/util/JwtUtil.java`
- Create: `catplanet-server/src/main/java/com/catplanet/common/interceptor/JwtAuthInterceptor.java`
- Create: `catplanet-server/src/main/java/com/catplanet/common/interceptor/FamilyIsolationInterceptor.java`
- Create: `catplanet-server/src/main/java/com/catplanet/common/config/WebMvcConfig.java`
- Test: `catplanet-server/src/test/java/com/catplanet/common/util/JwtUtilTest.java`

- [ ] **Step 1: 编写 JwtUtil 单元测试**

```java
package com.catplanet.common.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        jwtUtil.setSecret("catplanet-test-secret-key-must-be-at-least-256-bits-long-enough");
        jwtUtil.setExpiration(3600000L); // 1h
    }

    @Test
    void generateAndParse_validToken() {
        Long userId = 123456789L;
        String token = jwtUtil.generateToken(userId);

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
        assertEquals(userId, jwtUtil.getUserIdFromToken(token));
    }

    @Test
    void validateToken_expiredToken_returnsFalse() {
        JwtUtil shortLived = new JwtUtil();
        shortLived.setSecret("catplanet-test-secret-key-must-be-at-least-256-bits-long-enough");
        shortLived.setExpiration(1L); // 1ms

        String token = shortLived.generateToken(1L);
        try { Thread.sleep(10); } catch (InterruptedException ignored) {}

        assertFalse(shortLived.validateToken(token));
    }

    @Test
    void validateToken_invalidToken_returnsFalse() {
        assertFalse(jwtUtil.validateToken("invalid.token.here"));
    }
}
```

- [ ] **Step 2: 运行测试确认失败**

Run: `cd catplanet-server && mvn test -pl . -Dtest=JwtUtilTest -q`
Expected: FAIL (JwtUtil 不存在)

- [ ] **Step 3: 实现 JwtUtil**

```java
package com.catplanet.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Setter
@Component
public class JwtUtil {

    @Value("${catplanet.jwt.secret}")
    private String secret;

    @Value("${catplanet.jwt.expiration}")
    private Long expiration;

    public String generateToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
```

- [ ] **Step 4: 运行测试确认通过**

Run: `cd catplanet-server && mvn test -pl . -Dtest=JwtUtilTest -q`
Expected: PASS (3 tests)

- [ ] **Step 5: 实现 JwtAuthInterceptor**

```java
package com.catplanet.common.interceptor;

import com.catplanet.common.context.UserContext;
import com.catplanet.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        UserContext.setUserId(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }
}
```

- [ ] **Step 6: 实现 FamilyIsolationInterceptor**

```java
package com.catplanet.common.interceptor;

import com.catplanet.common.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class FamilyIsolationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String familyIdStr = request.getHeader("X-Family-Id");
        if (StringUtils.hasText(familyIdStr)) {
            try {
                Long familyId = Long.parseLong(familyIdStr);
                UserContext.setFamilyId(familyId);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return false;
            }
        }
        // 不要求必传 —— 某些接口（如创建家庭）不需要
        return true;
    }
}
```

- [ ] **Step 7: 注册拦截器 WebMvcConfig**

```java
package com.catplanet.common.config;

import com.catplanet.common.interceptor.FamilyIsolationInterceptor;
import com.catplanet.common.interceptor.JwtAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtAuthInterceptor jwtAuthInterceptor;
    private final FamilyIsolationInterceptor familyIsolationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/v1/auth/**");

        registry.addInterceptor(familyIsolationInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/v1/auth/**");
    }
}
```

- [ ] **Step 8: Commit**

```bash
git add .
git commit -m "feat: add JWT auth + family isolation interceptors"
```

---

## Task 4: 微信登录 + 用户模块

**Files:**
- Create: `catplanet-server/src/main/java/com/catplanet/module/user/entity/User.java`
- Create: `catplanet-server/src/main/java/com/catplanet/module/user/mapper/UserMapper.java`
- Create: `catplanet-server/src/main/java/com/catplanet/module/auth/dto/WxLoginRequest.java`
- Create: `catplanet-server/src/main/java/com/catplanet/module/auth/dto/LoginResponse.java`
- Create: `catplanet-server/src/main/java/com/catplanet/module/auth/service/AuthService.java`
- Create: `catplanet-server/src/main/java/com/catplanet/module/auth/service/impl/AuthServiceImpl.java`
- Create: `catplanet-server/src/main/java/com/catplanet/module/auth/controller/AuthController.java`
- Test: `catplanet-server/src/test/java/com/catplanet/module/auth/AuthControllerTest.java`

- [ ] **Step 1: 创建 User 实体**

```java
package com.catplanet.module.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.ASSIGN_ID)
    private Long userId;
    private String openid;
    private String unionid;
    private String nickname;
    private String avatar;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}
```

- [ ] **Step 2: 创建 UserMapper**

```java
package com.catplanet.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catplanet.module.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
```

- [ ] **Step 3: 创建 Auth DTO**

```java
// WxLoginRequest.java
package com.catplanet.module.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WxLoginRequest {
    @NotBlank(message = "code 不能为空")
    private String code;
    private String nickname;
    private String avatar;
}
```

```java
// LoginResponse.java
package com.catplanet.module.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private Long userId;
    private String nickname;
    private String avatar;
    private Boolean isNewUser;
}
```

- [ ] **Step 4: 创建 AuthService 接口 + 实现**

```java
// AuthService.java
package com.catplanet.module.auth.service;

import com.catplanet.module.auth.dto.LoginResponse;
import com.catplanet.module.auth.dto.WxLoginRequest;

public interface AuthService {
    LoginResponse wxLogin(WxLoginRequest request);
}
```

```java
// AuthServiceImpl.java
package com.catplanet.module.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.ResultCode;
import com.catplanet.common.util.JwtUtil;
import com.catplanet.module.auth.dto.LoginResponse;
import com.catplanet.module.auth.dto.WxLoginRequest;
import com.catplanet.module.auth.service.AuthService;
import com.catplanet.module.user.entity.User;
import com.catplanet.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final WebClient.Builder webClientBuilder;

    @Value("${catplanet.wx.appid}")
    private String wxAppId;

    @Value("${catplanet.wx.secret}")
    private String wxSecret;

    @Override
    public LoginResponse wxLogin(WxLoginRequest request) {
        // 1. 调用微信 code2session
        String openid = code2Session(request.getCode());

        // 2. 查找或创建用户
        boolean isNew = false;
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));

        if (user == null) {
            isNew = true;
            user = new User();
            user.setOpenid(openid);
            user.setNickname(request.getNickname());
            user.setAvatar(request.getAvatar());
            userMapper.insert(user);
        }

        // 3. 生成 JWT
        String token = jwtUtil.generateToken(user.getUserId());

        return LoginResponse.builder()
                .token(token)
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .isNewUser(isNew)
                .build();
    }

    private String code2Session(String code) {
        try {
            String url = String.format(
                    "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    wxAppId, wxSecret, code);

            Map<String, Object> result = webClientBuilder.build()
                    .get().uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (result == null || result.containsKey("errcode") && (int) result.get("errcode") != 0) {
                log.error("微信登录失败: {}", result);
                throw new BizException(ResultCode.WX_LOGIN_FAIL);
            }

            return (String) result.get("openid");
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用微信接口异常", e);
            throw new BizException(ResultCode.WX_LOGIN_FAIL);
        }
    }
}
```

- [ ] **Step 5: 创建 AuthController**

```java
package com.catplanet.module.auth.controller;

import com.catplanet.common.result.Result;
import com.catplanet.module.auth.dto.LoginResponse;
import com.catplanet.module.auth.dto.WxLoginRequest;
import com.catplanet.module.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/wx-login")
    public Result<LoginResponse> wxLogin(@Valid @RequestBody WxLoginRequest request) {
        return Result.ok(authService.wxLogin(request));
    }
}
```

- [ ] **Step 6: 编写集成测试**

```java
package com.catplanet.module.auth;

import com.catplanet.module.auth.service.AuthService;
import com.catplanet.module.auth.dto.LoginResponse;
import com.catplanet.module.auth.dto.WxLoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void wxLogin_validCode_returnsToken() throws Exception {
        LoginResponse resp = LoginResponse.builder()
                .token("test-jwt-token")
                .userId(1L)
                .nickname("测试用户")
                .isNewUser(true)
                .build();
        when(authService.wxLogin(any(WxLoginRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/v1/auth/wx-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"test_wx_code\",\"nickname\":\"测试用户\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.data.isNewUser").value(true));
    }

    @Test
    void wxLogin_missingCode_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/wx-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nickname\":\"test\"}"))
                .andExpect(status().isBadRequest());
    }
}
```

- [ ] **Step 7: 运行测试**

Run: `cd catplanet-server && mvn test -Dtest=AuthControllerTest -q`
Expected: PASS (2 tests)

- [ ] **Step 8: Commit**

```bash
git add .
git commit -m "feat: add WeChat login + user module"
```

---

## Task 5: 家庭管理模块

**Files:**
- Create: `catplanet-server/src/main/java/com/catplanet/module/family/entity/Family.java`
- Create: `catplanet-server/src/main/java/com/catplanet/module/family/entity/FamilyMember.java`
- Create: `catplanet-server/src/main/java/com/catplanet/module/family/mapper/FamilyMapper.java`
- Create: `catplanet-server/src/main/java/com/catplanet/module/family/mapper/FamilyMemberMapper.java`
- Create: `catplanet-server/src/main/java/com/catplanet/module/family/dto/CreateFamilyRequest.java`
- Create: `catplanet-server/src/main/java/com/catplanet/module/family/dto/JoinFamilyRequest.java`
- Create: `catplanet-server/src/main/java/com/catplanet/module/family/dto/FamilyDetailResponse.java`
- Create: `catplanet-server/src/main/java/com/catplanet/module/family/service/FamilyService.java`
- Create: `catplanet-server/src/main/java/com/catplanet/module/family/service/impl/FamilyServiceImpl.java`
- Create: `catplanet-server/src/main/java/com/catplanet/module/family/controller/FamilyController.java`
- Test: `catplanet-server/src/test/java/com/catplanet/module/family/FamilyServiceTest.java`

- [ ] **Step 1: 创建 Family + FamilyMember 实体**

```java
// Family.java
package com.catplanet.module.family.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("family")
public class Family {
    @TableId(type = IdType.ASSIGN_ID)
    private Long familyId;
    private String name;
    private String coverEmoji;
    private Long creatorId;
    private String inviteCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}
```

```java
// FamilyMember.java
package com.catplanet.module.family.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("family_member")
public class FamilyMember {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long familyId;
    private Long userId;
    private String role;  // owner / admin / member
    private String nicknameInFamily;
    private LocalDateTime joinedAt;
}
```

- [ ] **Step 2: 创建 Mapper**

```java
// FamilyMapper.java
package com.catplanet.module.family.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catplanet.module.family.entity.Family;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FamilyMapper extends BaseMapper<Family> {
}
```

```java
// FamilyMemberMapper.java
package com.catplanet.module.family.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catplanet.module.family.entity.FamilyMember;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FamilyMemberMapper extends BaseMapper<FamilyMember> {
}
```

- [ ] **Step 3: 创建 DTO**

```java
// CreateFamilyRequest.java
package com.catplanet.module.family.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateFamilyRequest {
    @NotBlank(message = "家庭名不能为空")
    @Size(max = 64, message = "家庭名最长64字")
    private String name;
    private String coverEmoji;
}
```

```java
// JoinFamilyRequest.java
package com.catplanet.module.family.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinFamilyRequest {
    @NotBlank(message = "邀请码不能为空")
    private String inviteCode;
    private String nicknameInFamily;
}
```

```java
// FamilyDetailResponse.java
package com.catplanet.module.family.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FamilyDetailResponse {
    private Long familyId;
    private String name;
    private String coverEmoji;
    private String inviteCode;
    private List<MemberInfo> members;

    @Data
    @Builder
    public static class MemberInfo {
        private Long userId;
        private String nickname;
        private String avatar;
        private String role;
        private String nicknameInFamily;
    }
}
```

- [ ] **Step 4: 创建 FamilyService 接口**

```java
package com.catplanet.module.family.service;

import com.catplanet.module.family.dto.CreateFamilyRequest;
import com.catplanet.module.family.dto.FamilyDetailResponse;
import com.catplanet.module.family.dto.JoinFamilyRequest;

import java.util.List;

public interface FamilyService {
    FamilyDetailResponse createFamily(Long userId, CreateFamilyRequest request);
    FamilyDetailResponse joinFamily(Long userId, JoinFamilyRequest request);
    FamilyDetailResponse getFamilyDetail(Long familyId);
    List<FamilyDetailResponse> getMyFamilies(Long userId);
    void leaveFamily(Long userId, Long familyId);
    boolean isFamilyMember(Long userId, Long familyId);
}
```

- [ ] **Step 5: 实现 FamilyServiceImpl**

```java
package com.catplanet.module.family.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.ResultCode;
import com.catplanet.module.family.dto.CreateFamilyRequest;
import com.catplanet.module.family.dto.FamilyDetailResponse;
import com.catplanet.module.family.dto.JoinFamilyRequest;
import com.catplanet.module.family.entity.Family;
import com.catplanet.module.family.entity.FamilyMember;
import com.catplanet.module.family.mapper.FamilyMapper;
import com.catplanet.module.family.mapper.FamilyMemberMapper;
import com.catplanet.module.family.service.FamilyService;
import com.catplanet.module.user.entity.User;
import com.catplanet.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FamilyServiceImpl implements FamilyService {

    private final FamilyMapper familyMapper;
    private final FamilyMemberMapper familyMemberMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public FamilyDetailResponse createFamily(Long userId, CreateFamilyRequest request) {
        Family family = new Family();
        family.setName(request.getName());
        family.setCoverEmoji(request.getCoverEmoji() != null ? request.getCoverEmoji() : "🏠");
        family.setCreatorId(userId);
        family.setInviteCode(generateInviteCode());
        familyMapper.insert(family);

        // 创建者自动成为 owner
        FamilyMember member = new FamilyMember();
        member.setFamilyId(family.getFamilyId());
        member.setUserId(userId);
        member.setRole("owner");
        member.setJoinedAt(LocalDateTime.now());
        familyMemberMapper.insert(member);

        return getFamilyDetail(family.getFamilyId());
    }

    @Override
    @Transactional
    public FamilyDetailResponse joinFamily(Long userId, JoinFamilyRequest request) {
        Family family = familyMapper.selectOne(
                new LambdaQueryWrapper<Family>().eq(Family::getInviteCode, request.getInviteCode()));
        if (family == null) {
            throw new BizException(ResultCode.INVITE_CODE_INVALID);
        }

        // 检查是否已是成员
        Long count = familyMemberMapper.selectCount(
                new LambdaQueryWrapper<FamilyMember>()
                        .eq(FamilyMember::getFamilyId, family.getFamilyId())
                        .eq(FamilyMember::getUserId, userId));
        if (count > 0) {
            throw new BizException(ResultCode.FAMILY_MEMBER_EXISTS);
        }

        FamilyMember member = new FamilyMember();
        member.setFamilyId(family.getFamilyId());
        member.setUserId(userId);
        member.setRole("member");
        member.setNicknameInFamily(request.getNicknameInFamily());
        member.setJoinedAt(LocalDateTime.now());
        familyMemberMapper.insert(member);

        return getFamilyDetail(family.getFamilyId());
    }

    @Override
    public FamilyDetailResponse getFamilyDetail(Long familyId) {
        Family family = familyMapper.selectById(familyId);
        if (family == null) {
            throw new BizException(ResultCode.FAMILY_NOT_FOUND);
        }

        List<FamilyMember> members = familyMemberMapper.selectList(
                new LambdaQueryWrapper<FamilyMember>().eq(FamilyMember::getFamilyId, familyId));

        List<FamilyDetailResponse.MemberInfo> memberInfos = members.stream().map(m -> {
            User user = userMapper.selectById(m.getUserId());
            return FamilyDetailResponse.MemberInfo.builder()
                    .userId(m.getUserId())
                    .nickname(user != null ? user.getNickname() : null)
                    .avatar(user != null ? user.getAvatar() : null)
                    .role(m.getRole())
                    .nicknameInFamily(m.getNicknameInFamily())
                    .build();
        }).collect(Collectors.toList());

        return FamilyDetailResponse.builder()
                .familyId(family.getFamilyId())
                .name(family.getName())
                .coverEmoji(family.getCoverEmoji())
                .inviteCode(family.getInviteCode())
                .members(memberInfos)
                .build();
    }

    @Override
    public List<FamilyDetailResponse> getMyFamilies(Long userId) {
        List<FamilyMember> memberships = familyMemberMapper.selectList(
                new LambdaQueryWrapper<FamilyMember>().eq(FamilyMember::getUserId, userId));
        return memberships.stream()
                .map(m -> getFamilyDetail(m.getFamilyId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void leaveFamily(Long userId, Long familyId) {
        familyMemberMapper.delete(
                new LambdaQueryWrapper<FamilyMember>()
                        .eq(FamilyMember::getFamilyId, familyId)
                        .eq(FamilyMember::getUserId, userId));
    }

    @Override
    public boolean isFamilyMember(Long userId, Long familyId) {
        Long count = familyMemberMapper.selectCount(
                new LambdaQueryWrapper<FamilyMember>()
                        .eq(FamilyMember::getFamilyId, familyId)
                        .eq(FamilyMember::getUserId, userId));
        return count > 0;
    }

    private String generateInviteCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
```

- [ ] **Step 6: 创建 FamilyController**

```java
package com.catplanet.module.family.controller;

import com.catplanet.common.context.UserContext;
import com.catplanet.common.result.Result;
import com.catplanet.module.family.dto.CreateFamilyRequest;
import com.catplanet.module.family.dto.FamilyDetailResponse;
import com.catplanet.module.family.dto.JoinFamilyRequest;
import com.catplanet.module.family.service.FamilyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/families")
@RequiredArgsConstructor
public class FamilyController {

    private final FamilyService familyService;

    @PostMapping
    public Result<FamilyDetailResponse> create(@Valid @RequestBody CreateFamilyRequest request) {
        return Result.ok(familyService.createFamily(UserContext.getUserId(), request));
    }

    @PostMapping("/join")
    public Result<FamilyDetailResponse> join(@Valid @RequestBody JoinFamilyRequest request) {
        return Result.ok(familyService.joinFamily(UserContext.getUserId(), request));
    }

    @GetMapping("/mine")
    public Result<List<FamilyDetailResponse>> myFamilies() {
        return Result.ok(familyService.getMyFamilies(UserContext.getUserId()));
    }

    @GetMapping("/{familyId}")
    public Result<FamilyDetailResponse> detail(@PathVariable Long familyId) {
        return Result.ok(familyService.getFamilyDetail(familyId));
    }

    @DeleteMapping("/{familyId}/leave")
    public Result<Void> leave(@PathVariable Long familyId) {
        familyService.leaveFamily(UserContext.getUserId(), familyId);
        return Result.ok();
    }
}
```

- [ ] **Step 7: 编写 FamilyService 单元测试**

```java
package com.catplanet.module.family;

import com.catplanet.common.exception.BizException;
import com.catplanet.module.family.dto.CreateFamilyRequest;
import com.catplanet.module.family.dto.FamilyDetailResponse;
import com.catplanet.module.family.dto.JoinFamilyRequest;
import com.catplanet.module.family.entity.Family;
import com.catplanet.module.family.entity.FamilyMember;
import com.catplanet.module.family.mapper.FamilyMapper;
import com.catplanet.module.family.mapper.FamilyMemberMapper;
import com.catplanet.module.family.service.impl.FamilyServiceImpl;
import com.catplanet.module.user.entity.User;
import com.catplanet.module.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FamilyServiceTest {

    @Mock private FamilyMapper familyMapper;
    @Mock private FamilyMemberMapper familyMemberMapper;
    @Mock private UserMapper userMapper;

    @InjectMocks private FamilyServiceImpl familyService;

    @Test
    void createFamily_success() {
        Long userId = 1L;
        CreateFamilyRequest req = new CreateFamilyRequest();
        req.setName("喵喵的家");

        when(familyMapper.insert(any(Family.class))).thenReturn(1);
        when(familyMemberMapper.insert(any(FamilyMember.class))).thenReturn(1);

        Family mockFamily = new Family();
        mockFamily.setFamilyId(100L);
        mockFamily.setName("喵喵的家");
        mockFamily.setCoverEmoji("🏠");
        mockFamily.setInviteCode("ABCD1234");
        when(familyMapper.selectById(any())).thenReturn(mockFamily);
        when(familyMemberMapper.selectList(any())).thenReturn(List.of());

        FamilyDetailResponse resp = familyService.createFamily(userId, req);
        assertNotNull(resp);
        assertEquals("喵喵的家", resp.getName());
        verify(familyMapper).insert(any(Family.class));
        verify(familyMemberMapper).insert(any(FamilyMember.class));
    }

    @Test
    void joinFamily_invalidCode_throwsException() {
        JoinFamilyRequest req = new JoinFamilyRequest();
        req.setInviteCode("INVALID");

        when(familyMapper.selectOne(any())).thenReturn(null);

        assertThrows(BizException.class, () -> familyService.joinFamily(1L, req));
    }
}
```

- [ ] **Step 8: 运行测试**

Run: `cd catplanet-server && mvn test -Dtest=FamilyServiceTest -q`
Expected: PASS (2 tests)

- [ ] **Step 9: Commit**

```bash
git add .
git commit -m "feat: add family CRUD module (create/join/leave/list)"
```

---

## Task 6: 猫咪 CRUD 模块

**Files:**
- Create: `catplanet-server/src/main/java/com/catplanet/module/cat/entity/Cat.java`
- Create: `catplanet-server/src/main/java/com/catplanet/module/cat/mapper/CatMapper.java`
- Create: `catplanet-server/src/main/java/com/catplanet/module/cat/dto/CreateCatRequest.java`
- Create: `catplanet-server/src/main/java/com/catplanet/module/cat/dto/UpdateCatRequest.java`
- Create: `catplanet-server/src/main/java/com/catplanet/module/cat/dto/CatDetailResponse.java`
- Create: `catplanet-server/src/main/java/com/catplanet/module/cat/service/CatService.java`
- Create: `catplanet-server/src/main/java/com/catplanet/module/cat/service/impl/CatServiceImpl.java`
- Create: `catplanet-server/src/main/java/com/catplanet/module/cat/controller/CatController.java`
- Test: `catplanet-server/src/test/java/com/catplanet/module/cat/CatServiceTest.java`

- [ ] **Step 1: 创建 Cat 实体**

```java
package com.catplanet.module.cat.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "cat", autoResultMap = true)
public class Cat {
    @TableId(type = IdType.ASSIGN_ID)
    private Long catId;
    private Long familyId;
    private String name;
    private String avatar;
    private String breed;
    private String gender;
    private LocalDate birthday;
    private Integer isNeutered;
    private BigDecimal weightKg;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> personalityTags;
    private LocalDate adoptionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}
```

- [ ] **Step 2: 创建 CatMapper**

```java
package com.catplanet.module.cat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catplanet.module.cat.entity.Cat;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CatMapper extends BaseMapper<Cat> {
}
```

- [ ] **Step 3: 创建 DTO**

```java
// CreateCatRequest.java
package com.catplanet.module.cat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateCatRequest {
    @NotBlank(message = "猫咪名字不能为空")
    @Size(max = 32, message = "名字最长32字")
    private String name;
    private String avatar;
    private String breed;
    private String gender;
    private LocalDate birthday;
    private Integer isNeutered;
    private BigDecimal weightKg;
    private List<String> personalityTags;
    private LocalDate adoptionDate;
}
```

```java
// UpdateCatRequest.java
package com.catplanet.module.cat.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateCatRequest {
    @Size(max = 32, message = "名字最长32字")
    private String name;
    private String avatar;
    private String breed;
    private String gender;
    private LocalDate birthday;
    private Integer isNeutered;
    private BigDecimal weightKg;
    private List<String> personalityTags;
    private LocalDate adoptionDate;
}
```

```java
// CatDetailResponse.java
package com.catplanet.module.cat.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class CatDetailResponse {
    private Long catId;
    private Long familyId;
    private String name;
    private String avatar;
    private String breed;
    private String gender;
    private LocalDate birthday;
    private Integer isNeutered;
    private BigDecimal weightKg;
    private List<String> personalityTags;
    private LocalDate adoptionDate;
}
```

- [ ] **Step 4: 创建 CatService 接口**

```java
package com.catplanet.module.cat.service;

import com.catplanet.module.cat.dto.CatDetailResponse;
import com.catplanet.module.cat.dto.CreateCatRequest;
import com.catplanet.module.cat.dto.UpdateCatRequest;

import java.util.List;

public interface CatService {
    CatDetailResponse createCat(Long familyId, CreateCatRequest request);
    CatDetailResponse updateCat(Long familyId, Long catId, UpdateCatRequest request);
    CatDetailResponse getCatDetail(Long familyId, Long catId);
    List<CatDetailResponse> listCats(Long familyId);
    void deleteCat(Long familyId, Long catId);
}
```

- [ ] **Step 5: 实现 CatServiceImpl**

```java
package com.catplanet.module.cat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.ResultCode;
import com.catplanet.module.cat.dto.CatDetailResponse;
import com.catplanet.module.cat.dto.CreateCatRequest;
import com.catplanet.module.cat.dto.UpdateCatRequest;
import com.catplanet.module.cat.entity.Cat;
import com.catplanet.module.cat.mapper.CatMapper;
import com.catplanet.module.cat.service.CatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatServiceImpl implements CatService {

    private final CatMapper catMapper;

    @Override
    public CatDetailResponse createCat(Long familyId, CreateCatRequest request) {
        Cat cat = new Cat();
        cat.setFamilyId(familyId);
        cat.setName(request.getName());
        cat.setAvatar(request.getAvatar());
        cat.setBreed(request.getBreed());
        cat.setGender(request.getGender() != null ? request.getGender() : "unknown");
        cat.setBirthday(request.getBirthday());
        cat.setIsNeutered(request.getIsNeutered() != null ? request.getIsNeutered() : 0);
        cat.setWeightKg(request.getWeightKg());
        cat.setPersonalityTags(request.getPersonalityTags());
        cat.setAdoptionDate(request.getAdoptionDate());
        catMapper.insert(cat);

        return toResponse(cat);
    }

    @Override
    public CatDetailResponse updateCat(Long familyId, Long catId, UpdateCatRequest request) {
        Cat cat = getCatEntity(familyId, catId);

        if (request.getName() != null) cat.setName(request.getName());
        if (request.getAvatar() != null) cat.setAvatar(request.getAvatar());
        if (request.getBreed() != null) cat.setBreed(request.getBreed());
        if (request.getGender() != null) cat.setGender(request.getGender());
        if (request.getBirthday() != null) cat.setBirthday(request.getBirthday());
        if (request.getIsNeutered() != null) cat.setIsNeutered(request.getIsNeutered());
        if (request.getWeightKg() != null) cat.setWeightKg(request.getWeightKg());
        if (request.getPersonalityTags() != null) cat.setPersonalityTags(request.getPersonalityTags());
        if (request.getAdoptionDate() != null) cat.setAdoptionDate(request.getAdoptionDate());

        catMapper.updateById(cat);
        return toResponse(cat);
    }

    @Override
    public CatDetailResponse getCatDetail(Long familyId, Long catId) {
        return toResponse(getCatEntity(familyId, catId));
    }

    @Override
    public List<CatDetailResponse> listCats(Long familyId) {
        List<Cat> cats = catMapper.selectList(
                new LambdaQueryWrapper<Cat>().eq(Cat::getFamilyId, familyId));
        return cats.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public void deleteCat(Long familyId, Long catId) {
        Cat cat = getCatEntity(familyId, catId);
        catMapper.deleteById(cat.getCatId());
    }

    private Cat getCatEntity(Long familyId, Long catId) {
        Cat cat = catMapper.selectOne(
                new LambdaQueryWrapper<Cat>()
                        .eq(Cat::getCatId, catId)
                        .eq(Cat::getFamilyId, familyId));
        if (cat == null) {
            throw new BizException(ResultCode.CAT_NOT_FOUND);
        }
        return cat;
    }

    private CatDetailResponse toResponse(Cat cat) {
        return CatDetailResponse.builder()
                .catId(cat.getCatId())
                .familyId(cat.getFamilyId())
                .name(cat.getName())
                .avatar(cat.getAvatar())
                .breed(cat.getBreed())
                .gender(cat.getGender())
                .birthday(cat.getBirthday())
                .isNeutered(cat.getIsNeutered())
                .weightKg(cat.getWeightKg())
                .personalityTags(cat.getPersonalityTags())
                .adoptionDate(cat.getAdoptionDate())
                .build();
    }
}
```

- [ ] **Step 6: 创建 CatController**

```java
package com.catplanet.module.cat.controller;

import com.catplanet.common.context.UserContext;
import com.catplanet.common.result.Result;
import com.catplanet.module.cat.dto.CatDetailResponse;
import com.catplanet.module.cat.dto.CreateCatRequest;
import com.catplanet.module.cat.dto.UpdateCatRequest;
import com.catplanet.module.cat.service.CatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cats")
@RequiredArgsConstructor
public class CatController {

    private final CatService catService;

    @PostMapping
    public Result<CatDetailResponse> create(@Valid @RequestBody CreateCatRequest request) {
        Long familyId = UserContext.getFamilyId();
        return Result.ok(catService.createCat(familyId, request));
    }

    @PutMapping("/{catId}")
    public Result<CatDetailResponse> update(@PathVariable Long catId,
                                            @Valid @RequestBody UpdateCatRequest request) {
        Long familyId = UserContext.getFamilyId();
        return Result.ok(catService.updateCat(familyId, catId, request));
    }

    @GetMapping("/{catId}")
    public Result<CatDetailResponse> detail(@PathVariable Long catId) {
        Long familyId = UserContext.getFamilyId();
        return Result.ok(catService.getCatDetail(familyId, catId));
    }

    @GetMapping
    public Result<List<CatDetailResponse>> list() {
        Long familyId = UserContext.getFamilyId();
        return Result.ok(catService.listCats(familyId));
    }

    @DeleteMapping("/{catId}")
    public Result<Void> delete(@PathVariable Long catId) {
        Long familyId = UserContext.getFamilyId();
        catService.deleteCat(familyId, catId);
        return Result.ok();
    }
}
```

- [ ] **Step 7: 编写 CatService 单元测试**

```java
package com.catplanet.module.cat;

import com.catplanet.common.exception.BizException;
import com.catplanet.module.cat.dto.CatDetailResponse;
import com.catplanet.module.cat.dto.CreateCatRequest;
import com.catplanet.module.cat.entity.Cat;
import com.catplanet.module.cat.mapper.CatMapper;
import com.catplanet.module.cat.service.impl.CatServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatServiceTest {

    @Mock private CatMapper catMapper;
    @InjectMocks private CatServiceImpl catService;

    @Test
    void createCat_success() {
        Long familyId = 100L;
        CreateCatRequest req = new CreateCatRequest();
        req.setName("奶盖");
        req.setBreed("英短");
        req.setGender("female");
        req.setWeightKg(new BigDecimal("4.2"));

        when(catMapper.insert(any(Cat.class))).thenReturn(1);

        CatDetailResponse resp = catService.createCat(familyId, req);
        assertNotNull(resp);
        assertEquals("奶盖", resp.getName());
        assertEquals(familyId, resp.getFamilyId());
        verify(catMapper).insert(any(Cat.class));
    }

    @Test
    void getCatDetail_notFound_throwsException() {
        when(catMapper.selectOne(any())).thenReturn(null);
        assertThrows(BizException.class, () -> catService.getCatDetail(1L, 999L));
    }

    @Test
    void listCats_returnsAll() {
        Cat cat1 = new Cat();
        cat1.setCatId(1L);
        cat1.setFamilyId(100L);
        cat1.setName("奶盖");

        Cat cat2 = new Cat();
        cat2.setCatId(2L);
        cat2.setFamilyId(100L);
        cat2.setName("布丁");

        when(catMapper.selectList(any())).thenReturn(List.of(cat1, cat2));

        List<CatDetailResponse> result = catService.listCats(100L);
        assertEquals(2, result.size());
    }
}
```

- [ ] **Step 8: 运行测试**

Run: `cd catplanet-server && mvn test -Dtest=CatServiceTest -q`
Expected: PASS (3 tests)

- [ ] **Step 9: Commit**

```bash
git add .
git commit -m "feat: add cat CRUD module with family isolation"
```

---

## Task 7: 小程序前端脚手架 + 登录页

**Files:**
- Create: `catplanet-mini/app.js`
- Create: `catplanet-mini/app.json`
- Create: `catplanet-mini/app.wxss`
- Create: `catplanet-mini/project.config.json`
- Create: `catplanet-mini/utils/request.js`
- Create: `catplanet-mini/utils/auth.js`
- Create: `catplanet-mini/pages/login/login.wxml`
- Create: `catplanet-mini/pages/login/login.wxss`
- Create: `catplanet-mini/pages/login/login.js`
- Create: `catplanet-mini/pages/login/login.json`

- [ ] **Step 1: 创建 app.json**

```json
{
  "pages": [
    "pages/planet/planet",
    "pages/login/login",
    "pages/onboarding/onboarding",
    "pages/cat-detail/cat-detail"
  ],
  "window": {
    "navigationBarBackgroundColor": "#FAF4ED",
    "navigationBarTitleText": "呼噜星球",
    "navigationBarTextStyle": "black",
    "backgroundColor": "#FAF4ED"
  },
  "tabBar": {
    "color": "#B0A294",
    "selectedColor": "#E58C7A",
    "backgroundColor": "#FFFFFF",
    "borderStyle": "white",
    "list": [
      {
        "pagePath": "pages/planet/planet",
        "text": "我的星球",
        "iconPath": "assets/tab-planet.png",
        "selectedIconPath": "assets/tab-planet-active.png"
      }
    ]
  },
  "style": "v2",
  "sitemapLocation": "sitemap.json"
}
```

- [ ] **Step 2: 创建 app.wxss（全局海盐桃主题）**

```css
/* app.wxss - 全局样式 · 海盐白桃风 */
page {
  --color-primary: #FFDDD2;
  --color-bg: #FAF4ED;
  --color-accent: #E58C7A;
  --color-neutral-1: #E8E0D5;
  --color-text-main: #4A3F35;
  --color-text-sub: #8A7B6E;
  --color-text-hint: #B0A294;
  --color-blue: #BFD7DE;
  --color-white: #FFFFFF;

  background-color: var(--color-bg);
  color: var(--color-text-main);
  font-family: -apple-system, "PingFang SC", "HarmonyOS Sans", sans-serif;
  font-size: 14px;
  line-height: 1.5;
}

.container {
  padding: 0 32rpx;
}

/* 按钮基础 */
.btn-primary {
  background: var(--color-accent);
  color: #fff;
  border: none;
  border-radius: 28rpx;
  padding: 20rpx 0;
  font-size: 16px;
  font-weight: 600;
  text-align: center;
}

.btn-secondary {
  background: var(--color-primary);
  color: var(--color-text-main);
  border: none;
  border-radius: 28rpx;
  padding: 20rpx 0;
  font-size: 14px;
  font-weight: 500;
  text-align: center;
}

/* 卡片基础 */
.card {
  background: var(--color-white);
  border-radius: 24rpx;
  padding: 24rpx;
  box-shadow: 0 2px 12px rgba(74, 63, 53, 0.06);
}
```

- [ ] **Step 3: 创建 app.js**

```javascript
// app.js
App({
  onLaunch() {
    // 检查登录态
    const token = wx.getStorageSync('token')
    if (!token) {
      wx.redirectTo({ url: '/pages/login/login' })
    }
  },

  globalData: {
    baseUrl: 'http://localhost:8080',
    userInfo: null,
    currentFamilyId: null
  }
})
```

- [ ] **Step 4: 创建 utils/request.js**

```javascript
// utils/request.js
const app = getApp()

const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token')
    const familyId = app.globalData.currentFamilyId

    const header = {
      'Content-Type': 'application/json',
      ...options.header
    }

    if (token) {
      header['Authorization'] = `Bearer ${token}`
    }
    if (familyId) {
      header['X-Family-Id'] = String(familyId)
    }

    wx.request({
      url: `${app.globalData.baseUrl}${options.url}`,
      method: options.method || 'GET',
      data: options.data,
      header,
      success(res) {
        if (res.statusCode === 401) {
          wx.removeStorageSync('token')
          wx.redirectTo({ url: '/pages/login/login' })
          reject(new Error('未登录'))
          return
        }
        if (res.data.code === 0) {
          resolve(res.data.data)
        } else {
          wx.showToast({ title: res.data.message || '请求失败', icon: 'none' })
          reject(new Error(res.data.message))
        }
      },
      fail(err) {
        wx.showToast({ title: '网络异常', icon: 'none' })
        reject(err)
      }
    })
  })
}

module.exports = { request }
```

- [ ] **Step 5: 创建 utils/auth.js**

```javascript
// utils/auth.js
const { request } = require('./request')

const login = () => {
  return new Promise((resolve, reject) => {
    wx.login({
      success(loginRes) {
        if (!loginRes.code) {
          reject(new Error('wx.login 失败'))
          return
        }
        request({
          url: '/api/v1/auth/wx-login',
          method: 'POST',
          data: { code: loginRes.code }
        }).then(data => {
          wx.setStorageSync('token', data.token)
          wx.setStorageSync('userId', data.userId)
          const app = getApp()
          app.globalData.userInfo = data
          resolve(data)
        }).catch(reject)
      },
      fail: reject
    })
  })
}

const isLoggedIn = () => {
  return !!wx.getStorageSync('token')
}

const logout = () => {
  wx.removeStorageSync('token')
  wx.removeStorageSync('userId')
  wx.redirectTo({ url: '/pages/login/login' })
}

module.exports = { login, isLoggedIn, logout }
```

- [ ] **Step 6: 创建登录页**

```html
<!-- pages/login/login.wxml -->
<view class="login-page">
  <view class="logo-area">
    <view class="logo">🪐</view>
    <view class="app-name">呼噜星球</view>
    <view class="slogan">和家人一起，把猫主子照顾得明明白白</view>
  </view>

  <view class="login-area">
    <button class="btn-login" bindtap="handleLogin">
      微信一键登录
    </button>
    <view class="agreement">
      登录即代表同意
      <text class="link">用户协议</text>
      和
      <text class="link">隐私政策</text>
    </view>
  </view>
</view>
```

```css
/* pages/login/login.wxss */
.login-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 0 64rpx;
}

.logo-area {
  text-align: center;
  margin-bottom: 120rpx;
}

.logo {
  font-size: 80px;
  margin-bottom: 24rpx;
}

.app-name {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-main);
  margin-bottom: 16rpx;
}

.slogan {
  font-size: 14px;
  color: var(--color-text-sub);
}

.login-area {
  width: 100%;
}

.btn-login {
  background: var(--color-accent);
  color: #fff;
  border: none;
  border-radius: 48rpx;
  padding: 28rpx 0;
  font-size: 16px;
  font-weight: 600;
  width: 100%;
}

.btn-login::after {
  border: none;
}

.agreement {
  text-align: center;
  font-size: 12px;
  color: var(--color-text-hint);
  margin-top: 24rpx;
}

.agreement .link {
  color: var(--color-accent);
}
```

```javascript
// pages/login/login.js
const { login } = require('../../utils/auth')

Page({
  data: {
    loading: false
  },

  async handleLogin() {
    if (this.data.loading) return
    this.setData({ loading: true })

    try {
      const result = await login()
      if (result.isNewUser) {
        wx.redirectTo({ url: '/pages/onboarding/onboarding' })
      } else {
        wx.switchTab({ url: '/pages/planet/planet' })
      }
    } catch (err) {
      wx.showToast({ title: '登录失败，请重试', icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  }
})
```

```json
{
  "navigationBarTitleText": "登录",
  "navigationStyle": "custom"
}
```

- [ ] **Step 7: Commit**

```bash
git add catplanet-mini/
git commit -m "feat: init mini-program scaffold + login page"
```

---

## Task 8: 小程序首页（我的星球）+ 猫咪卡片

**Files:**
- Create: `catplanet-mini/pages/planet/planet.wxml`
- Create: `catplanet-mini/pages/planet/planet.wxss`
- Create: `catplanet-mini/pages/planet/planet.js`
- Create: `catplanet-mini/pages/planet/planet.json`
- Create: `catplanet-mini/components/cat-card/cat-card.wxml`
- Create: `catplanet-mini/components/cat-card/cat-card.wxss`
- Create: `catplanet-mini/components/cat-card/cat-card.js`
- Create: `catplanet-mini/components/cat-card/cat-card.json`

- [ ] **Step 1: 创建 cat-card 组件**

```html
<!-- components/cat-card/cat-card.wxml -->
<view class="cat-card" bindtap="onTap">
  <view class="cat-row">
    <image class="cat-avatar" src="{{cat.avatar || ''}}" mode="aspectFill">
      <view wx:if="{{!cat.avatar}}" class="cat-avatar-placeholder">🐱</view>
    </image>
    <view class="cat-info">
      <view class="cat-name">{{cat.name}}</view>
      <view class="cat-tags">
        <text class="tag" wx:if="{{cat.breed}}">{{cat.breed}}</text>
        <text class="tag" wx:if="{{cat.gender === 'female'}}">♀</text>
        <text class="tag" wx:if="{{cat.gender === 'male'}}">♂</text>
        <text class="tag" wx:if="{{cat.isNeutered}}">已绝育</text>
      </view>
    </view>
  </view>
  <view class="cat-stats">
    <view class="stat">
      <view class="stat-label">体重</view>
      <view class="stat-value">{{cat.weightKg || '--'}} kg</view>
    </view>
    <view class="stat">
      <view class="stat-label">品种</view>
      <view class="stat-value">{{cat.breed || '未知'}}</view>
    </view>
  </view>
</view>
```

```css
/* components/cat-card/cat-card.wxss */
.cat-card {
  background: linear-gradient(135deg, #FFDDD2 0%, #FFE9DF 60%, #FAF4ED 100%);
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 0 4px 16px rgba(229, 140, 122, 0.12);
  margin-bottom: 24rpx;
}

.cat-row {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.cat-avatar {
  width: 112rpx;
  height: 112rpx;
  border-radius: 50%;
  background: #FFF8F3;
  border: 4rpx solid #fff;
  overflow: hidden;
}

.cat-avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48rpx;
}

.cat-info { flex: 1; }
.cat-name { font-size: 19px; font-weight: 700; margin-bottom: 8rpx; }
.cat-tags { display: flex; gap: 8rpx; flex-wrap: wrap; }
.tag {
  background: rgba(255,255,255,0.7);
  border-radius: 8rpx;
  padding: 4rpx 12rpx;
  font-size: 11px;
  color: var(--color-text-sub);
}

.cat-stats {
  display: flex;
  gap: 32rpx;
  margin-top: 24rpx;
  padding-top: 24rpx;
  border-top: 1rpx dashed rgba(74,63,53,0.15);
}

.stat { flex: 1; }
.stat-label { font-size: 10px; color: var(--color-text-sub); }
.stat-value { font-size: 14px; font-weight: 600; margin-top: 4rpx; }
```

```javascript
// components/cat-card/cat-card.js
Component({
  properties: {
    cat: { type: Object, value: {} }
  },
  methods: {
    onTap() {
      this.triggerEvent('tap', { catId: this.data.cat.catId })
    }
  }
})
```

```json
{
  "component": true
}
```

- [ ] **Step 2: 创建首页「我的星球」**

```html
<!-- pages/planet/planet.wxml -->
<view class="planet-page">
  <view class="page-header">
    <view class="title-area">
      <view class="page-title">🪐 我的星球</view>
      <view class="greeting">{{greeting}}</view>
    </view>
    <view class="invite-btn" bindtap="inviteFamily">+</view>
  </view>

  <!-- 家庭 chip -->
  <view class="family-chip" wx:if="{{family}}" bindtap="switchFamily">
    <text class="family-emoji">{{family.coverEmoji}}</text>
    <text>{{family.name}} · {{family.members.length}}位成员</text>
    <text class="arrow">▾</text>
  </view>

  <!-- 无家庭状态 -->
  <view class="empty-state" wx:if="{{!family}}">
    <view class="empty-icon">🏠</view>
    <view class="empty-text">还没有创建家庭哦</view>
    <button class="btn-primary" bindtap="goCreateFamily">创建我的呼噜星球</button>
  </view>

  <!-- 猫咪卡片 -->
  <swiper class="cat-swiper" wx:if="{{cats.length > 0}}"
          indicator-dots="{{cats.length > 1}}"
          indicator-color="rgba(74,63,53,0.2)"
          indicator-active-color="#E58C7A">
    <swiper-item wx:for="{{cats}}" wx:key="catId">
      <cat-card cat="{{item}}" bind:tap="onCatTap" />
    </swiper-item>
  </swiper>

  <!-- 无猫咪状态 -->
  <view class="empty-cat" wx:if="{{family && cats.length === 0}}">
    <view class="empty-icon">🐱</view>
    <view class="empty-text">添加你的第一只猫咪吧</view>
    <button class="btn-secondary" bindtap="goAddCat">+ 添加猫咪</button>
  </view>

  <!-- 快捷记录 -->
  <view class="section-title" wx:if="{{cats.length > 0}}">⚡ 快捷记录</view>
  <view class="quick-row" wx:if="{{cats.length > 0}}">
    <view class="quick-btn" bindtap="quickAction" data-type="feed">
      <view class="quick-circle">🍚</view>
      <text class="quick-label">喂食</text>
    </view>
    <view class="quick-btn" bindtap="quickAction" data-type="litter">
      <view class="quick-circle">💩</view>
      <text class="quick-label">猫砂</text>
    </view>
    <view class="quick-btn" bindtap="quickAction" data-type="weight">
      <view class="quick-circle">⚖️</view>
      <text class="quick-label">体重</text>
    </view>
    <view class="quick-btn" bindtap="quickAction" data-type="medicine">
      <view class="quick-circle">💊</view>
      <text class="quick-label">用药</text>
    </view>
    <view class="quick-btn" bindtap="quickAction" data-type="more">
      <view class="quick-circle">➕</view>
      <text class="quick-label">更多</text>
    </view>
  </view>
</view>
```

```css
/* pages/planet/planet.wxss */
.planet-page { padding: 24rpx 32rpx 160rpx; }

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}
.page-title { font-size: 24px; font-weight: 700; }
.greeting { font-size: 11px; color: var(--color-text-sub); margin-top: 4rpx; }
.invite-btn {
  width: 44rpx; height: 44rpx;
  border-radius: 50%;
  background: var(--color-neutral-1);
  display: flex; align-items: center; justify-content: center;
  font-size: 20px; color: var(--color-text-main);
}

.family-chip {
  display: inline-flex;
  align-items: center;
  gap: 12rpx;
  background: #fff;
  border-radius: 32rpx;
  padding: 12rpx 20rpx;
  font-size: 12px;
  box-shadow: 0 1px 3px rgba(74,63,53,0.06);
  margin-bottom: 32rpx;
}
.family-emoji { font-size: 16px; }
.arrow { color: var(--color-text-hint); }

.cat-swiper { height: 360rpx; margin-bottom: 16rpx; }

.empty-state, .empty-cat {
  text-align: center;
  padding: 80rpx 0;
}
.empty-icon { font-size: 60px; margin-bottom: 16rpx; }
.empty-text { font-size: 14px; color: var(--color-text-sub); margin-bottom: 32rpx; }

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-sub);
  margin: 32rpx 0 16rpx;
}

.quick-row {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16rpx;
}
.quick-btn { display: flex; flex-direction: column; align-items: center; gap: 8rpx; }
.quick-circle {
  width: 88rpx; height: 88rpx; border-radius: 50%;
  background: #fff;
  display: flex; align-items: center; justify-content: center;
  font-size: 28px;
  box-shadow: 0 2px 6px rgba(74,63,53,0.05);
}
.quick-label { font-size: 11px; }
```

```javascript
// pages/planet/planet.js
const { request } = require('../../utils/request')
const { isLoggedIn } = require('../../utils/auth')

Page({
  data: {
    greeting: '',
    family: null,
    cats: []
  },

  onLoad() {
    if (!isLoggedIn()) {
      wx.redirectTo({ url: '/pages/login/login' })
      return
    }
    this.setGreeting()
  },

  onShow() {
    if (isLoggedIn()) {
      this.loadFamilyData()
    }
  },

  setGreeting() {
    const hour = new Date().getHours()
    let text = '晚上好'
    if (hour < 6) text = '夜深了'
    else if (hour < 12) text = '早上好，今天也要爱猫主子哦'
    else if (hour < 18) text = '下午好'
    this.setData({ greeting: text })
  },

  async loadFamilyData() {
    try {
      const families = await request({ url: '/api/v1/families/mine' })
      if (families && families.length > 0) {
        const family = families[0]
        const app = getApp()
        app.globalData.currentFamilyId = family.familyId
        this.setData({ family })
        this.loadCats()
      } else {
        this.setData({ family: null, cats: [] })
      }
    } catch (err) {
      console.error('加载家庭数据失败', err)
    }
  },

  async loadCats() {
    try {
      const cats = await request({ url: '/api/v1/cats' })
      this.setData({ cats: cats || [] })
    } catch (err) {
      console.error('加载猫咪列表失败', err)
    }
  },

  onCatTap(e) {
    const { catId } = e.detail
    wx.navigateTo({ url: `/pages/cat-detail/cat-detail?id=${catId}` })
  },

  goCreateFamily() {
    wx.navigateTo({ url: '/pages/onboarding/onboarding' })
  },

  goAddCat() {
    wx.navigateTo({ url: '/pages/cat-detail/cat-detail?mode=create' })
  },

  inviteFamily() {
    if (!this.data.family) return
    wx.setClipboardData({
      data: this.data.family.inviteCode,
      success() {
        wx.showToast({ title: '邀请码已复制', icon: 'success' })
      }
    })
  },

  switchFamily() {
    // TODO: Plan 2 实现家庭切换
    wx.showToast({ title: '家庭切换功能开发中', icon: 'none' })
  },

  quickAction(e) {
    const type = e.currentTarget.dataset.type
    // TODO: Plan 2 实现快捷记录
    wx.showToast({ title: `${type} 功能开发中`, icon: 'none' })
  }
})
```

```json
{
  "navigationBarTitleText": "我的星球",
  "usingComponents": {
    "cat-card": "/components/cat-card/cat-card"
  }
}
```

- [ ] **Step 3: Commit**

```bash
git add catplanet-mini/
git commit -m "feat: add planet homepage + cat-card component"
```

---

## Task 9: 小程序引导页 + 创建家庭 + 添加猫咪

**Files:**
- Create: `catplanet-mini/pages/onboarding/onboarding.wxml`
- Create: `catplanet-mini/pages/onboarding/onboarding.wxss`
- Create: `catplanet-mini/pages/onboarding/onboarding.js`
- Create: `catplanet-mini/pages/onboarding/onboarding.json`
- Create: `catplanet-mini/pages/cat-detail/cat-detail.wxml`
- Create: `catplanet-mini/pages/cat-detail/cat-detail.wxss`
- Create: `catplanet-mini/pages/cat-detail/cat-detail.js`
- Create: `catplanet-mini/pages/cat-detail/cat-detail.json`

- [ ] **Step 1: 创建引导/创建家庭页**

```html
<!-- pages/onboarding/onboarding.wxml -->
<view class="onboarding">
  <!-- Step 1: 创建家庭 -->
  <view wx:if="{{step === 1}}" class="step">
    <view class="step-icon">🪐</view>
    <view class="step-title">创建你的呼噜星球</view>
    <view class="step-desc">给你的小家起个名字吧</view>

    <view class="form-group">
      <input class="input" placeholder="例如：奶盖的家"
             value="{{familyName}}" bindinput="onFamilyNameInput" maxlength="64"/>
    </view>

    <button class="btn-primary" bindtap="createFamily" disabled="{{!familyName}}">
      创建家庭
    </button>
  </view>

  <!-- Step 2: 添加猫咪 -->
  <view wx:if="{{step === 2}}" class="step">
    <view class="step-icon">🐱</view>
    <view class="step-title">添加第一只猫咪</view>
    <view class="step-desc">告诉我它的名字和基本信息</view>

    <view class="form-group">
      <view class="label">名字 *</view>
      <input class="input" placeholder="猫主子叫什么" value="{{catName}}" bindinput="onCatNameInput"/>
    </view>
    <view class="form-group">
      <view class="label">品种</view>
      <input class="input" placeholder="如：英短蓝白、橘猫" value="{{catBreed}}" bindinput="onBreedInput"/>
    </view>
    <view class="form-group">
      <view class="label">性别</view>
      <view class="radio-group">
        <view class="radio-item {{catGender === 'female' ? 'active' : ''}}" bindtap="setGender" data-v="female">♀ 女生</view>
        <view class="radio-item {{catGender === 'male' ? 'active' : ''}}" bindtap="setGender" data-v="male">♂ 男生</view>
        <view class="radio-item {{catGender === 'unknown' ? 'active' : ''}}" bindtap="setGender" data-v="unknown">未知</view>
      </view>
    </view>
    <view class="form-group">
      <view class="label">体重 (kg)</view>
      <input class="input" type="digit" placeholder="如 4.2" value="{{catWeight}}" bindinput="onWeightInput"/>
    </view>

    <button class="btn-primary" bindtap="addCat" disabled="{{!catName}}">
      添加猫咪并进入
    </button>
    <view class="skip" bindtap="skipAddCat">稍后添加 →</view>
  </view>
</view>
```

```css
/* pages/onboarding/onboarding.wxss */
.onboarding { padding: 80rpx 48rpx; }
.step { text-align: center; }
.step-icon { font-size: 64px; margin-bottom: 32rpx; }
.step-title { font-size: 22px; font-weight: 700; margin-bottom: 12rpx; }
.step-desc { font-size: 14px; color: var(--color-text-sub); margin-bottom: 64rpx; }

.form-group { margin-bottom: 32rpx; text-align: left; }
.label { font-size: 12px; color: var(--color-text-sub); margin-bottom: 8rpx; }
.input {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  font-size: 14px;
  width: 100%;
  box-sizing: border-box;
}

.radio-group { display: flex; gap: 16rpx; }
.radio-item {
  flex: 1;
  text-align: center;
  padding: 16rpx;
  border-radius: 12rpx;
  background: #fff;
  font-size: 13px;
}
.radio-item.active {
  background: var(--color-primary);
  font-weight: 600;
}

.btn-primary { margin-top: 48rpx; width: 100%; }
.skip {
  margin-top: 32rpx;
  font-size: 13px;
  color: var(--color-text-hint);
  text-align: center;
}
```

```javascript
// pages/onboarding/onboarding.js
const { request } = require('../../utils/request')

Page({
  data: {
    step: 1,
    familyName: '',
    familyId: null,
    catName: '',
    catBreed: '',
    catGender: 'unknown',
    catWeight: ''
  },

  onFamilyNameInput(e) { this.setData({ familyName: e.detail.value }) },
  onCatNameInput(e) { this.setData({ catName: e.detail.value }) },
  onBreedInput(e) { this.setData({ catBreed: e.detail.value }) },
  onWeightInput(e) { this.setData({ catWeight: e.detail.value }) },
  setGender(e) { this.setData({ catGender: e.currentTarget.dataset.v }) },

  async createFamily() {
    try {
      const res = await request({
        url: '/api/v1/families',
        method: 'POST',
        data: { name: this.data.familyName }
      })
      const app = getApp()
      app.globalData.currentFamilyId = res.familyId
      this.setData({ step: 2, familyId: res.familyId })
    } catch (err) {
      wx.showToast({ title: '创建失败', icon: 'none' })
    }
  },

  async addCat() {
    try {
      await request({
        url: '/api/v1/cats',
        method: 'POST',
        data: {
          name: this.data.catName,
          breed: this.data.catBreed || null,
          gender: this.data.catGender,
          weightKg: this.data.catWeight ? parseFloat(this.data.catWeight) : null
        }
      })
      wx.switchTab({ url: '/pages/planet/planet' })
    } catch (err) {
      wx.showToast({ title: '添加失败', icon: 'none' })
    }
  },

  skipAddCat() {
    wx.switchTab({ url: '/pages/planet/planet' })
  }
})
```

```json
{
  "navigationBarTitleText": "欢迎来到呼噜星球",
  "navigationStyle": "custom"
}
```

- [ ] **Step 2: 创建猫咪详情/编辑页**

```html
<!-- pages/cat-detail/cat-detail.wxml -->
<view class="cat-detail">
  <view class="header-card">
    <view class="avatar-area">
      <view class="avatar">{{cat.avatar ? '' : '🐱'}}</view>
      <image wx:if="{{cat.avatar}}" class="avatar-img" src="{{cat.avatar}}" mode="aspectFill"/>
    </view>
    <view class="cat-name">{{cat.name || '新猫咪'}}</view>
  </view>

  <!-- 编辑模式 -->
  <view wx:if="{{isEdit}}" class="edit-form">
    <view class="form-group">
      <view class="label">名字</view>
      <input class="input" value="{{form.name}}" bindinput="onInput" data-field="name"/>
    </view>
    <view class="form-group">
      <view class="label">品种</view>
      <input class="input" value="{{form.breed}}" bindinput="onInput" data-field="breed"/>
    </view>
    <view class="form-group">
      <view class="label">性别</view>
      <view class="radio-group">
        <view class="radio-item {{form.gender === 'female' ? 'active' : ''}}" bindtap="setField" data-field="gender" data-v="female">♀</view>
        <view class="radio-item {{form.gender === 'male' ? 'active' : ''}}" bindtap="setField" data-field="gender" data-v="male">♂</view>
      </view>
    </view>
    <view class="form-group">
      <view class="label">体重 (kg)</view>
      <input class="input" type="digit" value="{{form.weightKg}}" bindinput="onInput" data-field="weightKg"/>
    </view>
    <view class="form-group">
      <view class="label">是否绝育</view>
      <switch checked="{{form.isNeutered}}" bindchange="onNeuterChange" color="#E58C7A"/>
    </view>

    <button class="btn-primary" bindtap="saveCat">保存</button>
  </view>

  <!-- 查看模式 -->
  <view wx:else class="info-list">
    <view class="info-item">
      <text class="info-label">品种</text>
      <text class="info-value">{{cat.breed || '未填写'}}</text>
    </view>
    <view class="info-item">
      <text class="info-label">性别</text>
      <text class="info-value">{{cat.gender === 'female' ? '♀ 女生' : cat.gender === 'male' ? '♂ 男生' : '未知'}}</text>
    </view>
    <view class="info-item">
      <text class="info-label">体重</text>
      <text class="info-value">{{cat.weightKg ? cat.weightKg + ' kg' : '未记录'}}</text>
    </view>
    <view class="info-item">
      <text class="info-label">绝育</text>
      <text class="info-value">{{cat.isNeutered ? '已绝育' : '未绝育'}}</text>
    </view>
    <button class="btn-secondary" bindtap="toggleEdit">编辑资料</button>
  </view>
</view>
```

```css
/* pages/cat-detail/cat-detail.wxss */
.cat-detail { padding: 32rpx; }

.header-card {
  text-align: center;
  padding: 48rpx 0;
}
.avatar-area { position: relative; display: inline-block; }
.avatar {
  width: 160rpx; height: 160rpx;
  border-radius: 50%;
  background: var(--color-primary);
  display: flex; align-items: center; justify-content: center;
  font-size: 64rpx;
  margin: 0 auto;
}
.avatar-img {
  width: 160rpx; height: 160rpx;
  border-radius: 50%;
  position: absolute; top: 0; left: 0;
}
.cat-name { font-size: 22px; font-weight: 700; margin-top: 16rpx; }

.edit-form, .info-list { margin-top: 32rpx; }

.form-group { margin-bottom: 24rpx; }
.label { font-size: 12px; color: var(--color-text-sub); margin-bottom: 8rpx; }
.input {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  font-size: 14px;
  width: 100%;
  box-sizing: border-box;
}
.radio-group { display: flex; gap: 16rpx; }
.radio-item {
  flex: 1; text-align: center;
  padding: 16rpx; border-radius: 12rpx;
  background: #fff; font-size: 16px;
}
.radio-item.active { background: var(--color-primary); }

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 24rpx 0;
  border-bottom: 1rpx solid var(--color-neutral-1);
}
.info-label { color: var(--color-text-sub); font-size: 14px; }
.info-value { font-size: 14px; font-weight: 500; }

.btn-primary { margin-top: 48rpx; width: 100%; }
.btn-secondary { margin-top: 32rpx; width: 100%; }
```

```javascript
// pages/cat-detail/cat-detail.js
const { request } = require('../../utils/request')

Page({
  data: {
    catId: null,
    cat: {},
    isEdit: false,
    isCreate: false,
    form: {}
  },

  onLoad(options) {
    if (options.mode === 'create') {
      this.setData({ isEdit: true, isCreate: true, form: { gender: 'unknown' } })
    } else if (options.id) {
      this.setData({ catId: options.id })
      this.loadCat(options.id)
    }
  },

  async loadCat(catId) {
    try {
      const cat = await request({ url: `/api/v1/cats/${catId}` })
      this.setData({ cat, form: { ...cat } })
    } catch (err) {
      wx.showToast({ title: '加载失败', icon: 'none' })
    }
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ [`form.${field}`]: e.detail.value })
  },

  setField(e) {
    const { field, v } = e.currentTarget.dataset
    this.setData({ [`form.${field}`]: v })
  },

  onNeuterChange(e) {
    this.setData({ 'form.isNeutered': e.detail.value ? 1 : 0 })
  },

  toggleEdit() {
    this.setData({ isEdit: true, form: { ...this.data.cat } })
  },

  async saveCat() {
    const { form, isCreate, catId } = this.data
    try {
      if (isCreate) {
        await request({
          url: '/api/v1/cats',
          method: 'POST',
          data: {
            name: form.name,
            breed: form.breed,
            gender: form.gender,
            weightKg: form.weightKg ? parseFloat(form.weightKg) : null,
            isNeutered: form.isNeutered || 0
          }
        })
        wx.showToast({ title: '添加成功', icon: 'success' })
        setTimeout(() => wx.navigateBack(), 1000)
      } else {
        const updated = await request({
          url: `/api/v1/cats/${catId}`,
          method: 'PUT',
          data: form
        })
        this.setData({ cat: updated, isEdit: false })
        wx.showToast({ title: '保存成功', icon: 'success' })
      }
    } catch (err) {
      wx.showToast({ title: '操作失败', icon: 'none' })
    }
  }
})
```

```json
{
  "navigationBarTitleText": "猫咪档案"
}
```

- [ ] **Step 3: Commit**

```bash
git add catplanet-mini/
git commit -m "feat: add onboarding + cat-detail pages"
```

---

## Task 10: 端到端验证 + 收尾

**Files:**
- Verify: all backend endpoints
- Verify: mini-program pages render without error

- [ ] **Step 1: 执行数据库初始化**

Run: `mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS catplanet CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"`
Then: `mysql -u root -p catplanet < catplanet-server/src/main/resources/db/migration/V1__init_schema.sql`
Expected: 4 tables created (user, family, family_member, cat)

- [ ] **Step 2: 启动后端服务**

Run: `cd catplanet-server && mvn spring-boot:run -Dspring-boot.run.profiles=dev`
Expected: 应用在 8080 端口启动，无报错

- [ ] **Step 3: 用 curl 验证登录接口可达**

```bash
curl -X POST http://localhost:8080/api/v1/auth/wx-login \
  -H "Content-Type: application/json" \
  -d '{"code":"test_code"}'
```
Expected: 返回 JSON（可能是微信接口错误，但确认后端不 500）

- [ ] **Step 4: 运行全部测试**

Run: `cd catplanet-server && mvn test -q`
Expected: ALL TESTS PASS

- [ ] **Step 5: 用微信开发者工具打开小程序项目**

路径: `catplanet-mini/`
Expected: 编译无错误，能看到登录页

- [ ] **Step 6: 最终 Commit**

```bash
git add .
git commit -m "chore: Plan 1 complete - foundation module verified"
```

---

## 总结

Plan 1 完成后，你将拥有：

| 层 | 交付物 |
|----|--------|
| 后端 | Spring Boot 3.2 项目 + JWT 鉴权 + 家庭隔离 + 微信登录 + 家庭 CRUD + 猫咪 CRUD |
| 前端 | 微信小程序脚手架 + 登录页 + 引导页 + 首页（我的星球）+ 猫咪详情页 |
| 数据库 | MySQL schema (user/family/family_member/cat 4 张表) |
| 测试 | JwtUtil 单元测试 + AuthController 集成测试 + FamilyService 单元测试 + CatService 单元测试 |

这为后续 Plan 2（家庭记录模块）提供了完整的基础设施。