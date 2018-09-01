package com.yq.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by yangqian on 2018/8/7.
 */

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String handle() {
        return "Hello World";
    }



}