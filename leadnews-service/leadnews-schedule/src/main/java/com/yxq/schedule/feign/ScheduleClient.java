package com.yxq.schedule.feign;

import com.yxq.apis.schedule.IScheduleClient;
import com.yxq.model.common.dtos.ResponseResult;
import com.yxq.model.schedule.dto.Task;
import com.yxq.schedule.service.TaskService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/16
 */
@RestController
public class ScheduleClient implements IScheduleClient {

    @Resource
    private TaskService taskService;

    @PostMapping("/api/v1/task/add")
    @Override
    public ResponseResult addTask(@RequestBody Task task) {
        return ResponseResult.okResult(taskService.addTask(task));
    }

    @GetMapping("/api/v1/task/{taskId}")
    @Override
    public ResponseResult cancelTask(@PathVariable("taskId") long taskId) {
        return ResponseResult.okResult(taskService.cancelTask(taskId));
    }

    @GetMapping("/api/v1/task/{type}/{priority}")
    @Override
    public ResponseResult poll(@PathVariable("type") int type,@PathVariable("priority") int priority) {
        return ResponseResult.okResult(taskService.poll(type,priority));
    }
}
