package me.zkutils.loadbalance.server.registry;

import me.zkutils.loadbalance.ServicePayLoad;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Collection;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ServiceRegistry implements FactoryBean<ServiceDiscovery>, InitializingBean, DisposableBean {

    private Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

    private ServiceDiscovery<ServicePayLoad> serviceDiscovery;


    private CuratorFramework client;
    private String basePath;
    private String connectString;


    // setע��
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    // setע��
    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    public ServiceRegistry() {

    }

    // constructorע��
    public ServiceRegistry(String basePath, String connectString) {
        this.basePath = basePath;
        this.connectString = connectString;
    }


    public void registerService(ServiceInstance<ServicePayLoad> instance) throws Exception {
        serviceDiscovery.registerService(instance);
    }


    public void updateService(ServiceInstance<ServicePayLoad> instance) throws Exception {
        serviceDiscovery.updateService(instance);
    }

    public void unregisterService(ServiceInstance<ServicePayLoad> instance) throws Exception {
        serviceDiscovery.unregisterService(instance);
    }

    public Collection<ServiceInstance<ServicePayLoad>> queryForInstances(String name) throws Exception {
        return serviceDiscovery.queryForInstances(name);
    }

    public ServiceInstance<ServicePayLoad> queryForInstance(String name, String id) throws Exception {
        return serviceDiscovery.queryForInstance(name, id);
    }

    public void start() throws Exception {
        client.start();
        serviceDiscovery.start();
    }

    public void stop() {
        CloseableUtils.closeQuietly(serviceDiscovery);
        CloseableUtils.closeQuietly(client);
    }

    public void init() throws Exception {
        client = CuratorFrameworkFactory.newClient(connectString, 2000, 2000, new ExponentialBackoffRetry(1000, 3));

        // ����ServiceDiscoveryʵ��
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServicePayLoad.class)
            .basePath(basePath)
            .client(client)
            .serializer(new JsonInstanceSerializer<ServicePayLoad>(ServicePayLoad.class))
            .build();
    }



    //
    //  For spring ioc
    //

    public void destroy() throws Exception {
        this.stop();
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
        this.init();
        this.start();
    }


    public static void main(String[] args) throws Exception {

        // no use, just to demonstrate usage for non-spring cases
        String connectString = "";
        String basePath = "";
        ServiceRegistry registry = new ServiceRegistry(connectString, basePath);
        registry.init();
        registry.start();
        registry.registerService(null);
    }
}
