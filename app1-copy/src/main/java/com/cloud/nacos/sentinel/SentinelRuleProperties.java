package com.cloud.nacos.sentinel;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ligen
 * @title: SentinelRuleProperties
 * @projectName simlink
 * @description:
 * @date 2020/4/2117:25
 */
@Component
@ConfigurationProperties(prefix = "sentinel-rule")
public class SentinelRuleProperties {

    private Nacos nacos = new Nacos();


    public Nacos getNacos() {
        return nacos;
    }
    public static class Nacos {
        private String server;
        private String namespace;
        private String groupId;

        public String getServer() {
            return server;
        }

        public void setServer(String server) {
            this.server = server;
        }

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }
    }
}
