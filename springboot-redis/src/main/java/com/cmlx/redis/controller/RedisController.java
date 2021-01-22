package com.cmlx.redis.controller;

import com.cmlx.redis.pojo.model.StudentModel;
import com.cmlx.redis.service.IRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author cmlx
 * @Date -> 2021/1/22 12:18
 * @Desc ->
 **/
@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    IRedisService iRedisService;

    @RequestMapping("/addString")
    public void addString(StudentModel studentModel){
        iRedisService.addString(studentModel);
    }

    @RequestMapping("/getString")
    public String getString (Integer id) {
        StudentModel string = iRedisService.getString(id);
        return string.toString();
    }

    @RequestMapping("/addHash")
    public void addHash(StudentModel studentModel){
        iRedisService.addHash(studentModel);
    }

    @RequestMapping("/getHash")
    public String getHash (Integer id) {
        StudentModel string = iRedisService.getHash(id);
        return string.toString();
    }

    @RequestMapping("/addAnnotation")
    public void addAnnotation(StudentModel studentModel){
        iRedisService.addAnnotation(studentModel);
    }

    @RequestMapping("/getAnnotation")
    public String getAnnotation (Integer id) {
        StudentModel string = iRedisService.getAnnotation(id);
        return string.toString();
    }



}
