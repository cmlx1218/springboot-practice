package com.cmlx.transaction.basics.persist.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @Desc
 * @Author cmlx
 * @Date 2020-5-8 0008 11:50
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "transaction_student")
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;

    private Integer sex;

    private String hobby;

    private String address;

    private Integer grade;

    private Integer clazz;



}
