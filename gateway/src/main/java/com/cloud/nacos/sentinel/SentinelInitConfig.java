package com.cloud.nacos.sentinel;

import com.alibaba.cloud.sentinel.SentinelProperties;
import com.alibaba.cloud.sentinel.datasource.RuleType;
import com.alibaba.cloud.sentinel.datasource.config.DataSourcePropertiesConfiguration;
import com.alibaba.cloud.sentinel.datasource.config.NacosDataSourceProperties;
import com.alibaba.csp.sentinel.adapter.dubbo.fallback.DubboFallbackRegistry;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.senthink.www.config.sentinel.fallback.DubboConsumeFallback;
import com.senthink.www.config.sentinel.fallback.DubboProviderFallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.*;

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
        initNacosConfigFromNacos();
        initDubboFallback();
    }

    //     网关没有热点规则和授权规则
    private void initNacosConfigFromNacos() {
        logger.info("[NacosSource初始化,从Nacos中获取规则]");
        for (DataSourcePropertiesConfiguration dataSourcePropertiesConfiguration : sentinelProperties.getDatasource().values()) {
            if (dataSourcePropertiesConfiguration.getNacos() != null) {
                NacosDataSourceProperties nacos = dataSourcePropertiesConfiguration.getNacos();
                Properties properties = new Properties() {{
                    setProperty("serverAddr", nacos.getServerAddr());
                    setProperty("namespace", ruleProperties.getNacos().getNamespace());
                }};
                // 网关分组规则
                if (Objects.equals(nacos.getRuleType(), RuleType.GW_API_GROUP)) {
                    ReadableDataSource<String, Set<ApiDefinition>> apiDefinition = new NacosDataSource<>(properties,
                            nacos.getGroupId(), nacos.getDataId(),
                            source -> {
                                Set<MyApiDefine> myApiDefines = JSON.parseObject(source, new TypeReference<Set<MyApiDefine>>() {
                                });
                                Set<ApiDefinition> apiDefinitions = new HashSet<>();
                                myApiDefines.forEach(myApiDefine -> {
                                    HashSet<ApiPredicateItem> apiPredicateItems = new HashSet<>();
                                    // 匹配规则赋值
                                    for (ApiPathPredicateItem p : myApiDefine.getPredicateItems()) {
                                        apiPredicateItems.add(p);
                                    }

                                    ApiDefinition temp = new ApiDefinition() {{
                                        setApiName(myApiDefine.getApiName());
                                        setPredicateItems(apiPredicateItems);
                                    }};

                                    apiDefinitions.add(temp);

                                });
                                return apiDefinitions;
                            });
                    // 注册规则
                    GatewayApiDefinitionManager.register2Property(apiDefinition.getProperty());
                }
                // 网关限流规则
                if (Objects.equals(nacos.getRuleType(), RuleType.GW_FLOW)) {
                    ReadableDataSource<String, Set<GatewayFlowRule>> flowRuleDataSource = new NacosDataSource<>(properties,
                            nacos.getGroupId(), nacos.getDataId(),
                            source -> JSON.parseObject(source, new TypeReference<Set<GatewayFlowRule>>() {
                            }));
                    GatewayRuleManager.register2Property(flowRuleDataSource.getProperty());
                }
                // 网关降级规则
                if (Objects.equals(nacos.getRuleType(), RuleType.DEGRADE)) {
                    ReadableDataSource<String, List<DegradeRule>> degradeRuleDataSource = new NacosDataSource<>(properties,
                            nacos.getGroupId(), nacos.getDataId(),
                            source -> JSON.parseObject(source, new TypeReference<List<DegradeRule>>() {
                            }));
                    DegradeRuleManager.register2Property(degradeRuleDataSource.getProperty());
                }
                // 系统规则
                if (Objects.equals(nacos.getRuleType(), RuleType.SYSTEM)) {
                    ReadableDataSource<String, List<SystemRule>> systemRuleDataSource = new NacosDataSource<>(properties,
                            nacos.getGroupId(), nacos.getDataId(),
                            source -> JSON.parseObject(source, new TypeReference<List<SystemRule>>() {
                            }));
                    SystemRuleManager.register2Property(systemRuleDataSource.getProperty());
                }
            }
        }
    }

    /**
     * @ desc : 注册Dubbo失败回调函数
     * @ params
     * @ return
     * @ date 2020/6/1
     * @ author ligen
     */
    private void initDubboFallback() {
        DubboFallbackRegistry.setProviderFallback(new DubboProviderFallback());
        DubboFallbackRegistry.setConsumerFallback(new DubboConsumeFallback());
    }

}
