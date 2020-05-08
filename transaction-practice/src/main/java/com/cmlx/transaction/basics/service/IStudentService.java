package com.cmlx.transaction.basics.service;

import com.cmlx.transaction.basics.pojo.dto.StudentDto;
import com.cmlx.transaction.basics.pojo.model.StudentModel;

/**
 * @Desc
 * @Author cmlx
 * @Date 2020-5-8 0008 12:21
 */
public interface IStudentService {

    StudentDto addStudent(StudentModel studentModel);

}
