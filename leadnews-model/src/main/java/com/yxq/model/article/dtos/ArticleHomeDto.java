package com.yxq.model.article.dtos;

import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/7
 */
@Data
public class ArticleHomeDto {

    // 最大时间
    Date maxBehotTime;
    // 最小时间
    Date minBehotTime;
    // 分页size
    Integer size;
    // 频道ID
    String tag;
}
