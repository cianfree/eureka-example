package com.github.eureka.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Arvin
 * @version 1.0
 * @since 2018/8/3 18:25
 */
@RestController
public class MonitorController {

    @GetMapping("/monitor/monitor.do")
    public String monitor() {
        return "OK";
    }
}
