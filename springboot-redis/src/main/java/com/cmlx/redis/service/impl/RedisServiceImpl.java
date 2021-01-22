package com.cmlx.redis.service.impl;

import com.alibaba.fastjson.JSON;
import com.cmlx.redis.config.annotation.CacheConfigList;
import com.cmlx.redis.config.annotation.CacheConfiguration;
import com.cmlx.redis.pojo.model.StudentModel;
import com.cmlx.redis.service.IRedisCacheService;
import com.cmlx.redis.service.IRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

/**
 * @Author cmlx
 * @Date -> 2021/1/22 12:19
 * @Desc ->
 **/
@Service
@CacheConfigList({
        @CacheConfiguration(nameSpace = "CMLX_STUDENT_STRING", expTime = 60),
        @CacheConfiguration(nameSpace = "CMLX_STUDENT_HASH", expTime = 60)
})
public class RedisServiceImpl implements IRedisService {

    private static final String STRING_NAME_SPACE = "CMLX_STUDENT_STRING:";
    private static final String HASH_NAME_SPACE = "CMLX_STUDENT_HASH";


    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    IRedisCacheService iRedisCacheService;

    @Override
    public void addString(StudentModel studentModel) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(STRING_NAME_SPACE + studentModel.getId(), JSON.toJSONString(studentModel));
    }

    @Override
    public StudentModel getString(Integer id) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String json = valueOperations.get(STRING_NAME_SPACE + id);
        return JSON.parseObject(json, StudentModel.class);
    }

    @Override
    public void addHash(StudentModel studentModel) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.put(HASH_NAME_SPACE, studentModel.getId().toString(), JSON.toJSONString(studentModel));
    }

    @Override
    public StudentModel getHash(Integer id) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String json = hashOperations.get(HASH_NAME_SPACE, id.toString());
        return JSON.parseObject(json, StudentModel.class);
    }

    @Override
    public StudentModel addAnnotation(StudentModel studentModel) {
        return iRedisCacheService.addAnnotation(studentModel);
    }

    @Override
    public StudentModel getAnnotation(Integer id) {
        return iRedisCacheService.getAnnotation(id);
    }


}
