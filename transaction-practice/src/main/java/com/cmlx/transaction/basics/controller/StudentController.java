package com.cmlx.transaction.basics.controller;

import com.cmlx.transaction.basics.pojo.dto.StudentDto;
import com.cmlx.transaction.basics.pojo.model.StudentModel;
import com.cmlx.transaction.basics.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Desc
 * @Author cmlx
 * @Date 2020-5-8 0008 12:22
 */
@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    IStudentService iStudentService;

    @RequestMapping("/addStudent")
    public StudentDto addStudent(StudentModel studentModel) {
        StudentDto studentDto = iStudentService.addStudent(studentModel);
        return studentDto;
    }
}
