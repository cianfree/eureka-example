package com.github.service.provider.hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author Arvin
 * @version 1.0
 * @since 2018/8/3 9:45
 */
@SpringBootApplication
@EnableEurekaClient
public class HelloServiceProvider {

    public static void main(String[] args) {
        SpringApplication.run(HelloServiceProvider.class, args);
    }
}
