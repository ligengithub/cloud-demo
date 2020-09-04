package com.cloud.nacos.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author ligen
 * @title: TestController
 * @projectName nacos
 * @description:
 * @date 2020/8/279:05
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public Long test() throws InterruptedException {
        System.out.println(System.currentTimeMillis());
        Thread.sleep(100);
        return System.currentTimeMillis();
    }


}
