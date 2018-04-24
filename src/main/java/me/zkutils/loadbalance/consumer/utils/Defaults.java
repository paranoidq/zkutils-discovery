package me.zkutils.loadbalance.consumer.utils;

import me.zkutils.loadbalance.ServicePayLoad;
import org.apache.curator.x.discovery.ProviderStrategy;
import org.apache.curator.x.discovery.strategies.RandomStrategy;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public interface Defaults {

    int CONNECT_TIMEOUT_MS = 2000;
    int SESSION_TIMEOUT_MS = 2000;
    int  MAX_CONNECT_RETRIES= 3;
}
