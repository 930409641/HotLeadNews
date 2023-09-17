package com.yxq.article.service;

import com.yxq.model.article.pojos.ApArticle;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/14
 */
public interface ArticleFreemarkerService {

    /**
     * 生成静态文件上传到minIO中
     * @param apArticle
     * @param content
     */
    public void buildArticleToMinIO(ApArticle apArticle, String content);
}
