package com.atguigu.yygh.task.scheduled;

import com.atguigu.common.rabbit.constant.MqConst;
import com.atguigu.common.rabbit.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScheduledTask {

    @Autowired
    private RabbitService rabbitService;

    //定时任务方法
    //每天八点执行方法,进行提醒
    // cron 表达式, 设置执行间隔
    // 0 0 8 * * ? *
    @Scheduled(cron = "0/30 * * * * ?")
    public void TaskPatient() {
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK, MqConst.ROUTING_TASK_8, "");
    }
}
