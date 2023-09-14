package com.yxq.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxq.common.exception.CustomException;
import com.yxq.model.common.constants.WemediaConstants;
import com.yxq.model.common.dtos.PageResponseResult;
import com.yxq.model.common.dtos.ResponseResult;
import com.yxq.model.common.enums.AppHttpCodeEnum;
import com.yxq.model.wemedia.dtos.WmNewsDto;
import com.yxq.model.wemedia.dtos.WmNewsPageReqDto;
import com.yxq.model.wemedia.pojos.WmMaterial;
import com.yxq.model.wemedia.pojos.WmNews;
import com.yxq.model.wemedia.pojos.WmNewsMaterial;
import com.yxq.model.wemedia.pojos.WmUser;
import com.yxq.utils.common.WmThreadLocalUtil;
import com.yxq.wemedia.mapper.WmMaterialMapper;
import com.yxq.wemedia.mapper.WmNewsMapper;
import com.yxq.wemedia.mapper.WmNewsMaterialMapper;
import com.yxq.wemedia.service.WmNewsMaterialService;
import com.yxq.wemedia.service.WmNewsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/10
 */
@Service
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {


    @Resource
    private WmNewsMaterialService wmNewsMaterialService;

    @Resource
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Resource
    private WmMaterialMapper wmMaterialMapper;

    @Override
    public ResponseResult findAll(WmNewsPageReqDto wmNewsPageReqDto) {
        wmNewsPageReqDto.checkParam();

        // 检查当前用户
        WmUser user = WmThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        LambdaQueryWrapper<WmNews> wrapper = new LambdaQueryWrapper<>();
        // 状态精确查询
        if(wmNewsPageReqDto.getStatus() != null) {
            wrapper.eq(WmNews::getStatus,wmNewsPageReqDto.getStatus());
        }
        // 频道精确查询
        if(wmNewsPageReqDto.getChannelId() != null) {
            wrapper.eq(WmNews::getChannelId,wmNewsPageReqDto.getChannelId());
        }
        // 开始和结束时间
        if(wmNewsPageReqDto.getBeginPubDate() != null && wmNewsPageReqDto.getEndPubDate() != null) {
            wrapper.between(WmNews::getPublishTime,wmNewsPageReqDto.getBeginPubDate(),wmNewsPageReqDto.getEndPubDate());
        }
        //模糊查询
        if(wmNewsPageReqDto.getKeyword() != null) {
            wrapper.like(WmNews::getTitle,wmNewsPageReqDto.getKeyword());
        }

        Page<WmNews> page = baseMapper.selectPage(new Page<>(wmNewsPageReqDto.getPage(), wmNewsPageReqDto.getSize()), wrapper);
        ResponseResult result = new PageResponseResult(wmNewsPageReqDto.getPage(), wmNewsPageReqDto.getSize(), (int)page.getTotal());
        result.setData(page.getRecords());
        return result;
    }

    @Override
    public ResponseResult submitNews(WmNewsDto dto) {

        // 判断内容是否为空
        if(dto.getContent() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //1.保存或修改文章
        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(dto,wmNews);
        //封面图片 List-----》string
        if(dto.getImages() != null && dto.getImages().size() != 0) {
            String images = StringUtils.join(dto.getImages(), ",");
            wmNews.setImages(images);
        }
        // 如果当前封面类型为自动
        if(dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)) {
            wmNews.setType(null);
        }
        this.saveOrUpdateWmNews(wmNews);

        //判断是否为草稿，如果为草稿结束当前方法
        if(dto.getStatus().equals(WmNews.Status.NORMAL.getCode())) {
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }
        //不是草稿，保存文章内容图片与素材的关系
        //获取到文章内容中的图片信息
        List<String> materials = ectractUrlInfo(dto.getContent());
        saveRelativeInfoForContent(materials,wmNews.getId());

        // 4.不是草稿，保存文章封面图片与素材的关系，如果当前布局是自动，需要匹配封面图片
        saveRelativeInfoForCover(dto,wmNews,materials);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 第一个功能：如果当前封面类型为自动，则设置封面类型的数据
     * 匹配规则：
     * 1，如果内容图片大于等于1，小于3  单图  type 1
     * 2，如果内容图片大于等于3  多图  type 3
     * 3，如果内容没有图片，无图  type 0
     *
     * 第二个功能：保存封面图片与素材的关系
     * @param dto
     * @param wmNews
     * @param materials
     */
    private void saveRelativeInfoForCover(WmNewsDto dto, WmNews wmNews, List<String> materials) {

        List<String> images = dto.getImages();

        //如果当前封面类型为自动，则设置封面类型的数据
        if(dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)){
            //多图
            if(materials.size() >= 3){
                wmNews.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
                images = materials.stream().limit(3).collect(Collectors.toList());
            }else if(materials.size() >= 1 && materials.size() < 3){
                //单图
                wmNews.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                images = materials.stream().limit(1).collect(Collectors.toList());
            }else {
                //无图
                wmNews.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }

            //修改文章
            if(images != null && images.size() > 0){
                wmNews.setImages(StringUtils.join(images,","));
            }
            updateById(wmNews);
        }
        if(images != null && images.size() > 0){
            saveRelativeInfo(images,wmNews.getId(),WemediaConstants.WM_COVER_REFERENCE);
        }

    }

    /**
     * 处理文章内容图片与素材的关系
     * @param materials
     * @param id
     */
    private void saveRelativeInfoForContent(List<String> materials, Integer id) {
        saveRelativeInfo(materials,id,WemediaConstants.WM_CONTENT_REFERENCE);
    }

    private void saveRelativeInfo(List<String> materials, Integer id, Short type) {
        if(materials == null || materials.size() == 0) {
            return;
        }
        //通过图片的url查询id
        List<WmMaterial> wmMaterials = wmMaterialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery().in(WmMaterial::getUrl, materials));
        //判断素材是否有效
        if(wmMaterials == null && wmMaterials.size() == 0) {
            //手动抛出异常   第一个功能：能够提示调用者素材失效了，第二个功能，进行数据的回滚
            throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
        }

        if(materials.size() != wmMaterials.size()){
            throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
        }

        List<Integer> collect = wmMaterials.stream().map(item -> item.getId()).collect(Collectors.toList());

        //批量保存
        wmNewsMaterialMapper.saveRelations(collect,id,type);
    }

    /**
     * 提取文章内容中的图片信息
     * @param content
     * @return
     */
    private List<String> ectractUrlInfo(String content) {
        List<String> materials = new ArrayList<>();
        List<Map> maps = JSON.parseArray(content, Map.class);
        for (Map map : maps) {
            if(map.get("type").equals("image")) {
                String value = (String) map.get("value");
                materials.add(value);
            }
        }
        return materials;
    }

    private void saveOrUpdateWmNews(WmNews wmNews) {

        // 补全属性
        wmNews.setUserId(WmThreadLocalUtil.getUser().getId());
        wmNews.setCreatedTime(new Date());
        wmNews.setSubmitedTime(new Date());
        wmNews.setEnable((short) 1); // 默认上架
        if(wmNews.getId() == null) {
            //新增
            baseMapper.insert(wmNews);
        }else {
            //修改
            //删除文章相关联的图片
            wmNewsMaterialService.remove(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId,wmNews.getId()));
            baseMapper.updateById(wmNews);
        }

    }
}
