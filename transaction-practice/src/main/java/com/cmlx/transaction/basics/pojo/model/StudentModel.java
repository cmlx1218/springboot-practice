package com.cmlx.transaction.basics.pojo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Desc
 * @Author cmlx
 * @Date 2020-5-8 0008 12:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentModel {

    private Long id;

    private String userName;

    private Integer sex;

    private String hobby;

    private String address;

    private Integer grade;

    private Integer clazz;

}
