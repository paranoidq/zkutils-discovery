package me.zkutils.loadbalance.client.discovery;


import me.zkutils.loadbalance.ServicePayLoad;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ServiceQuery implements FactoryBean<ServiceDiscovery>, InitializingBean, DisposableBean {

    private ConcurrentHashMap<String, ServiceProvider<ServicePayLoad>> cache = new ConcurrentHashMap<String, ServiceProvider<ServicePayLoad>>();

    private static Logger logger = LoggerFactory.getLogger(ServiceQuery.class);

    private CuratorFramework client;
    private ServiceDiscovery<ServicePayLoad> serviceDiscovery;

    private String connectString;
    private String basePath;

    // set注入
    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    // set注入
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    // constructor注入
    public ServiceQuery(String connectString, String basePath) {
        this.connectString = connectString;
        this.basePath = basePath;
    }

    public ServiceInstance<ServicePayLoad> getServiceInstance(String serviceName) throws Exception {
        ServiceProvider<ServicePayLoad> serviceProvider = cache.get(serviceName);
        if (serviceProvider == null) {
            serviceProvider = serviceDiscovery.serviceProviderBuilder()
                .serviceName(serviceName)
                .providerStrategy(new RoundRobinStrategy<ServicePayLoad>())
                .build();

            ServiceProvider<ServicePayLoad> provider = cache.putIfAbsent(serviceName, serviceProvider);
            if (provider != null) {
                serviceProvider = provider;
            } else {
                serviceProvider.start();
            }
        }
        return serviceProvider.getInstance();
    }

    public void start() throws Exception {
        client.start();
        serviceDiscovery.start();
    }


    public void init() throws Exception {
        client = CuratorFrameworkFactory.newClient(connectString, 2000, 2000, new ExponentialBackoffRetry(1000, 3));
        // 构造ServiceDiscovery实例
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServicePayLoad.class)
            .basePath(basePath)
            .client(client)
            .serializer(new JsonInstanceSerializer<ServicePayLoad>(ServicePayLoad.class))
            .build();
    }

    public void stop() {
        CloseableUtils.closeQuietly(client);
        CloseableUtils.closeQuietly(serviceDiscovery);
        for (ServiceProvider provider : cache.values()) {
            CloseableUtils.closeQuietly(provider);
        }
    }



    //
    //  for spring ioc
    //

    public void destroy() throws Exception {
        stop();
    }

    public ServiceDiscovery getObject() throws Exception {
        return this.serviceDiscovery;
    }

    public Class<?> getObjectType() {
        return ServiceDiscovery.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void afterPropertiesSet() throws Exception {
        init();
        start();
    }

}
