package com.yxq.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxq.model.common.dtos.ResponseResult;
import com.yxq.model.wemedia.pojos.WmChannel;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/10
 */
public interface WmChannelService extends IService<WmChannel> {
    ResponseResult findAll();

}
