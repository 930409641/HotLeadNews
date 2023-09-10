package com.yxq.model.wemedia.dtos;

import com.yxq.model.common.dtos.PageRequestDto;
import lombok.Data;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/10
 */
@Data
public class WmMaterialDto extends PageRequestDto {
    private Short isCollection;
}
