package me.zkutils.discovery.recipes.dns;

import me.zkutils.discovery.ServicePayLoad;
import me.zkutils.discovery.provider.registry.ServiceRegistry;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class DnsRegistry {

    private static Logger logger = LoggerFactory.getLogger(DnsRegistry.class);

    private ServiceRegistry registry;

    public void registerDns(String domain, DnsEntry entry) throws Exception {
        ServiceInstance serviceInstance = ServiceInstance.<ServicePayLoad>builder()
            .name(domain)
            .id(entry.getHost() + ":" + entry.getPort())
            .address(entry.getHost())
            .port(entry.getPort())
            .build();
        registry.registerService(serviceInstance);
    }




    public DnsRegistry(ServiceRegistry registry) {
        this.registry = registry;
    }

    public void setRegistry(ServiceRegistry registry) {
        this.registry = registry;
    }


    public static void main(String[] args) throws Exception {
        ServiceRegistry serviceRegistry = new ServiceRegistry("localhost", "/dns");
        serviceRegistry.start();
        DnsRegistry dns = new DnsRegistry(serviceRegistry);
        dns.registerDns("test.com.up", new DnsEntry("localhost", 1001));
        dns.registerDns("test.com.up", new DnsEntry("localhost", 1002));

        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);

    }
}
