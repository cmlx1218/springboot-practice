package com.cmlx.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author cmlx
 * @Date -> 2021/1/22 16:37
 * @Desc ->
 **/
@Data
@ConfigurationProperties(prefix = "spring.redis")
public class RedisCacheProperties {

    public String packagesToScan;   //注解扫描包的集

    private Long expiredTime;   // 过期时间

}
