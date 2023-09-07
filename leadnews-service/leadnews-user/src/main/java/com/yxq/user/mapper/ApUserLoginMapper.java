package com.yxq.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yxq.model.user.pojos.ApUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/6
 */
@Mapper
public interface ApUserLoginMapper extends BaseMapper<ApUser> {
}
