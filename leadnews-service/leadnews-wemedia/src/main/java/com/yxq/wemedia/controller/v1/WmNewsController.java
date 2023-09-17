package com.yxq.wemedia.controller.v1;

import com.yxq.model.common.dtos.ResponseResult;
import com.yxq.model.wemedia.dtos.WmNewsDto;
import com.yxq.model.wemedia.dtos.WmNewsPageReqDto;
import com.yxq.wemedia.service.WmNewsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/10
 */
@RestController
@RequestMapping("/api/v1/news")
public class WmNewsController {

    @Resource
    private WmNewsService wmNewsService;

    @PostMapping("/list")
    public ResponseResult findAll(@RequestBody WmNewsPageReqDto wmNewsPageReqDto) {
        return wmNewsService.findAll(wmNewsPageReqDto);
    }

    @PostMapping("/submit")
    public ResponseResult submitNews(@RequestBody WmNewsDto dto) throws Exception {
        return wmNewsService.submitNews(dto);
    }

}
