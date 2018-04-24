package me.zkutils.loadbalance;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ServicePayLoad {


    public ServicePayLoad() {

    }


    public ServicePayLoad(String appName, String serviceName, String domain, String ip, String port) {
    }


    public ServicePayLoad(String appName, String serviceName, String domain, String port) {
        this(appName, serviceName, domain, "localhost", port);
    }

}
