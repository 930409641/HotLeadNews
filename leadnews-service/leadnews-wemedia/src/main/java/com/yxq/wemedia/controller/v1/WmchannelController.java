package com.yxq.wemedia.controller.v1;

import com.yxq.model.common.dtos.ResponseResult;
import com.yxq.wemedia.service.WmChannelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/10
 */
@RestController
@RequestMapping("/api/v1/channel")
public class WmchannelController {

    @Resource
    private WmChannelService wmChannelService;

    @GetMapping("/channels")
    public ResponseResult findAll() {
        return wmChannelService.findAll();
    }
}
