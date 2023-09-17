package com.yxq.article.feign;

import com.yxq.apis.article.IArticleClient;
import com.yxq.article.service.ApArticleService;
import com.yxq.model.article.dtos.ArticleDto;
import com.yxq.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/12
 */
@RestController
public class ArticleClient implements IArticleClient {

    @Autowired
    private ApArticleService apArticleService;

    @PostMapping("/api/v1/article/save")
    @Override
    public ResponseResult saveArticle(@RequestBody ArticleDto dto) {
        return apArticleService.save(dto);
    }
}
