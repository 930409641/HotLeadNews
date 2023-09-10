package com.yxq.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxq.model.common.dtos.ResponseResult;
import com.yxq.model.wemedia.dtos.WmLoginDto;
import com.yxq.model.wemedia.pojos.WmUser;

public interface WmUserService extends IService<WmUser> {

    /**
     * 自媒体端登录
     * @param dto
     * @return
     */
    public ResponseResult login(WmLoginDto dto);

}