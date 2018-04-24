# Zookeeper搭建服务注册与发现框架

## 服务发现
- 基于正常API方式调用
- 基于Spring Bean方式注入
- 基于动态流量的均衡loadbalance策略 （TODO）


## 服务注册
- 基于正常API方式调用
- 基于Spring Bean方式注入


## 用例

### Dns动态注册和查询
#### spring方式配置
``` xml
    <!-- dnsquery -->
    <bean class="me.zkutils.discovery.consumer.discovery.ServiceQuery" id="dnsQuery">
        <constructor-arg name="basePath" value="/dns"></constructor-arg>
        <constructor-arg name="connectString" value="localhost"></constructor-arg>
        <property name="providerStrategy"><bean class="org.apache.curator.x.discovery.strategies.RoundRobinStrategy"></bean></property>
    </bean>
    <bean class="me.zkutils.discovery.recipes.dns.DnsQuery" lazy-init="true">
        <constructor-arg ref="dnsQuery"/>
    </bean>

    <!-- dnsregistry -->
    <bean class="me.zkutils.discovery.provider.registry.ServiceRegistry" id="dnsRegistry">
        <constructor-arg name="basePath" value="/dns"></constructor-arg>
        <constructor-arg name="connectString" value="localhost"></constructor-arg>
    </bean>
    <bean class="me.zkutils.discovery.recipes.dns.DnsRegistry" lazy-init="true">
        <constructor-arg ref="dnsRegistry"/>
    </bean>
```

#### 代码方式
``` java
    // dnsregistry
    ServiceRegistry serviceRegistry = new ServiceRegistry("localhost", "/dns");
    serviceRegistry.start();
    DnsRegistry dnsRegistry = new DnsRegistry(serviceRegistry);
    dnsRegistry.register("test.com.up", new DnsEnrty("localhost", 8080));
    ...

    // dnsQuery
    ServiceQuery serviceQuery = new ServiceQuery("localhost", "dns");
    serviceQuery.setProviderStrategy(new RoundRobinStrategy<>());
    serviceQuery.start();
    DnsQuery dnsQuery = new DnsQuery(serviceQuery);
    DnsEntry rst = dnsQuery.resolve("test.com.up");
    ...


```
