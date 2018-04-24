package me.zkutils.loadbalance.recipes.dns;

import me.zkutils.loadbalance.ServicePayLoad;
import me.zkutils.loadbalance.consumer.discovery.ServiceQuery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author paranoidq
 * @since 1.0.0
 */

public final class DnsQuery {
    private static Logger logger = LoggerFactory.getLogger(DnsQuery.class);

    private ServiceQuery query;

    /**
     * 解析Domain，并返回DnsEntry
     *
     * @param domainName
     * @return
     */
    public DnsEntry resolver(String domainName) {
        try {
            ServiceInstance<ServicePayLoad> serviceInstance = query.getService(domainName);
            String address = serviceInstance.getAddress();
            int port = serviceInstance.getPort();
            return new DnsEntry(address, port);
        } catch (Exception e) {
            logger.error("Resolve domain error", e);
        }
        return null;
    }


    public DnsQuery(ServiceQuery query) {
        this.query = query;
    }

    public void setQuery(ServiceQuery query) {
        this.query = query;
    }

    public static void main(String[] args) throws Exception {
        ServiceQuery serviceQuery = new ServiceQuery("localhost", "dns");
        serviceQuery.setProviderStrategy(new RoundRobinStrategy<>());
        serviceQuery.start();
        DnsQuery dnsQuery = new DnsQuery(serviceQuery);
        for (int i = 0; i < 10000; i++) {
            DnsEntry dnsEntry = dnsQuery.resolver("test.com.up");
            System.out.println("解析DNS: " + dnsEntry.toString());
            TimeUnit.SECONDS.sleep(2);
        }

        /*
            output:
                解析DNS: DnsEntry{host='localhost', port=1002}
                解析DNS: DnsEntry{host='localhost', port=1001}
                解析DNS: DnsEntry{host='localhost', port=1002}
                解析DNS: DnsEntry{host='localhost', port=1001}
                解析DNS: DnsEntry{host='localhost', port=1002}
         */
    }


}
