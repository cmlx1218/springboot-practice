package com.cmlx.redis.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.cmlx.redis.config.annotation.CacheConfigList;
import com.cmlx.redis.config.annotation.CacheConfiguration;
import com.cmlx.redis.util.ReflectionUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author cmlx
 * @Date -> 2021/1/22 11:34
 * @Desc -> redis配置类
 * redis 存储java对象有两种方式：
 * 1、序列化和反序列化：这种效率更高，占用空间少，但是如果对象字段变化会报错，适用于大数据量
 * 2、转json：
 **/
@Slf4j
@Configuration
@EnableCaching//开启注解式缓存
@EnableConfigurationProperties({RedisCacheProperties.class})
public class RedisConfiguration extends CachingConfigurerSupport {

    private RedisCacheProperties redisCacheProperties;
    private ConcurrentHashMap<String, RedisCacheConfiguration> expires;
    private Set<String> nameSpaces;
    private RedisCacheConfiguration defaultCacheConfig;

    public RedisConfiguration(RedisCacheProperties redisCacheProperties) {
        this.redisCacheProperties = redisCacheProperties;
        init();
    }

    public void init() {
        try {
            Set<Class<?>> classes = ReflectionUtility.loadClassesByAnnotationClass(
                    new Class[]{CacheConfiguration.class, CacheConfigList.class}, redisCacheProperties.packagesToScan.split(","));
            if (!CollectionUtils.isEmpty(classes)) {
                expires = new ConcurrentHashMap<>();
                nameSpaces = new HashSet<>();
                defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                        .computePrefixWith(name -> name + ":")
                        .entryTtl(Duration.ofSeconds(3600 * 24 * 60))
                        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer()))
                        .disableCachingNullValues();
            }
            classes.forEach(aClass -> {
                CacheConfiguration configuration = AnnotationUtils.findAnnotation(aClass, CacheConfiguration.class);
                CacheConfigList configurationList = AnnotationUtils.findAnnotation(aClass, CacheConfigList.class);
                if (null != configuration) {
                    long expTime = configuration.expTime() == 0 ? redisCacheProperties.getExpiredTime() : configuration.expTime();
                    expires.put(configuration.nameSpace(), defaultCacheConfig.entryTtl(Duration.ofSeconds(expTime)));
                    nameSpaces.add(configuration.nameSpace());
                }
                if (null != configurationList) {
                    CacheConfiguration[] value = configurationList.value();
                    for (CacheConfiguration config : value) {
                        long expTime = config.expTime() == 0 ? redisCacheProperties.getExpiredTime() : config.expTime();
                        expires.put(config.nameSpace(), defaultCacheConfig.entryTtl(Duration.ofSeconds(expTime)));
                        nameSpaces.add(config.nameSpace());
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


    // CacheManager只针对注解缓存有效
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisSerializer<Object> redisSerializer = getRedisSerializer();
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                // 默认过期时间
                .entryTtl(Duration.ofSeconds(60 * 60 * 24 * 30))
                // 定义cache key前缀，避免不同项目之间key冲突
                //.computePrefixWith(cacheName -> "yourAppName".concat(":").concat(cacheName).concat(":"))
                .computePrefixWith(name -> name + ":")
                // springboot2.0 变双冒号为单冒号
                // 定义key和value的序列化协议，同时的hash key和hash value也被定义
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                // 禁止缓存null对象
                .disableCachingNullValues();
        if (nameSpaces != null) {
            return RedisCacheManager.builder(redisConnectionFactory)
                    // 默认配置
                    .cacheDefaults(redisCacheConfiguration)
                    // 初始化缓存名
                    .initialCacheNames(nameSpaces)
                    // 初始化相关配置
                    .withInitialCacheConfigurations(expires)
                    .build();
        }
        return RedisCacheManager.builder(redisConnectionFactory)
                // 默认配置
                .cacheDefaults(redisCacheConfiguration)
                //// 初始化缓存名
                //.initialCacheNames(nameSpaces)
                //// 初始化相关配置
                //.withInitialCacheConfigurations(expires)
                .build();
    }


    /**
     * 序列化方式对比：
     * 1、JdkSerializationRedisSerializer：使用JDK提供的序列化功能。 优点是反序列化时不需要提供类型信息(class)，但缺点是需要实现Serializable接口，还有序列化后的结果非常庞大，是JSON格式的5倍左右，这样就会消耗redis服务器的大量内存
     * 2、Jackson2JsonRedisSerializer：使用Jackson库将对象序列化为JSON字符串。优点是速度快，序列化后的字符串短小精悍，不需要实现Serializable接口。但缺点也非常致命，那就是此类的构造函数中有一个类型参数，必须提供要序列化对象的类型信息(.class对象)。 通过查看源代码，发现其只在反序列化过程中用到了类型信息。
     * 容易出现取值时类型转换失败【eg：存入 (Long)111,取出会变成 (Integer)111】
     *
     * @param redisConnectionFactory redis 连接工厂
     * @return redisTemplate
     */
    @Bean
    // 注入时默认注入当前对象
    // 如果需要注入其他bean对象，注入时需要加 -> @Qualifier(value = "redisTemplate2")
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisSerializer<Object> redisSerializer = getRedisSerializer();
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate();
        // 配置连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        //使用StringRedisSerializer来序列化和反序列化redis的key值
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //使用FastJson2JsonRedisSerializer来序列化和反序列化redis的value值
        redisTemplate.setValueSerializer(redisSerializer);

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(redisSerializer);

        // 执行这个函数初始化RedisTemplate
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }


    public RedisSerializer<Object> getRedisSerializer() {
        return new GenericFastJsonRedisSerializer();
    }

}
