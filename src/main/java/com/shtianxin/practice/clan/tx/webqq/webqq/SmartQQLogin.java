package com.shtianxin.practice.clan.tx.webqq.webqq;

import com.alibaba.fastjson.JSONObject;
import com.shtianxin.practice.clan.tx.webqq.config.SmartQQConfig;
import com.shtianxin.practice.clan.tx.webqq.utils.HttpTools;
import com.shtianxin.practice.clan.tx.webqq.utils.SmartQQUtils;
import com.shtianxin.practice.clan.tx.webqq.utils.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * SmartQQLogin
 * create at 2017/2/6
 *
 * @author chenclannad@gmail.com
 */
public class SmartQQLogin {

    private final Logger logger = LoggerFactory.getLogger(SmartQQLogin.class);

    private SmartQQConfig smartQQConfig;
    private CloseableHttpClient httpClient;

    public SmartQQLogin(SmartQQConfig smartQQConfig) {
        this.smartQQConfig = smartQQConfig;
        this.httpClient = HttpTools.getHttpClient();
    }

    /**
     * 登录
     *
     * @return
     */
    protected boolean login() {
        this.pagemain();
        this.cgi_login();
        this.getErweima();
        for (; ; ) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.checkLogin();
            if (smartQQConfig.isLoginflag()) {
                break;
            }
        }
        this.getPara();
        smartQQConfig.setPsessionid(this.getPsessionid());
        return true;
    }

    private void pagemain() {
        HttpGet httpget = new HttpGet("http://w.qq.com/");
        httpget.setHeader("User-Agent", SmartQQConfig.User_Agent);
        logger.debug("Executing request {}", httpget.getURI());//开始
        String html = "";
        try {
            CloseableHttpResponse response = httpClient.execute(httpget);
            HttpTools.writeCookie(response, smartQQConfig.getCookies(), smartQQConfig.getNeedCookies());
            HttpEntity entity = response.getEntity();
            html = EntityUtils.toString(entity, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug(html);
    }

    private void cgi_login() {
        HttpGet httpGet = new HttpGet("https://ui.ptlogin2.qq.com/cgi-bin/login?daid=164&target=self&style=16&mibao_css=m_webqq&appid=501004106&enable_qlogin=0&no_verifyimg=1&s_url=http%3A%2F%2Fw.qq.com%2Fproxy.html&f_url=loginerroralert&strong_login=1&login_state=10&t=20131024001");
        httpGet.setHeader("referer", "http://w.qq.com/");
        httpGet.setHeader("upgrade-insecure-requests", "1");
        httpGet.setHeader("user-agent", SmartQQConfig.User_Agent);
        String pgv_info = SmartQQUtils.getPgv_info();
        String pgv_pvid = SmartQQUtils.getPgv_pvid();
        httpGet.setHeader("Cookie", String.format("pgv_info=%s;pgv_pvid=%s", pgv_info, pgv_pvid));
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpTools.writeCookie(response, smartQQConfig.getCookies(), smartQQConfig.getNeedCookies());
            smartQQConfig.getCookies().put("pgv_info", pgv_info);
            smartQQConfig.getCookies().put("pgv_pvid", pgv_pvid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //https://ssl.ptlogin2.qq.com/ptqrshow?appid=501004106&e=0&l=M&s=5&d=72&v=4&t=0.3770239355508238
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void getErweima() {
        HttpGet httpget = new HttpGet("https://ssl.ptlogin2.qq.com/ptqrshow?appid=501004106&e=0&l=M&s=5&d=72&v=4&t=0.3770239355508238");
        logger.debug("获取二维码：Executing request {}", httpget.getURI());//开始
        String html = StringUtils.EMPTY;
        FileOutputStream fos = null;
        InputStream inputStream = null;
        try {
            CloseableHttpResponse response = httpClient.execute(httpget);
            HttpTools.writeCookie(response, smartQQConfig.getCookies(), smartQQConfig.getNeedCookies());
            String qrsig = HttpTools.getCookie("qrsig", response);
            smartQQConfig.setQrsig(qrsig);
            inputStream = response.getEntity().getContent();
            File fileImgDir = new File(this.smartQQConfig.getImageDir());
            if (!fileImgDir.exists()) {
                logger.info("存放二维码目录未找到，创建目录:{}",smartQQConfig.getImageDir());
                fileImgDir.mkdirs();
            }
            fos = new FileOutputStream(new File(fileImgDir, "erweima.jpg"));
            byte[] data = new byte[1024];
            int len;
            while ((len = inputStream.read(data)) != -1) {
                fos.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.debug(html);
    }

    /**
     * 检查是否登录成功
     *
     * @return
     */
    private void checkLogin() {
        HttpGet httpget = new HttpGet("https://ssl.ptlogin2.qq.com/ptqrlogin?ptqrtoken=" + SmartQQUtils.hash3(smartQQConfig.getQrsig()) + "&webqq_type=10&remember_uin=1&login2qq=1&aid=501004106&u1=http%3A%2F%2Fw.qq.com%2Fproxy.html%3Flogin2qq%3D1%26webqq_type%3D10&ptredirect=0&ptlang=2052&daid=164&from_ui=1&pttype=1&dumy=&fp=loginerroralert&action=0-0-120107&mibao_css=m_webqq&t=undefined&g=1&js_type=0&js_ver=10156&login_sig=&pt_randsalt=0");
        logger.debug("检查是否登录：Executing request {}", httpget.getURI());//开始
        String html;
        try {
            CloseableHttpResponse response = httpClient.execute(httpget);
            HttpEntity entity = response.getEntity();
            html = EntityUtils.toString(entity, "utf-8");
            //ptuiCB('0','0','http://ptlogin4.web2.qq.com/check_sig?pttype=1&uin=1069478446&service=ptqrlogin&nodirect=0&ptsigx=afde2a7fe5f26485b976c9f0f0d87c1ebf27706be0d7b9a0fbab5df1d9e5ec9fb1df62d5cdef526fa7e2df2b3ce2dd84fad270fbdfc90bdd4da0308f73a337fd&s_url=http%3A%2F%2Fw.qq.com%2Fproxy.html%3Flogin2qq%3D1%26webqq_type%3D10&f_url=&ptlang=2052&ptredirect=100&aid=501004106&daid=164&j_later=0&low_login_hour=0&regmaster=0&pt_login_type=3&pt_aid=0&pt_aaid=16&pt_light=0&pt_3rd_aid=0','0','登录成功！', 'lonter');
            if (html.contains("登录成功")) {
                smartQQConfig.setLoginflag(true);
                HttpTools.writeCookie(response, smartQQConfig.getCookies(), smartQQConfig.getNeedCookies());
                String uin = HttpTools.getCookie("uin", response);
                smartQQConfig.getCookies().put("o_cookie", uin.replace("o", ""));
                int start = html.indexOf("http:");
                int end = html.indexOf("pt_3rd_aid");
                String ptuiCBurl = html.substring(start, end + 12);
                smartQQConfig.setPtuiCBurl(ptuiCBurl);
                smartQQConfig.setPtwebqq(HttpTools.getCookie("ptwebqq", response));
            } else if (html.contains("二维码已失效")) {
                this.getErweima();
            }
            logger.info(html);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //{"errmsg":"error!!!","retcode":100001}
    public String getPsessionid(String... cookies) {
        HttpPost httppost = new HttpPost("http://d1.web2.qq.com/channel/login2");
        if (cookies != null && cookies.length > 0) {
            httppost.setHeader("Cookie", cookies[0]);
        }
        httppost.setHeader("Origin", "http://d1.web2.qq.com");
        httppost.setHeader("Referer", "http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2");
        httppost.setHeader("User-Agent", SmartQQConfig.User_Agent);
        String result = StringUtils.EMPTY;
        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("r", "{\"ptwebqq\":\"" + smartQQConfig.getPtwebqq() + "\",\"clientid\":53999199,\"psessionid\":\"\",\"status\":\"online\"}"));
        UrlEncodedFormEntity uefEntity;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httppost.setEntity(uefEntity);
            logger.debug("executing request {}", httppost.getURI());
            HttpResponse response = httpClient.execute(httppost);
            HttpTools.writeCookie(response, smartQQConfig.getCookies(), smartQQConfig.getNeedCookies());
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");
            logger.debug(result);
            smartQQConfig.setRetcode(JSONObject.parseObject(result).getIntValue("retcode"));
            int start = result.indexOf("psessionid\":\"") + 13;
            result = result.substring(start);
            int end = result.indexOf("\"");
            result = result.substring(0, end);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获得登录参数
     *
     * @return
     */
    private void getPara() {
        HttpGet httpGet = new HttpGet(smartQQConfig.getPtuiCBurl());
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", SmartQQConfig.User_Agent);
        httpGet.setConfig(RequestConfig.custom().setRedirectsEnabled(false).build());//禁止重定向
        String result;
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpTools.writeCookie(response, smartQQConfig.getCookies(), smartQQConfig.getNeedCookies());
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");
            logger.debug(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
