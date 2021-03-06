package me.zkutils.discovery.provider.sample;

import me.zkutils.discovery.ServicePayLoad;
import me.zkutils.discovery.provider.registry.ServiceRegistry;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class SampleServer {

    private static Logger logger = LoggerFactory.getLogger(SampleServer.class);

    private static String connectString = "localhost";
    private static String basePath = "/loadbalance";


    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext-all.xml");
        ServiceRegistry registry = context.getBean(ServiceRegistry.class);

        registry.registerService(ServiceInstance.<ServicePayLoad>builder()
            .name("demo-service")
            .id("host1")
            .port(8080)
            .address("localhost")
            .build());

        registry.registerService(ServiceInstance.<ServicePayLoad>builder()
            .name("demo-service")
            .id("host2")
            .port(8089)
            .address("localhost")
            .build());

        ServiceInstance<ServicePayLoad> serviceInstance = registry.queryForInstance("demo-service", "host1");

        TimeUnit.SECONDS.sleep(5);
        logger.info(serviceInstance.toString());

        TimeUnit.SECONDS.sleep(8);
        ServiceInstance newServiceInstance = ServiceInstance.<ServicePayLoad>builder()
            .name("demo-service")
            .id("host3")
            .port(8089)
            .address("localhost")
            .build();
        registry.registerService(newServiceInstance);

        TimeUnit.SECONDS.sleep(4);

//        registry.unregisterService(newServiceInstance);


        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);



    }

}
