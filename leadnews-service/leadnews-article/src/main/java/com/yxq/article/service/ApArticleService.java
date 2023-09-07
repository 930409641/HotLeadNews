package com.yxq.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxq.model.article.dtos.ArticleHomeDto;
import com.yxq.model.article.pojos.ApArticle;
import com.yxq.model.common.dtos.ResponseResult;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/7
 */
public interface ApArticleService extends IService<ApArticle> {

    ResponseResult load(ArticleHomeDto dto, Short type);
}
