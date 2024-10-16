package com.example.demo.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.example.demo.service.DeviceControlService;
import com.example.demo.service.ScheduledTask;

@Component
public class ServerPushingEventListener implements ApplicationListener<ServerPushingEvent> {
    private static final Logger log = LoggerFactory.getLogger(ServerPushingEventListener.class);

    private final DeviceControlService deviceControlService;
    private final ScheduledTask scheduledTask;

    public ServerPushingEventListener(DeviceControlService deviceControlService, ScheduledTask scheduledTask) {
        this.deviceControlService = deviceControlService;
        this.scheduledTask = scheduledTask;
    }

    /**
     * 处理服务器推送事件
     *
     * @param event 服务器推送事件
     */
    @Override
    public void onApplicationEvent(ServerPushingEvent event) {
        if(log.isInfoEnabled()) {
            log.info("Received server pushing event: {}", event);
        }
        scheduledTask.performTask(); // 执行本地网络任务
    }
}
