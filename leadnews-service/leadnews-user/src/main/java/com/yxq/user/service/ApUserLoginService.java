package com.yxq.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxq.model.common.dtos.ResponseResult;
import com.yxq.model.user.dtos.LoginDto;
import com.yxq.model.user.pojos.ApUser;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/6
 */
public interface ApUserLoginService extends IService<ApUser> {
    ResponseResult login(LoginDto loginDto);
}
