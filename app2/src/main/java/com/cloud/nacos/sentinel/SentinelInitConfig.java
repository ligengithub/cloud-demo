package com.cloud.nacos.sentinel;

import com.alibaba.cloud.sentinel.SentinelProperties;
import com.alibaba.cloud.sentinel.datasource.RuleType;
import com.alibaba.cloud.sentinel.datasource.config.DataSourcePropertiesConfiguration;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author ligen
 * @title: NacosStartService
 * @projectName dashboardplus
 * @description:
 * @date 2020/4/214:08
 */
@Configuration
public class SentinelInitConfig implements CommandLineRunner {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SentinelProperties sentinelProperties;

    @Autowired
    private SentinelRuleProperties ruleProperties;

    @Override
    public void run(String... args) {
        // 注册规则源头
        initNacosConfigFromNacos();
        // 注册dubbo限流失败回调
//        initDubboFallback();
    }

    /**
     * @ desc : 注册规则监听器
     * @ params
     * @ return
     * @ date 2020/6/1
     * @ author ligen
     */
    private void initNacosConfigFromNacos() {
        logger.info("[NacosSource初始化,从Nacos中获取规则]");
        sentinelProperties.getDatasource().values().stream().filter(dataSourcePropertiesConfiguration -> dataSourcePropertiesConfiguration.getNacos() != null).map(DataSourcePropertiesConfiguration::getNacos).forEach(nacos -> {
            Properties properties = new Properties() {{
                setProperty("serverAddr", nacos.getServerAddr());
                setProperty("namespace", ruleProperties.getNacos().getNamespace());
            }};
            if (Objects.equals(nacos.getRuleType(), RuleType.FLOW)) {
                ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(properties,
                        nacos.getGroupId(), nacos.getDataId(),
                        source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
                        }));
                FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
            }
            if (Objects.equals(nacos.getRuleType(), RuleType.DEGRADE)) {
                ReadableDataSource<String, List<DegradeRule>> degradeRuleDataSource = new NacosDataSource<>(properties,
                        nacos.getGroupId(), nacos.getDataId(),
                        source -> JSON.parseObject(source, new TypeReference<List<DegradeRule>>() {
                        }));
                DegradeRuleManager.register2Property(degradeRuleDataSource.getProperty());
            }
            // 热点参数规则
            if (Objects.equals(nacos.getRuleType(), RuleType.PARAM_FLOW)) {
                ReadableDataSource<String, List<ParamFlowRule>> paramFlowRuleDataSource = new NacosDataSource<>(properties,
                        nacos.getGroupId(), nacos.getDataId(),
                        source -> JSON.parseObject(source, new TypeReference<List<ParamFlowRule>>() {
                        })
                );
                ParamFlowRuleManager.register2Property(paramFlowRuleDataSource.getProperty());

            }
            // 授权规则
            if (Objects.equals(nacos.getRuleType(), RuleType.AUTHORITY)) {
                ReadableDataSource<String, List<AuthorityRule>> authorityRuleDataSource = new NacosDataSource<>(properties,
                        nacos.getGroupId(), nacos.getDataId(),
                        source -> JSON.parseObject(source, new TypeReference<List<AuthorityRule>>() {
                        }));
                AuthorityRuleManager.register2Property(authorityRuleDataSource.getProperty());
            }
            // 系统规则
            if (Objects.equals(nacos.getRuleType(), RuleType.SYSTEM)) {
                ReadableDataSource<String, List<SystemRule>> systemRuleDataSource = new NacosDataSource<>(properties,
                        nacos.getGroupId(), nacos.getDataId(),
                        source -> JSON.parseObject(source, new TypeReference<List<SystemRule>>() {
                        }));
                SystemRuleManager.register2Property(systemRuleDataSource.getProperty());
            }
        });
    }

//    /**
//     * @ desc : 注册Dubbo失败回调函数
//     * @ params
//     * @ return
//     * @ date 2020/6/1
//     * @ author ligen
//     */
//    private void initDubboFallback() {
//        DubboFallbackRegistry.setProviderFallback(new DubboProviderFallback());
//        DubboFallbackRegistry.setConsumerFallback(new DubboConsumeFallback());
//    }


}
