package com.github.eureka.example.task;

import com.alibaba.fastjson.JSON;
import com.github.eureka.example.model.EurekaServer;
import com.github.eureka.example.service.EurekaServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 监控 Eureka 服务列表
 *
 * @author Arvin
 * @version 1.0
 * @since 2018/8/3 14:06
 */
@Component
public class MonitorEurekaServerTask {

    private static final Logger logger = LoggerFactory.getLogger(MonitorEurekaServerTask.class);

    @Autowired
    private EurekaServerService eurekaServerService;

    /**
     * 每隔 5 秒执行一次
     */
    @Scheduled(cron = "* 1 * * * *")
    public void eurekaServerMonitor() {

        List<EurekaServer> serverList = eurekaServerService.list();
        System.out.println("执行监控： " + JSON.toJSONString(serverList));

    }
}
