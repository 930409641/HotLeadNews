package com.yxq.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxq.model.common.dtos.ResponseResult;
import com.yxq.model.common.enums.AppHttpCodeEnum;
import com.yxq.model.user.dtos.LoginDto;
import com.yxq.model.user.pojos.ApUser;
import com.yxq.user.mapper.ApUserLoginMapper;
import com.yxq.user.service.ApUserLoginService;
import com.yxq.utils.common.AppJwtUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/6
 */
@Service
public class ApUserLoginServiceImpl extends ServiceImpl<ApUserLoginMapper, ApUser> implements ApUserLoginService {

    @Resource
    private ApUserLoginMapper apUserLoginMapper;
    @Override
    public ResponseResult login(LoginDto loginDto) {
        //游客登录
        if(StringUtils.isBlank(loginDto.getPhone()) && StringUtils.isBlank(loginDto.getPassword())) {
            Map<String,Object> map = new HashMap<>();
            map.put("token", AppJwtUtil.getToken(0L));
            return ResponseResult.okResult(map);
        }
        //用户密码登录
        ApUser apUser = apUserLoginMapper.selectOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, loginDto.getPhone()));
        if(apUser == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"用户不存在");
        }

        //比对密码
        String salt = apUser.getSalt();
        String password = loginDto.getPassword();
        password = DigestUtils.md5DigestAsHex((password + salt).getBytes());
        if(!password.equals(apUser.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR,"密码错误");
        }

        //返回数据
        Map<String,Object> map = new HashMap<>();
        map.put("token",AppJwtUtil.getToken(apUser.getId().longValue()));
        apUser.setPassword("");
        apUser.setSalt("");
        map.put("user",apUser);
        return ResponseResult.okResult(map);

    }
}
