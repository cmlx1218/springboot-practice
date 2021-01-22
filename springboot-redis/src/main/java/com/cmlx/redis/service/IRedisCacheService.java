package com.cmlx.redis.service;

import com.cmlx.redis.pojo.model.StudentModel;

/**
 * @Author cmlx
 * @Date -> 2021/1/22 15:44
 * @Desc ->
 **/
public interface IRedisCacheService {

    /**
     * 注解添加String
     *
     * @param studentModel
     */
    StudentModel addAnnotation(StudentModel studentModel);

    /**
     * 注解获取String
     *
     * @param id
     * @return
     */
    StudentModel getAnnotation(Integer id);

}
