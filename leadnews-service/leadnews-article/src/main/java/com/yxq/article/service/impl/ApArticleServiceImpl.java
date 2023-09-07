package com.yxq.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxq.article.mapper.ApArticleMapper;
import com.yxq.article.service.ApArticleService;
import com.yxq.model.article.dtos.ArticleHomeDto;
import com.yxq.model.article.pojos.ApArticle;
import com.yxq.model.common.constants.ArticleConstants;
import com.yxq.model.common.dtos.ResponseResult;
import lombok.extern.slf4j.Slf4j;
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
}
