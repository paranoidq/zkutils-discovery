package me.zkutils.loadbalance.client.sample;

import me.zkutils.loadbalance.ServicePayLoad;
import me.zkutils.loadbalance.client.discovery.ServiceQuery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class SampleClient {

    private static Logger logger = LoggerFactory.getLogger(SampleClient.class);

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext-all.xml");
        ServiceQuery query = context.getBean(ServiceQuery.class);
        ServiceInstance<ServicePayLoad> serviceInstance = null;

        for (int i = 0; i < 1000; i++) {
            serviceInstance = query.getServiceInstance("demo-service");
            logger.info("[" + i + "]获取Service：" + serviceInstance.toString());
            TimeUnit.SECONDS.sleep(5);
        }

        /*
            output:

            14:44:15.636 [main] INFO  me.zkutils.loadbalance.client.sample.SampleClient - 第一次获取service: ServiceInstance{name='demo-service', id='host1', address='localhost', port=8080, sslPort=null, payload=null, registrationTimeUTC=1524551786299, serviceType=DYNAMIC, uriSpec=null, enabled=true}
            14:44:15.638 [main] INFO  me.zkutils.loadbalance.client.sample.SampleClient - 第二次获取service：ServiceInstance{name='demo-service', id='host2', address='localhost', port=8089, sslPort=null, payload=null, registrationTimeUTC=1524551786542, serviceType=DYNAMIC, uriSpec=null, enabled=true}
         */


        Thread.sleep(Integer.MAX_VALUE);
    }

}
