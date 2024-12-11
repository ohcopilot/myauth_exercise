package com.karl.mysecurity.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/test")
public class HelloWorld {
    @RequestMapping("/helloworld")
    public String helloWorld() {
        return "hello world";
    }
}
