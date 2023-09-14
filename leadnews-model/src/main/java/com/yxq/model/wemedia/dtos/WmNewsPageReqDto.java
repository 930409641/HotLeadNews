package com.yxq.model.wemedia.dtos;

import com.yxq.model.common.dtos.PageRequestDto;
import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/10
 */
@Data
public class WmNewsPageReqDto extends PageRequestDto {

    /**
     * 状态
     */
    private Short status;
    /**
     * 开始时间
     */
    private Date beginPubDate;
    /**
     * 结束时间
     */
    private Date endPubDate;
    /**
     * 所属频道ID
     */
    private Integer channelId;
    /**
     * 关键字
     */
    private String keyword;
}
