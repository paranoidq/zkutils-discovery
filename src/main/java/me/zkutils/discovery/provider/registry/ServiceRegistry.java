package me.zkutils.discovery.provider.registry;

import me.zkutils.discovery.ServicePayLoad;
import me.zkutils.discovery.consumer.utils.Defaults;
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

import java.util.Collection;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ServiceRegistry {

    private Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

    private CuratorFramework client;
    private ServiceDiscovery<ServicePayLoad> serviceDiscovery;

    private String basePath;
    private String connectString;
    private int connectTimeout = Defaults.CONNECT_TIMEOUT_MS;
    private int sessionTimeout = Defaults.SESSION_TIMEOUT_MS;
    private int maxConnectRetries = Defaults.MAX_CONNECT_RETRIES;

    private volatile boolean initialized = false;


    public ServiceRegistry(String connectString, String basePath) {
        this.connectString = connectString;
        this.basePath = basePath;
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
        if (!initialized) {
            synchronized (ServiceRegistry.class) {
                if (!initialized) {
                    init();
                    initialized = true;
                    client.start();
                    serviceDiscovery.start();
                }
            }
        }

    }

    public void stop() {
        CloseableUtils.closeQuietly(serviceDiscovery);
        CloseableUtils.closeQuietly(client);
    }

    private synchronized void init() throws Exception {
        client = CuratorFrameworkFactory.newClient(connectString, connectTimeout, sessionTimeout, new ExponentialBackoffRetry(1000, maxConnectRetries));

        // 构造ServiceDiscovery实例
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServicePayLoad.class)
            .basePath(basePath)
            .client(client)
            .serializer(new JsonInstanceSerializer<ServicePayLoad>(ServicePayLoad.class))
            .build();
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public void setMaxConnectRetries(int maxConnectRetries) {
        this.maxConnectRetries = maxConnectRetries;
    }

    //
    //  For spring ioc
    //

//    public void destroy() throws Exception {
//        this.stop();
//    }
//
//    public ServiceDiscovery getObject() throws Exception {
//        return this.serviceDiscovery;
//    }
//
//    public Class<?> getObjectType() {
//        return ServiceDiscovery.class;
//    }
//
//    public boolean isSingleton() {
//        return true;
//    }
//
//    public void afterPropertiesSet() throws Exception {
//        this.init();
//        this.start();
//    }
//
//
//    public static void main(String[] args) throws Exception {
//
//        // no use, just to demonstrate usage for non-spring cases
//        String connectString = "";
//        String basePath = "";
//        ServiceRegistry registry = new ServiceRegistry(connectString, basePath);
//        registry.init();
//        registry.start();
//        registry.registerService(null);
//    }
}
