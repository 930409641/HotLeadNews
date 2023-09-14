package com.yxq.article.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxq.article.mapper.ApArticleConfigMapper;
import com.yxq.article.mapper.ApArticleContentMapper;
import com.yxq.article.mapper.ApArticleMapper;
import com.yxq.article.service.ApArticleService;
import com.yxq.model.article.dtos.ArticleDto;
import com.yxq.model.article.dtos.ArticleHomeDto;
import com.yxq.model.article.pojos.ApArticle;
import com.yxq.model.article.pojos.ApArticleConfig;
import com.yxq.model.article.pojos.ApArticleContent;
import com.yxq.model.common.constants.ArticleConstants;
import com.yxq.model.common.dtos.ResponseResult;
import com.yxq.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.nntp.Article;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/7
 */
@Service
@Transactional
@Slf4j
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    @Resource
    private ApArticleMapper apArticleMapper;

    @Resource
    private ApArticleConfigMapper apArticleConfigMapper;

    @Resource
    private ApArticleContentMapper apArticleContentMapper;

    @Override
    public ResponseResult load(ArticleHomeDto dto, Short type) {
        //1.参数的校验
        Integer size = dto.getSize();
        if(size == null || size == 0) {
            size = 10;
        }
        size = Math.min(50,size);
        dto.setSize(size);

        //类型校验
        if(!type.equals(ArticleConstants.LOADTYPE_LOAD_MORE) || !type.equals(ArticleConstants.LOADTYPE_LOAD_NEW)) {
            type = ArticleConstants.LOADTYPE_LOAD_MORE;
        }
        //文章频道校验
        if(dto.getTag() == null) {
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }

        //时间校验
        if(dto.getMaxBehotTime() == null) dto.setMaxBehotTime(new Date());
        if(dto.getMinBehotTime() == null) dto.setMinBehotTime(new Date());

        List<ApArticle> apArticleList = apArticleMapper.loadArticleList(dto, type);
        return ResponseResult.okResult(apArticleList);
    }

    @Override
    public ResponseResult save(ArticleDto dto) {
        //检查参数
        if(dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        ApArticle article = new ApArticle();
        BeanUtils.copyProperties(dto,article);

        //判断是否存在id
        if(dto.getId() == null) {
            // 不存在id 保存文章信息，文章配置，文章内容
            //保存文章
            baseMapper.insert(article);

            //保存文章配置
            ApArticleConfig apArticleConfig = new ApArticleConfig(dto.getId());
            apArticleConfigMapper.insert(apArticleConfig);

            //保存文章内容
            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setArticleId(dto.getId());
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.insert(apArticleContent);
        }else {
            //存在id 更新文章信息 文章内容

            //修改文章
            baseMapper.updateById(article);
            ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, dto.getId()));
                apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.insert(apArticleContent);
        }

        return ResponseResult.okResult(article.getId());
    }
}
