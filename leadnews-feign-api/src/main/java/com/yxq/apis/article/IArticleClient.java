package com.yxq.apis.article;

import com.yxq.apis.article.fallback.IArticleClientFallback;
import com.yxq.model.article.dtos.ArticleDto;
import com.yxq.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/12
 */
@FeignClient(value = "leadnews-article",fallback = IArticleClientFallback.class)
public interface IArticleClient {

    @PostMapping("/api/v1/article/save")
    ResponseResult saveArticle(@RequestBody ArticleDto dto);

}
