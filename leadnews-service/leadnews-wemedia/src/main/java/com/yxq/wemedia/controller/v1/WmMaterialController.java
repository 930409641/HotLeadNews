package com.yxq.wemedia.controller.v1;

import com.yxq.model.common.dtos.ResponseResult;
import com.yxq.model.wemedia.dtos.WmMaterialDto;
import com.yxq.wemedia.service.WmMaterialService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/10
 */
@RestController
@RequestMapping("/api/v1/material")
public class WmMaterialController {

    @Resource
    private WmMaterialService wmMaterialService;

    @PostMapping("/upload_picture")
    public ResponseResult uploadPicture(MultipartFile multipartFile) throws IOException {
        return wmMaterialService.uploadPicture(multipartFile);
    }

    @PostMapping("list")
    public ResponseResult findList(@RequestBody WmMaterialDto wmMaterialDto) {
        return wmMaterialService.findList(wmMaterialDto);
    }

    @GetMapping("del_picture/{id}")
    public ResponseResult delete(@PathVariable("id") Integer id) {
        return wmMaterialService.delete(id);
    }

}
