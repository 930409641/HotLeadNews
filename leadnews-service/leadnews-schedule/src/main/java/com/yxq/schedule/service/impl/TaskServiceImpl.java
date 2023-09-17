package com.yxq.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yxq.common.constants.ScheduleConstants;
import com.yxq.common.redis.CacheService;
import com.yxq.model.schedule.dto.Task;
import com.yxq.model.schedule.pojos.Taskinfo;
import com.yxq.model.schedule.pojos.TaskinfoLogs;
import com.yxq.schedule.mapper.TaskinfoLogsMapper;
import com.yxq.schedule.mapper.TaskinfoMapper;
import com.yxq.schedule.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/14
 */
@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskinfoMapper taskinfoMapper;

    @Autowired
    private TaskinfoLogsMapper taskinfoLogsMapper;

    @Resource
    private CacheService cacheService;

    @Override
    public long addTask(Task task) {
        //保存任务到数据库
        boolean success = addTaskToDb(task);

        if(success) {
            //保存任务到redis
            addTaskToCache(task);
        }

        return task.getTaskId();
    }

    @Override
    public boolean cancelTask(long taskId) {

        boolean flag = false;

        //删除任务，更新日志
        Task task = updateDb(taskId,ScheduleConstants.CANCELLED);
        //删除redis的数据
        if(task != null) {
            removeTaskFromCache(task);
            flag = true;
        }
        return flag;
    }

    @Override
    public Task poll(int type, int priority) {
        Task task = null;
        try {
            String key = ScheduleConstants.TOPIC+type+"_"+priority;
            //移除最右边的元素
            String task_json = cacheService.lRightPop(key);
            if(StringUtils.isNotBlank(task_json)) {
                task = JSON.parseObject(task_json, Task.class);
                //更新数据库中的信息
                updateDb(task.getTaskId(),ScheduleConstants.EXECUTED);

            }
        }catch (Exception e) {
            e.printStackTrace();
            log.error("poll task exception");
        }

        return task;
    }

    /**
     * 未来数据定时刷新
     */
    @Scheduled(cron = "0 */1 * * * ?")
    @Override
    public void refresh() {
        String token = cacheService.tryLock("FUTURE_TASK_SYNC", 1000 * 30);
        if(StringUtils.isNotBlank(token)){
            //获取所有未来任务的集合的key值
            Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");
            for (String futureKey : futureKeys) { //future_250_250
                String topicKey = ScheduleConstants.TOPIC+futureKey.split(ScheduleConstants.FUTURE)[1];

                //获取该key下当前需要消费的任务
                Set<String> tasks = cacheService.zRangeByScore(futureKey, 0, System.currentTimeMillis());
                if(tasks != null) {
                    //将这些任务数据添加到消费者队列中
                    cacheService.refreshWithPipeline(futureKey,topicKey,tasks);
                    System.out.println("成功的将" + futureKey + "下的当前需要执行的任务数据刷新到" + topicKey + "下");
                }
            }
        }

    }

    @Scheduled(cron = "0 */5 * * * ?")
    @PostConstruct
    @Override
    public void reloadData() {
        //清除redis缓存
        clearCache();

        //将数据库中数据同步到redis
        //查看小于未来5分钟的任务
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,5);

        List<Taskinfo> taskinfos = taskinfoMapper.selectList(Wrappers
                .<Taskinfo>lambdaQuery()
                .lt(Taskinfo::getExecuteTime, calendar.getTime()));
        if(taskinfos != null && taskinfos.size() != 0) {
            for (Taskinfo taskinfo : taskinfos) {
                Task task = new Task();
                BeanUtils.copyProperties(taskinfo,task);
                task.setExecuteTime(taskinfo.getExecuteTime().getTime());
                addTaskToCache(task);
            }
        }
        System.out.println("执行定时同步任务");

    }

    private void clearCache() {
        // 删除缓存中未来数据集合和当前消费者队列的所有key
        Set<String> topicKeys = cacheService.scan(ScheduleConstants.TOPIC + "*");
        Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");
        cacheService.delete(topicKeys);
        cacheService.delete(futureKeys);
    }


    private void removeTaskFromCache(Task task) {
        String key = task.getTaskType()+"_"+task.getPriority();
        if(task.getExecuteTime()<=System.currentTimeMillis()){
            cacheService.lRemove(ScheduleConstants.TOPIC+key,0,JSON.toJSONString(task));
        }else {
            cacheService.zRemove(ScheduleConstants.FUTURE+key, JSON.toJSONString(task));
        }
    }

    private Task updateDb(long taskId, int status) {
        Task task = null;

        try {
            //删除taskInfo
            taskinfoMapper.deleteById(taskId);

            //更新taskInfoLogs
            TaskinfoLogs taskinfoLogs = taskinfoLogsMapper.selectById(taskId);
            taskinfoLogs.setStatus(status);
            taskinfoLogsMapper.updateById(taskinfoLogs);
            task = new Task();
            BeanUtils.copyProperties(taskinfoLogs,task);
            task.setExecuteTime(taskinfoLogs.getExecuteTime().getTime());
        }catch (Exception e) {
            log.error("task cancel exception taskid={}",taskId);
        }

        return task;
    }

    private void addTaskToCache(Task task) {
        String key = task.getTaskType() +"_"+ task.getPriority();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,5);
        long nextScheduleTime = calendar.getTimeInMillis();

        //2.1如果任务的执行时间小于等于当前时间，存入list
        if (task.getExecuteTime() <= System.currentTimeMillis()) {
            cacheService.lLeftPush(ScheduleConstants.TOPIC + key, JSON.toJSONString(task));
        } else if (task.getExecuteTime() <= nextScheduleTime) {
            //2.2 如果任务时间大于等于当前时间 && 小于等于预设时间（未来五分钟） 存入zset
            cacheService.zAdd(ScheduleConstants.FUTURE+key,JSON.toJSONString(task),task.getExecuteTime());
        }

    }

    private boolean addTaskToDb(Task task) {

        boolean flag = false;

        try {
            //保存任务表
            Taskinfo taskinfo = new Taskinfo();
            BeanUtils.copyProperties(task,taskinfo);
            taskinfo.setExecuteTime(new Date(task.getExecuteTime()));
            taskinfoMapper.insert(taskinfo);

            //设置taskId
            task.setTaskId(taskinfo.getTaskId());
            //保存任务日志表
            TaskinfoLogs taskinfoLogs = new TaskinfoLogs();
            BeanUtils.copyProperties(task,taskinfoLogs);
            taskinfoLogs.setExecuteTime(new Date(task.getExecuteTime()));
            taskinfoLogs.setStatus(ScheduleConstants.SCHEDULED);
            taskinfoLogs.setVersion(1);
            taskinfoLogsMapper.insert(taskinfoLogs);
            flag = true;
        }catch (Exception e) {
            e.printStackTrace();
        }

        return flag;
    }
}
