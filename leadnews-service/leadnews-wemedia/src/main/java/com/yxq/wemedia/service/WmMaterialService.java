package com.yxq.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxq.model.common.dtos.ResponseResult;
import com.yxq.model.wemedia.dtos.WmMaterialDto;
import com.yxq.model.wemedia.pojos.WmMaterial;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/10
 */
public interface WmMaterialService extends IService<WmMaterial> {
    ResponseResult uploadPicture(MultipartFile multipartFile) throws IOException;

    ResponseResult findList(WmMaterialDto wmMaterialDto);

    ResponseResult delete(Integer id);
}
