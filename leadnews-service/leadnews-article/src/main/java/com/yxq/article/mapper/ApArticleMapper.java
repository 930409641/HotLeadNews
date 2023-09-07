package com.yxq.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yxq.model.article.dtos.ArticleHomeDto;
import com.yxq.model.article.pojos.ApArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/7
 */
@Mapper
public interface ApArticleMapper extends BaseMapper<ApArticle> {

    List<ApArticle> loadArticleList(@Param("dto")ArticleHomeDto dto, @Param("type")Short type);
}
