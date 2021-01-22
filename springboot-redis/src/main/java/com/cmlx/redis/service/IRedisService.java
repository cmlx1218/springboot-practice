package com.cmlx.redis.service;

import com.cmlx.redis.pojo.model.StudentModel;

/**
 * @Author cmlx
 * @Date -> 2021/1/22 12:19
 * @Desc ->
 **/
public interface IRedisService {

    /**
     * 添加String类型
     *
     * @param studentModel
     */
    void addString(StudentModel studentModel);

    /**
     * 获取String类型
     *
     * @param id
     * @return
     */
    StudentModel getString(Integer id);

    /**
     * 添加Hash类型
     *
     * @param studentModel
     */
    void addHash(StudentModel studentModel);

    /**
     * 获取hash类型
     *
     * @param id
     * @return
     */
    StudentModel getHash(Integer id);

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
