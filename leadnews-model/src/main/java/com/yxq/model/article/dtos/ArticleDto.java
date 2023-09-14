package com.yxq.model.article.dtos;

import com.yxq.model.article.pojos.ApArticle;
import lombok.Data;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/12
 */
@Data
public class ArticleDto extends ApArticle {

    /**
     * 文章内容
     */
    private String content;
}
