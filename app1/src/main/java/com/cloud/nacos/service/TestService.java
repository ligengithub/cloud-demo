package com.cloud.nacos.service;

import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.stereotype.Service;

/**
 * @author ligen
 * @title: TestService
 * @projectName cloud-demo
 * @description:
 * @date 2020/9/411:14
 */
@Service
public class TestService {


    @SentinelResource(value = "paramFlow1", entryType = EntryType.IN)
    public void test2(Integer param1, Integer param2) throws InterruptedException {
        Thread.sleep(100);
        System.out.println("param1:" + param1 + "------param2:" + param2);
    }

}
