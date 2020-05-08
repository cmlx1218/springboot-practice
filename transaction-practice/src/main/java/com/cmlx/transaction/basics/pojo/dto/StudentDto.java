package com.cmlx.transaction.basics.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Desc
 * @Author cmlx
 * @Date 2020-5-8 0008 12:16
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class StudentDto {

    private Long id;

    private String userName;

    private Integer sex;

    private String hobby;

    private String address;

    private Integer grade;

    private Integer clazz;


}
