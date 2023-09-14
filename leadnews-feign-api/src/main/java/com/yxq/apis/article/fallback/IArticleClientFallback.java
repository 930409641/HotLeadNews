package com.yxq.apis.article.fallback;

import com.yxq.apis.article.IArticleClient;
import com.yxq.model.article.dtos.ArticleDto;
import com.yxq.model.common.dtos.ResponseResult;
import com.yxq.model.common.enums.AppHttpCodeEnum;
import org.springframework.stereotype.Component;

/**
 * @Description: feign失败配置
 * @Author: yxq
 * @Date: 2023/9/13
 */

@Component
public class IArticleClientFallback implements IArticleClient {
    @Override
    public ResponseResult saveArticle(ArticleDto dto) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR,"获取数据失败");
    }
}
