package com.github.service.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arvin
 * @version 1.0
 * @since 2018/8/3 11:24
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceConsumer implements CommandLineRunner {

    public static void main(String[] args) {

        String profile = "single";

        System.setProperty("spring.profiles.active", profile);

        SpringApplication.run(ServiceConsumer.class, args);
    }

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(restTemplate);

        testHiService();

        System.exit(-1);
    }

    private void testHelloService() {
        Map<String, Object> paramMap = new HashMap<String, Object>() {
            {
                put("name", "Arvin");
            }
        };

        // 一定要用占位符, 下面两种写法都可以
        String text = this.restTemplate.getForObject("http://hello-service/sayHello?name={name}", String.class, paramMap);
        System.out.println("ResponseText1: " + text);
        text = this.restTemplate.getForObject("http://hello-service/sayHello?name={name}", String.class, "Arvin");
        System.out.println("ResponseText2: " + text);

    }

    private void testHiService() {
        Map<String, Object> paramMap = new HashMap<String, Object>() {
            {
                put("name", "Arvin");
            }
        };

        // 一定要用占位符, 下面两种写法都可以
        String text = this.restTemplate.getForObject("http://hi-service/sayHi?name={name}", String.class, paramMap);
        System.out.println("ResponseText1: " + text);
        text = this.restTemplate.getForObject("http://hi-service/sayHi?name={name}", String.class, "Arvin");
        System.out.println("ResponseText2: " + text);
    }
}
