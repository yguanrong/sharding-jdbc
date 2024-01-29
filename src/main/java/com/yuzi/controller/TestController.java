package com.yuzi.controller;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by YangGuanRong
 * date: 2023/4/10
 */
@RestController
public class TestController {

    @Resource
    private DiscoveryClient discoveryClient;

    @GetMapping("/services")
    public List<String> services() {
        return this.discoveryClient.getServices();
    }

    @GetMapping("/testHystrix")
    public String testHystrix() throws InterruptedException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader("token");
            System.out.println("token = " + token);
        }
        Thread.sleep(1000);
        return "success ，is ok";
    }

}
