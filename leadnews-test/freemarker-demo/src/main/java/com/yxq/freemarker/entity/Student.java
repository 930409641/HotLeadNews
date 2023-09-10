package com.yxq.freemarker.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/7
 */
@Data
public class Student {
    private String name;//姓名
    private int age;//年龄
    private Date birthday;//生日
    private Float money;//钱包
}
