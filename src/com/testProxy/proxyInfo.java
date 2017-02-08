package com.testProxy;

/**
 * Created by 20160816 on 07-Feb-2017.
 */
public class proxyInfo {
    private String address;

    private String port;

    public proxyInfo() {
        super();
    }

    public proxyInfo(String address, String port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
