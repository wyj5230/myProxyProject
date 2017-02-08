package com.testProxy;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 20160816 on 07-Feb-2017.
 */
public class ProService {


    public List<proxyInfo> getProxyListFromXicidaili() throws IOException {
        Document doc = Jsoup.connect("http://www.xicidaili.com/").userAgent("Mozilla/5.0 (Windows NT 6.2; Win64; x64)" +
                " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36").get();
        Elements httpsProxys = doc.select("tr th h2");
        List<proxyInfo> proxys = new ArrayList<>();
        Element port = null;
        for (Element e : httpsProxys) {
            if (e.text().contains("HTTPS")) {
                port = e.parent().parent().nextElementSibling();
                for (int i = 1; i <= 15; i++) {
                    port = port.nextElementSibling();
                    proxys.add(new proxyInfo(port.select("td").get(1).text(), port.select("td").get(2).text()));
                }
                break;
            }
        }
        return proxys;
    }

    public List<proxyInfo> getProxyListFromKuaidaili(int page) throws IOException {
        printf("Getting proxy list from Kuaidaili.com");
        Document doc = Jsoup.connect("http://www.kuaidaili.com/free/outha/" + page).userAgent("Mozilla/5.0 (Windows " +
                "NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36").get();
        Elements httpsProxys = doc.select("tr");
        httpsProxys.remove(0);
        List<proxyInfo> proxys = new ArrayList<>();
        for (Element e : httpsProxys) {
            proxys.add(new proxyInfo(e.select("td[data-title$=IP]").get(0).text(), e.select("td[data-title$=PORT]")
                    .get(0).text()));
        }
        return proxys;
    }

    public void displayIP(Connection connection) throws IOException {
        Document doc = connection.get();
        Element ipElement = doc.select(".c-gap-right").first().parent();
        printf(ipElement.text());
    }

    public List<proxyInfo> showProxyedIP(List<proxyInfo> proxyList, int timeout) throws IOException {

        String url = "https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&rsv_idx=1&tn=baidu&wd=IP&oq=%25E5%259B%25BD%25E5" +
                "%25A4%2596IP%25E4%25BB%25A3%25E7%2590%2586&rsv_pq=b37b71b700003887&rsv_t" +
                "=32c0kvOd5oIODkSTZSKyovxu7R03%2FNfjR3FtErxD7tWVa09X0fW1ORrC3xc&rqlang=cn&rsv_enter=1&inputT=597" +
                "&rsv_sug3=9&rsv_sug1=5&rsv_sug7=100&rsv_sug2=0&rsv_sug4=597";
        Connection connection = Jsoup.connect(url);
        Connection.Response response = connection.execute();

        int statusCode = response.statusCode();
        if (statusCode == 200) {
            boolean flagPass = false;
            int i = 0;
            int page = 1;
            List<proxyInfo> result = new ArrayList<>();
            do {
                proxyInfo p = new proxyInfo();
                try {
                   p = proxyList.get(i);
                } catch (IndexOutOfBoundsException e) {
                    proxyList = getProxyListFromKuaidaili(page + 1);
                    page++;
                    i = 0;
                    p = proxyList.get(i);
                }
                i++;
                String host = p.getAddress();
                int port = Integer.parseInt(p.getPort());
                printf("Try connnecting: " + host);
                Proxy proxy = new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(host, port));
                connection = Jsoup.connect(url).proxy(proxy).timeout(timeout);
                try {
                    connection.execute();

                } catch (IOException e) {
                    if (e.getClass().equals(SocketException.class)) {
                        printf(e.getMessage());
                        printf("Bad proxy, proceed to next. ");
                        printf("");
                        continue;
                    } else if (e.getClass().equals(SocketTimeoutException.class)) {
                        printf(e.getMessage());
                        printf("Time out: " + timeout + "ms, proceed to next proxy. ");
                        printf("");
                        continue;
                    } else {
                        printf(e.getClass().toString());
                        printf(e.getMessage());
                        printf("");
                        continue;
                    }
                }
                printf("Try connect proxy succeeded.");
                try {
                    this.displayIP(connection);
                } catch (IOException e) {
                    printf("Failure during actual connection, record is aborted.");
                    printf("Fail reason: " + e.getMessage());
                    printf("");
                    continue;
                }
                result.add(p);
                printf("record is added.");
                printf("");
            }
            while (result.size() < 10);
            printf("数据采集完毕，共" + result.size() + "条数据.");
            return result;
        } else return null;
    }

    public void testProxcy(String address, int port) throws IOException {
        String url = "https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&rsv_idx=1&tn=baidu&wd=IP&oq=%25E5%259B%25BD%25E5" +
                "%25A4%2596IP%25E4%25BB%25A3%25E7%2590%2586&rsv_pq=b37b71b700003887&rsv_t" +
                "=32c0kvOd5oIODkSTZSKyovxu7R03%2FNfjR3FtErxD7tWVa09X0fW1ORrC3xc&rqlang=cn&rsv_enter=1&inputT=597" +
                "&rsv_sug3=9&rsv_sug1=5&rsv_sug7=100&rsv_sug2=0&rsv_sug4=597";
        Proxy proxy = new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(address, port));
        Connection connection = Jsoup.connect(url).proxy(proxy);
        Connection.Response response = connection.execute();
        System.out.println(response.statusCode());
        this.displayIP(connection);
    }

    public void printf(String msg) {
        System.out.println(msg);
    }
}
