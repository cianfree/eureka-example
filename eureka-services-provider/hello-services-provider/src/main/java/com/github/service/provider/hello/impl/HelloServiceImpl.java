package com.github.service.provider.hello.impl;

import com.github.service.hello.HelloService;
import org.springframework.stereotype.Component;

/**
 * @author Arvin
 * @version 1.0
 * @since 2018/8/3 9:44
 */
@Component
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "Hello, " + name;
    }
}
