package com.deals.jeetodeals.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class MyCookieJar implements CookieJar {

    private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cookieStore.put(url, cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = cookieStore.get(url);
        return cookies != null ? cookies : new ArrayList<>();
    }

    public List<Cookie> getAllCookies() {
        List<Cookie> allCookies = new ArrayList<>();
        for (List<Cookie> cookies : cookieStore.values()) {
            allCookies.addAll(cookies);
        }
        return allCookies;
    }
}

