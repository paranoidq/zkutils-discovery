package me.zkutils.loadbalance.client.strategy;

import org.apache.curator.x.discovery.ProviderStrategy;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.InstanceProvider;

import java.util.List;

/**
 *
 * 实现根据流量动态均衡的策略
 * @author paranoidq
 * @since 1.0.0
 */
public class LoadStrategy<T> implements ProviderStrategy<T> {


    //TODO
    public ServiceInstance<T> getInstance(InstanceProvider<T> instanceProvider) throws Exception {
        List<ServiceInstance<T>> instances = instanceProvider.getInstances();
        for (ServiceInstance instance : instances) {

        }

        return instances.get(0);
    }

}
