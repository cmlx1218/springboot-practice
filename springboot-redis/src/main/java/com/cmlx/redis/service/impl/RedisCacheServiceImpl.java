package com.cmlx.redis.service.impl;

import com.cmlx.redis.config.annotation.CacheConfigList;
import com.cmlx.redis.config.annotation.CacheConfiguration;
import com.cmlx.redis.pojo.model.StudentModel;
import com.cmlx.redis.service.IRedisCacheService;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @Author cmlx
 * @Date -> 2021/1/22 15:45
 * @Desc ->
 **/
@Service
@CacheConfigList({
        @CacheConfiguration(nameSpace = "CMLX_STUDENT_ANNOTATION", expTime = 60)
})
public class RedisCacheServiceImpl implements IRedisCacheService {

    private static final String ANNOTATION_NAME_SPACE = "CMLX_STUDENT_ANNOTATION";

    @Override
    @CachePut(value = ANNOTATION_NAME_SPACE, key = "#studentModel.id")
    public StudentModel addAnnotation(StudentModel studentModel) {
        return studentModel;
    }

    @Override
    @Cacheable(value = ANNOTATION_NAME_SPACE, key = "#id", unless = "#result == null")
    public StudentModel getAnnotation(Integer id) {
        StudentModel studentModel = new StudentModel();
        studentModel.setId(1);
        studentModel.setName("周芷若");
        return studentModel;
    }
}
