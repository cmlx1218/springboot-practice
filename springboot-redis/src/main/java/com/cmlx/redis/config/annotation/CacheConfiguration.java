package com.cmlx.redis.config.annotation;

import java.lang.annotation.*;

/**
 * @Author cmlx
 * @Date -> 2021/1/22 16:27
 * @Desc ->
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheConfiguration {

    String nameSpace() default "default";

    long expTime() default -1L; // 默认缓存不过期

}
