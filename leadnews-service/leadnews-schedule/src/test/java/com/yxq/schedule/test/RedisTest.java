package com.yxq.schedule.test;

import com.yxq.common.redis.CacheService;
import com.yxq.model.schedule.dto.Task;
import com.yxq.schedule.ScheduleApplication;
import com.yxq.schedule.service.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.util.Date;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/14
 */
@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
public class RedisTest {

    @Resource
    private CacheService cacheService;

    @Resource
    private TaskService taskService;

    @Test
    public void testList() {
        //在list的左边添加元素
        cacheService.lLeftPush("list_001","hello,redis");
        //在list右边添加数据


    }

    @Test
    public void testZset() {

    }


    @Test
    public void testAddSchedule() {
        Task task = new Task();
        task.setTaskType(100);
        task.setPriority(50);
        task.setParameters("task test".getBytes());
        task.setExecuteTime(new Date().getTime());

        taskService.addTask(task);
        System.out.println(task.getTaskId());

    }

    @Test
    public void cancelTask() {
        taskService.cancelTask(1702490517348040705L);
    }

}
