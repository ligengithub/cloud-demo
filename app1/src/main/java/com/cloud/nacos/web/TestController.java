package com.cloud.nacos.web;

import com.cloud.nacos.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
    private TestService testService;


    /**
     * @ desc : 测试流控
     * @ author: ligen
     * @ date 2020/9/4
     */
    @GetMapping("/test")
    public Long test() throws InterruptedException {
        System.out.println(System.currentTimeMillis());
        Thread.sleep(100);
        return System.currentTimeMillis();
    }


    /**
     * @ desc : 测试降级
     * @ author: ligen
     * @ date 2020/9/4
     */
    @GetMapping("/test2/{param1}/{param2}")
    public String test2(@PathVariable("param1") Integer param1, @PathVariable("param2") Integer param2) throws InterruptedException {

        testService.test2(param1, param2);
        return param1.toString() + "-----" + param2.toString();
    }


}
