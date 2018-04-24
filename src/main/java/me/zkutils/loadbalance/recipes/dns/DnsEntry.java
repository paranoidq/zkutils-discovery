package me.zkutils.loadbalance.recipes.dns;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class DnsEntry {

    private String host;
    private int port;

    public DnsEntry(String host, int port) {
        this.host = host;
        this.port = port;
    }


    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "DnsEntry{" +
            "host='" + host + '\'' +
            ", port=" + port +
            '}';
    }
}
