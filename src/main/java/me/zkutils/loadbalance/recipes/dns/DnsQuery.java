package me.zkutils.loadbalance.recipes.dns;

import me.zkutils.loadbalance.consumer.discovery.ServiceQuery;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author paranoidq
 * @since 1.0.0
 */

public final class DnsQuery {

    private ServiceQuery query;

    public DnsQuery(ServiceQuery query) {
        this.query = query;
    }

}
