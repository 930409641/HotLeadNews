package com.yxq.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxq.file.service.FileStorageService;
import com.yxq.model.common.dtos.PageResponseResult;
import com.yxq.model.common.dtos.ResponseResult;
import com.yxq.model.common.enums.AppHttpCodeEnum;
import com.yxq.model.wemedia.dtos.WmMaterialDto;
import com.yxq.model.wemedia.pojos.WmMaterial;
import com.yxq.utils.common.WmThreadLocalUtil;
import com.yxq.wemedia.mapper.WmMaterialMapper;
import com.yxq.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/10
 */
@Slf4j
@Service
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    @Resource
    private FileStorageService fileStorageService;


    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile)  {
        // 1.检查参数
        if(multipartFile == null || multipartFile.getSize() == 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 2.上传图片到minio
        String filename = UUID.randomUUID().toString().replace("-", "");
        String originalFilename = multipartFile.getOriginalFilename();
        String postfix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileId = null;
        try {
            fileId = fileStorageService.uploadImgFile("", filename + postfix, multipartFile.getInputStream());
            log.info("文件上传成功{}",fileId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 3.保存到数据库中
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUserId(WmThreadLocalUtil.getUser().getId());
        wmMaterial.setUrl(fileId);
        wmMaterial.setType((short)0);
        wmMaterial.setIsCollection((short)0);
        wmMaterial.setCreatedTime(new Date());
        baseMapper.insert(wmMaterial);
        // 4.返回结果
        return ResponseResult.okResult(wmMaterial);
    }

    @Override
    public ResponseResult findList(WmMaterialDto wmMaterialDto) {
        // 1.校验参数
        wmMaterialDto.checkParam();

        //分页查询
        LambdaQueryWrapper<WmMaterial> queryWrapper = new LambdaQueryWrapper<>();
        if(wmMaterialDto.getIsCollection() != null && wmMaterialDto.getIsCollection() == 1) {
            queryWrapper.eq(WmMaterial::getIsCollection,wmMaterialDto.getIsCollection());
        }
        queryWrapper
                .eq(WmMaterial::getUserId,WmThreadLocalUtil.getUser().getId())
                .orderByDesc(WmMaterial::getCreatedTime);
        Page<WmMaterial> page = baseMapper.selectPage(new Page<>(wmMaterialDto.getPage(), wmMaterialDto.getSize()), queryWrapper);
        ResponseResult result = new PageResponseResult(wmMaterialDto.getPage(),wmMaterialDto.getSize(),(int) page.getTotal());
        result.setData(page.getRecords());
        return result;
    }
}
