package com.xxfs.fsapischoolcrawler.manager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * @author zjh
 */
@Component
public class CookiesManager {
  private final Map<String, String> cookies;

  public CookiesManager() {
    this.cookies = new HashMap<>();
  }

  public void setCookies(Map<String, String> cookies) {
    this.cookies.clear();
    this.cookies.putAll(cookies);
  }

  public Map<String, String> getCookies() {
    return Collections.unmodifiableMap(this.cookies);
  }
}
