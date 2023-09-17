package com.yxq.model.schedule.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/14
 */
@Data
public class Task implements Serializable {

    /**
     * 任务id
     */
    private Long taskId;
    /**
     * 类型
     */
    private Integer taskType;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 执行id
     */
    private long executeTime;

    /**
     * task参数
     */
    private byte[] parameters;

}
