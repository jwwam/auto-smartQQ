package com.shtianxin.practice.clan.tx.webqq.utils;

import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HttpTools
 * create at 2017/2/8
 *
 * @author chenclannad@gmail.com
 */
public class HttpTools {

    private static Logger logger = LoggerFactory.getLogger(HttpTools.class);

    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    public static void writeCookie(HttpResponse response, Map<String, String> cookies, List<String> needCookies) {
        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            if ("Set-Cookie".equals(header.getName())) {
                String[] values = header.getValue().split(";");
                for (String value : values) {
                    String[] cookie = value.split("=");
                    if (needCookies.contains(cookie[0]) && //是需要的，并且如果已经设置过但内容是空的
                            (!cookies.containsKey(cookie[0]) || StringUtils.isBlank(cookies.get(cookie[0])))) {
                        if (cookie.length <= 2) {
                            cookies.put(cookie[0], cookie.length == 1 ? "" : cookie[1]);
                        } else {
                            cookies.put(cookie[0], cookie[1].concat("=").concat(cookie[2]));
                        }
                    }
                }

            }
        }
    }

    public static Map<String, String> getCookieMap(String cookieStr) {
        Map<String, String> cookiesMap = new HashMap<>();
        String[] cookies = cookieStr.split(";");
        for (String cookie : cookies) {
            String[] cookieKV = cookie.split("=");
            cookiesMap.put(cookieKV[0], cookieKV.length == 1 ? "" : cookieKV[1]);
        }
        return cookiesMap;
    }

    public static String getCookie(String cookiename, CloseableHttpResponse response) {
        Header[] cookie = response.getAllHeaders();
        for (int i = 0; i < cookie.length; i++) {
            HeaderElement[] he = cookie[i].getElements();
            for (int j = 0; j < he.length; j++) {
                String name = he[j].getName();
                if (cookiename.equals(name))
                    return he[j].getValue();
            }
        }
        return "";
    }

    public static String getaid(String html) {
        int start = html.indexOf("&aid=") + 5;
        html = html.substring(start);
        int end = html.indexOf("&daid");
        String aid = html.substring(0, end);
        System.out.println("aid:" + aid);
        return aid;
    }

    public static String getgetCookieurl(String html) {
        int start = html.indexOf("ptuiCB('0','0','");
        html = html.substring(start) + 16;
        int end = html.indexOf("','");
        String str = html.substring(0, end);
        System.out.println("getCookieurl:" + str);
        return str;
    }

    public static String getgroup_code(String html) {
        int start = html.indexOf("\"],\"from_uin\":") + 14;
        int end = html.indexOf(",\"group_code\":");
        if (end > html.length() || end == -1) {
            return "";
        }
        return html.substring(start, end);

    }

    public static String getmsgtext(String html) {
        int start = html.indexOf("]}],\"") + 5;
        int end = html.indexOf("\"],\"from_uin\":");
        if (end > html.length() || end == -1) {
            return "";
        }
        return html.substring(start, end);
    }


    /**
     * 得到结果
     *
     * @param httpGet httpGet
     * @return result
     */
    public static String getHttpGetResult(HttpGet httpGet) {
        String result = StringUtils.EMPTY;
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 得到post结果
     *
     * @param httpPost httpPost
     * @return result
     */
    public static String getHttpPostResult(HttpPost httpPost, String name, String value) {
        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair(name, value));
        UrlEncodedFormEntity uefEntity;
        String result = StringUtils.EMPTY;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httpPost.setEntity(uefEntity);
            logger.debug("executing request {}", httpPost.getURI());
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public static void setHttpClient(CloseableHttpClient httpClient) {
        HttpTools.httpClient = httpClient;
    }
}
