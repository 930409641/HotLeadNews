package com.yxq.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxq.model.common.dtos.ResponseResult;
import com.yxq.model.wemedia.dtos.WmNewsDto;
import com.yxq.model.wemedia.dtos.WmNewsPageReqDto;
import com.yxq.model.wemedia.pojos.WmNews;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/10
 */
public interface WmNewsService extends IService<WmNews> {
    ResponseResult findAll(WmNewsPageReqDto wmNewsPageReqDto);

    ResponseResult submitNews(WmNewsDto dto);
}
