package com.cloud.nacos.sentinel;

import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;

import java.util.Objects;
import java.util.Set;

/**
 * @author ligen
 * @title: MyApiDefine
 * @projectName simlink
 * @description: 自定义一个ApiDefin, 用来反序列化naocs的网关api分组规则,
 * 默认的ApiDefinition 中， 属性predicateItems定义的是接口，进行反序列化的时候。会丢失掉这部分数据，
 * 导致nacos上的api分组数据，不能被正确的注入到sentinel中。
 * @date 2020/4/1810:39
 */
public class MyApiDefine {

    private String apiName;

    private Set<ApiPathPredicateItem> predicateItems;

    public MyApiDefine() {
    }

    public MyApiDefine(String apiName) {
        this.apiName = apiName;
    }

    public String getApiName() {
        return this.apiName;
    }

    public MyApiDefine setApiName(String apiName) {
        this.apiName = apiName;
        return this;
    }


    public Set<ApiPathPredicateItem> getPredicateItems() {
        return predicateItems;
    }


    public MyApiDefine setPredicateItems(Set<ApiPathPredicateItem> predicateItems) {
        this.predicateItems = predicateItems;
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ApiDefinition that = (ApiDefinition) o;
            return !Objects.equals(this.apiName, that.getApiName()) ? false : Objects.equals(this.predicateItems, that.getPredicateItems());
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.apiName != null ? this.apiName.hashCode() : 0;
        result = 31 * result + (this.predicateItems != null ? this.predicateItems.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "ApiDefinition{apiName='" + this.apiName + '\'' + ", predicateItems=" + this.predicateItems + '}';
    }

}
