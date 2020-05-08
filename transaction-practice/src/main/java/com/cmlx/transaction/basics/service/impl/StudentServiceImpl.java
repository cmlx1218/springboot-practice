package com.cmlx.transaction.basics.service.impl;

import com.cmlx.transaction.basics.persist.entity.StudentEntity;
import com.cmlx.transaction.basics.persist.repository.StudentRepository;
import com.cmlx.transaction.basics.pojo.dto.StudentDto;
import com.cmlx.transaction.basics.pojo.model.StudentModel;
import com.cmlx.transaction.basics.service.IStudentService;
import com.cmlx.transaction.tool.EntityPropertyUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Desc
 * @Author cmlx
 * @Date 2020-5-8 0008 12:22
 */
@Service
public class StudentServiceImpl implements IStudentService {

    @Autowired
    StudentRepository studentRepository;


    @Override
    @Transactional
    public StudentDto addStudent(StudentModel studentModel) {
        StudentEntity studentEntity = new StudentEntity();
        studentEntity.setUserName(studentModel.getUserName()).setSex(studentModel.getSex())
                .setAddress(studentModel.getAddress()).setHobby(studentModel.getHobby())
                .setGrade(studentModel.getGrade()).setClazz(studentModel.getClazz());

        StudentEntity save = studentRepository.save(studentEntity);
        List list = new ArrayList();
        list.get(3);

        StudentDto studentDto = new StudentDto();
        EntityPropertyUtility.copyNotNull(save, studentDto);
        return studentDto;
    }
}
