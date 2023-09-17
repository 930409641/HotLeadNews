package com.yxq.schedule.service;

import com.yxq.model.schedule.dto.Task;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/14
 */
public interface TaskService {

    public long addTask(Task task);

    public boolean cancelTask(long taskId);

    /**
     * 按照类型和优先级来拉取任务
     * @param type
     * @param priority
     * @return
     */
    public Task poll(int type,int priority);

    public void refresh();

    public void reloadData();

}
