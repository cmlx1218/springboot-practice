package com.cmlx.redis.config.annotation;

import java.lang.annotation.*;

/**
 * @Author cmlx
 * @Date -> 2021/1/22 16:36
 * @Desc ->
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheConfigList {
    CacheConfiguration[] value();
}
