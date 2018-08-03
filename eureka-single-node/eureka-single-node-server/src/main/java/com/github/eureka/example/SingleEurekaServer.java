package com.github.eureka.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Arvin
 * @version 1.0
 * @since 2018/8/3 9:27
 */
@SpringBootApplication
@EnableEurekaServer
@EnableScheduling
public class SingleEurekaServer {

    public static void main(String[] args) {
        SpringApplication.run(SingleEurekaServer.class, args);
    }
}
