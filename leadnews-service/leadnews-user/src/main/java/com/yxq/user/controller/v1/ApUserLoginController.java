package com.yxq.user.controller.v1;

import com.yxq.model.common.dtos.ResponseResult;
import com.yxq.model.user.dtos.LoginDto;
import com.yxq.user.service.ApUserLoginService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/6
 */
@RestController
@RequestMapping("/api/v1/login")
@Api(value = "app端用户登录",tags = "app端用户登录")
public class ApUserLoginController {

    @Resource
    private ApUserLoginService apUserLoginService;

    @PostMapping("/login_auth")
    public ResponseResult login(@RequestBody LoginDto loginDto) {

        return apUserLoginService.login(loginDto);
    }
}
