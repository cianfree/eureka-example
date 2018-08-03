package com.github.service.provider.hello.controller;

import com.github.service.hello.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Arvin
 */
@RestController
public class HelloController {

    @Autowired
    private HelloService helloService;

    @GetMapping("/sayHello")
    @ResponseBody
    public String sayHello(String name) {
        return helloService.sayHello(name);
    }
}
