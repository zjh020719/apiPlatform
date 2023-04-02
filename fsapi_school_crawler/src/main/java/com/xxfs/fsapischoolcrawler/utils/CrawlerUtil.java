package com.xxfs.fsapischoolcrawler.utils;

import com.xxfs.fsapischoolcrawler.manager.CookiesManager;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;


/**
 * @author zjh
 */
@Component
public class CrawlerUtil {
    @Autowired
    private CookiesManager cookiesManager;

    public Document getDoc(String url) throws IOException {
        return Jsoup.connect(url)
                .cookies(cookiesManager.getCookies())
                .timeout(10000)
                .method(Connection.Method.GET)
                .execute()
                .parse();
    }

    public Document getDoc(String url, Map<String, String> data, String method) throws IOException {
        return Jsoup.connect(url)
                .cookies(cookiesManager.getCookies())
                .timeout(10000)
                .method("post".equals(method) ? Connection.Method.GET : Connection.Method.POST)
                .data(data)
                .execute()
                .parse();
    }
}
