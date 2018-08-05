package com.github.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Arvin.
 */
@Controller
public class HiController {

    @ResponseBody
    @RequestMapping("/sayHi")
    public String sayHi(String name) {
        return "Hi, " + name;
    }
}
