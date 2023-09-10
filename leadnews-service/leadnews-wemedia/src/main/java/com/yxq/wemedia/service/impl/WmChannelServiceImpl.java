package com.yxq.wemedia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxq.model.common.dtos.ResponseResult;
import com.yxq.model.wemedia.pojos.WmChannel;
import com.yxq.wemedia.mapper.WmChannelMapper;
import com.yxq.wemedia.service.WmChannelService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/10
 */
@Service
public class WmChannelServiceImpl extends ServiceImpl<WmChannelMapper, WmChannel> implements WmChannelService {
    @Override
    public ResponseResult findAll() {
        List<WmChannel> wmChannels = baseMapper.selectList(null);
        return ResponseResult.okResult(wmChannels);
    }
}
