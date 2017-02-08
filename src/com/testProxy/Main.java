package com.testProxy;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        ProService proService = new ProService();
//       proService.testProxcy("94.177.252.34",3128);
        proService.showProxyedIP(proService.getProxyListFromKuaidaili(1), 5000).forEach(proxyInfo -> printf("address:" +
                " " + proxyInfo.getAddress() + "port: " + proxyInfo.getPort()));
//        System.setProperty("https.proxyHost","117.247.195.89");
//        System.setProperty("https.proxyPort","8080");


    }

    public static void printf(String msg) {
        System.out.println(msg);
    }
}
