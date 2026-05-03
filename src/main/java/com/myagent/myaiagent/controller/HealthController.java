package com.myagent.myaiagent.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


// 访问http://localhost:8123/api/doc.html可调试接口
@RestController
@RequestMapping("/health")
public class HealthController {
    @GetMapping
    public String healthCheck() {
        return "ok";
    }

}
