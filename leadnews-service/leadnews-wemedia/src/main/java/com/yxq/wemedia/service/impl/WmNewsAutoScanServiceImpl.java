package com.yxq.wemedia.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.UpdatableResultSet;
import com.yxq.apis.article.IArticleClient;
import com.yxq.common.aliyun.GreenImageScan;
import com.yxq.common.aliyun.GreenTextScan;
import com.yxq.file.service.FileStorageService;
import com.yxq.model.article.dtos.ArticleDto;
import com.yxq.model.common.dtos.ResponseResult;
import com.yxq.model.wemedia.pojos.WmChannel;
import com.yxq.model.wemedia.pojos.WmNews;
import com.yxq.model.wemedia.pojos.WmUser;
import com.yxq.wemedia.mapper.WmChannelMapper;
import com.yxq.wemedia.mapper.WmNewsMapper;
import com.yxq.wemedia.mapper.WmUserMapper;
import com.yxq.wemedia.service.WmNewsAutoScanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/13
 */
@Service
@Slf4j
@Transactional
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {

    @Resource
    private WmNewsMapper wmNewsMapper;

    @Resource
    private WmChannelMapper wmChannelMapper;

    @Resource
    private WmUserMapper wmUserMapper;

    @Resource
    private GreenTextScan greenTextScan;

    @Resource
    private FileStorageService fileStorageService;

    @Resource
    private GreenImageScan greenImageScan;

    @Resource
    private IArticleClient iArticleClient;

    /**
     * 自媒体文章审核
     * @param id 自媒体文章id
     */
    @Override
    public void autoScanWmNews(Integer id) {
        //查询自媒体文章
        WmNews wmNews = wmNewsMapper.selectById(id);
        if(wmNews == null) {
            throw new RuntimeException("文章不存在");
        }

        if(wmNews.getStatus().equals(WmNews.Status.SUBMIT.getCode())) {
            //从内容中提取纯文本内容和图片
            Map<String,Object> textAndImages = handleTextAndImages(wmNews);

            //审核文本内容，阿里云接口
            boolean isTextScan = handleTestScan((String) textAndImages.get("content"),wmNews);
            if(!isTextScan)return;

            //审核图片内容，阿里云接口
            boolean isImageScan = handleImageScan((List<String>) textAndImages.get("images"),wmNews);
            if(!isImageScan) return;

            //4. 审核成功，保存app端相关的文章数据
            ResponseResult result = saveAppArticle(wmNews);

            if(!result.getCode().equals(200)) {
                throw new RuntimeException("保存APP端相关文章数据失败");
            }
            //回填article_id
            wmNews.setArticleId((Long) result.getData());
            wmNews.setStatus((short)9);
            wmNews.setReason("审核成功");
            wmNewsMapper.updateById(wmNews);
        }


    }

    private ResponseResult saveAppArticle(WmNews wmNews) {
        ArticleDto dto = new ArticleDto();
        BeanUtils.copyProperties(wmNews,dto);

        //文章的布局
        dto.setLayout(wmNews.getType());
        //频道
        WmChannel wmChannel = wmChannelMapper.selectById(wmNews.getChannelId());
        if(wmChannel != null) {
            dto.setChannelName(wmChannel.getName());
        }
        //作者
        WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
        if(wmUser != null) {
            dto.setAuthorName(wmUser.getName());
        }
        dto.setCreatedTime(new Date());

        ResponseResult result = iArticleClient.saveArticle(dto);

        return result;

    }

    private boolean handleImageScan(List<String> images, WmNews wmNews) {
        boolean flag = true;
        if(images == null || images.size() != 0) {
            return flag;
        }
        //下载图片 minio
        //图片去重
        List<byte[]> imageList = new ArrayList<>();
        images = images.stream().distinct().collect(Collectors.toList());
        for (String image : images) {
            byte[] bytes = fileStorageService.downLoadFile(image);
            imageList.add(bytes);
        }
        //审核图片
        try {
            Map map = greenImageScan.imageScan(imageList);
            if (map != null) {
                //审核失败
                if (map.get("suggestion").equals("block")) {
                    flag = false;
                    wmNews.setStatus((short) 2);
                    wmNews.setReason("当前文章中存在违规内容");
                    wmNewsMapper.updateById(wmNews);
                }
                //不确定信息，需要人工审核
                if (map.get("suggestion").equals("review")) {
                    flag = false;
                    wmNews.setStatus((short) 3);
                    wmNews.setReason("当前文章中存在不确定内容");
                    wmNewsMapper.updateById(wmNews);
                }
            }

                // TODO 默认返回true
                flag = true;
            } catch(Exception e){
                throw new RuntimeException(e);
            }

            return flag;
        }


    private boolean handleTestScan(String content, WmNews wmNews) {
        //TODO 目前没有接入阿里云的内容安全，默认返回true

        boolean flag = true;
        if((wmNews.getTitle()+"-"+content).length() == 0) {
            return flag;
        }

        try {
            Map map = greenTextScan.greeTextScan(content);
            if(map != null) {
                //审核失败
                if(map.get("suggestion").equals("block")) {
                    flag = false;
                    wmNews.setStatus((short)2);
                    wmNews.setReason("当前文章中存在违规内容");
                    wmNewsMapper.updateById(wmNews);
                }
                //不确定信息，需要人工审核
                if(map.get("suggestion").equals("review")) {
                    flag = false;
                    wmNews.setStatus((short)3);
                    wmNews.setReason("当前文章中存在不确定内容");
                    wmNewsMapper.updateById(wmNews);
                }

                // TODO 默认返回true
                flag = true;
            }
        } catch (Exception e) {
            flag = false;
            throw new RuntimeException(e);
        }
        return flag;
    }

    private Map<String, Object> handleTextAndImages(WmNews wmNews) {

        //存储文本内容
        StringBuilder stringBuilder = new StringBuilder();
        List<String> images = new ArrayList<>();
        //1.从自媒体文章的内容中提取文本和内容
        if(StringUtils.isNotBlank(wmNews.getContent())) {
            List<Map> maps = JSONArray.parseArray(wmNews.getContent(), Map.class);
            for(Map map : maps) {
                if(map.get("type").equals("text")) {
                    stringBuilder.append(map.get("value"));
                }
                if(map.get("type").equals("image")) {
                    images.add((String) map.get("value"));
                }
            }
        }

        //提取文章的封面图片
        if(StringUtils.isNotBlank(wmNews.getImages())) {
            String[] split = wmNews.getImages().split(",");
            images.addAll(Arrays.asList(split));
        }

        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("content",stringBuilder.toString());
        resultMap.put("images",images);


        return resultMap;
    }
}
