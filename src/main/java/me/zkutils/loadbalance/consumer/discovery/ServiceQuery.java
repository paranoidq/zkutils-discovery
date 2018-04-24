package me.zkutils.loadbalance.consumer.discovery;


import me.zkutils.loadbalance.ServicePayLoad;
import me.zkutils.loadbalance.consumer.utils.Defaults;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.*;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.strategies.RandomStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ServiceQuery {
    private static Logger logger = LoggerFactory.getLogger(ServiceQuery.class);

    private ConcurrentHashMap<String, ServiceProvider<ServicePayLoad>> cache = new ConcurrentHashMap<>();

    private CuratorFramework client;
    private ServiceDiscovery<ServicePayLoad> serviceDiscovery;

    private String connectString;
    private String basePath;
    private ProviderStrategy<ServicePayLoad> providerStrategy = new RandomStrategy<>();
    private int connectTimeout = Defaults.CONNECT_TIMEOUT_MS;
    private int sessionTimeout = Defaults.SESSION_TIMEOUT_MS;
    private int maxConnectRetries = Defaults.MAX_CONNECT_RETRIES;

    private volatile boolean initialized = false;

    /**
     * 构造函数
     * 必须提供connectString和basePath
     * @param connectString
     * @param basePath
     */
    public ServiceQuery(String connectString, String basePath) {
        this.connectString = connectString;
        this.basePath = basePath;
    }

    /**
     * 获取Service
     * @param serviceName
     * @return
     * @throws Exception
     */
    public ServiceInstance<ServicePayLoad> getService(String serviceName) throws Exception {
        ServiceProvider<ServicePayLoad> serviceProvider = cache.get(serviceName);
        if (serviceProvider == null) {
            serviceProvider = serviceDiscovery.serviceProviderBuilder()
                .serviceName(serviceName)
                .providerStrategy(providerStrategy)
                .build();

            ServiceProvider<ServicePayLoad> provider = cache.putIfAbsent(serviceName, serviceProvider);
            if (provider != null) {
                serviceProvider = provider;
            } else {
                serviceProvider.start();
            }
        }
        // IMPORTANT: users should not hold on to the instance returned. They should always get a fresh instance.
        // 每次都应该获取一个新的instance
        return serviceProvider.getInstance();
    }


    /**
     * 启动ServiceQuery内部组件
     * @throws Exception
     */
    public void start() throws Exception {
        if (!initialized) {
            synchronized (ServiceQuery.class) {
                if (!initialized) {
                    init();
                    initialized = true;

                    client.start();
                    serviceDiscovery.start();
                }
            }
        }
    }

    /**
     * 关闭ServiceQuery内部组件
     */
    public void stop() {
        CloseableUtils.closeQuietly(client);
        CloseableUtils.closeQuietly(serviceDiscovery);
        for (ServiceProvider provider : cache.values()) {
            CloseableUtils.closeQuietly(provider);
        }
    }


    /**
     * 初始化{@link CuratorFramework}和{@link ServiceDiscovery}
     * @throws Exception
     */
    private synchronized void init() throws Exception {
        client = CuratorFrameworkFactory.newClient(connectString, connectTimeout, sessionTimeout, new ExponentialBackoffRetry(1000, maxConnectRetries));
        // 构造ServiceDiscovery实例
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServicePayLoad.class)
            .basePath(basePath)
            .client(client)
            .serializer(new JsonInstanceSerializer<ServicePayLoad>(ServicePayLoad.class))
//            .thisInstance() ??
//            .watchInstances() ??
            .build();

        // TODO ??? 如何处理
        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                if (newState == ConnectionState.LOST) {

                }
            }
        });
    }


    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void setProviderStrategy(ProviderStrategy<ServicePayLoad> providerStrategy) {
        this.providerStrategy = providerStrategy;
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
}
