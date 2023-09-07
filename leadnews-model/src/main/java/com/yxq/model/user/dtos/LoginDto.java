package com.yxq.model.user.dtos;

import lombok.Data;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/6
 */
@Data
public class LoginDto {
    /**
     * 手机号
     */
    //@ApiModelProperty(value="手机号",required = true)
    private String phone;

    /**
     * 密码
     */
    //@ApiModelProperty(value="密码",required = true)
    private String password;
}
