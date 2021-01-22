package com.cmlx.redis.pojo.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author cmlx
 * @Date -> 2021/1/22 12:22
 * @Desc ->
 **/
@Data
public class StudentModel implements Serializable {

    private static final long serialVersionUID = 2694577449904909202L;
    private Integer id;

    private String name;

    private Integer age;

    private Integer sex;

    private String address;

}
