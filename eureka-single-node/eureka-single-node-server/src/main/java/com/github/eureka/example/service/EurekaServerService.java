package com.github.eureka.example.service;

import com.alibaba.fastjson.JSON;
import com.github.eureka.example.model.EurekaServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Arvin
 * @version 1.0
 * @since 2018/8/3 17:04
 */
@Component
public class EurekaServerService {

    private static final String KEY = "eureka.server.set";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取所有的服务列表
     *
     * @return 返回 Eureka 服务列表
     */
    public List<EurekaServer> list() {
        Set<String> jsonList = stringRedisTemplate.opsForSet().members(KEY);

        List<EurekaServer> serverList = new ArrayList<>(jsonList.size());

        for (String item : jsonList) {
            serverList.add(JSON.toJavaObject(JSON.parseObject(item), EurekaServer.class));
        }

        return serverList;
    }

    /**
     * 添加一个 Eureka Server
     *
     * @param server 要添加的 Eureka 服务
     */
    public void add(EurekaServer server) {
        String member = JSON.toJSONString(server);
        stringRedisTemplate.opsForSet().add(KEY, member);
    }

    /**
     * 删除一个 EurekaServer 对象
     *
     * @param server 要删除的 Eureka 服务
     */
    public void remove(EurekaServer server) {
        String member = JSON.toJSONString(server);
        stringRedisTemplate.opsForSet().remove(KEY, member);
    }
}
