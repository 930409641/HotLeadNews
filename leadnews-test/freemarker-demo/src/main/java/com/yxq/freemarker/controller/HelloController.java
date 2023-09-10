package com.yxq.freemarker.controller;

import com.yxq.freemarker.entity.Student;
import jdk.nashorn.internal.runtime.logging.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/7
 */
@Controller
public class HelloController {

    @GetMapping("/test")
    public String test(Model model) {
        model.addAttribute("name","freemarker");
        Student student = new Student();
        student.setName("张三");
        student.setAge(11);
        student.setBirthday(new Date());
        student.setMoney(111F);
        model.addAttribute("stu",student);
        return "01-basic";
    }
}
