package com.shtianxin.practice.clan.tx.webqq.config;

import com.shtianxin.practice.clan.tx.webqq.model.QQStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SmartQQConfig
 * create at 2017/2/6
 * @author chenclannad@gmail.com
 */
public class SmartQQConfig {

    private boolean loginflag;
    private String ptuiCBurl;
    private String ptwebqq;
    private QQStatus qqStatus;//qq状态
    private String psessionid;
    private String imageDir;//二维码目录
    private String cookieDir;//cookie存放目录
    private String cookieTxtName;//cookie文件名
    private String vfwebqq;
    private String hash;
    private String qrsig;
    private boolean writeCookie;
    private String cookie;
    private int retcode;
    private Map<String,String> cookies;
    private List<String> needCookies;
    public static final String User_Agent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.84 Safari/537.36";

    private List<String> sendFriends;//发送给哪些人 qq号
    private List<String> sendGroupNames;//发送给哪些群组 群名称
    private List<String> sendDiscussNames;//发送给哪些讨论组 讨论组名称

    public SmartQQConfig() {
        imageDir = System.getProperty("java.io.tmpdir");
        cookieDir = imageDir;
        cookieTxtName = "smartQQ_Cookie.txt";
        qqStatus = QQStatus.ONLINE;
        cookies = new HashMap<>();
        needCookies = new ArrayList<>();
        needCookies.add("RK");
        needCookies.add("ptisp");
        needCookies.add("pt2gguin");
        needCookies.add("uin");
        needCookies.add("skey");
        needCookies.add("p_uin");
        needCookies.add("p_skey");
        needCookies.add("pt4_token");
        needCookies.add("pgv_info");
        needCookies.add("pgv_pvid");
        needCookies.add("o_cookie");
        needCookies.add("ptwebqq");
    }

    public int getRetcode() {
        return retcode;
    }

    public void setRetcode(int retcode) {
        this.retcode = retcode;
    }

    public boolean isLoginflag() {
        return loginflag;
    }

    public void setLoginflag(boolean loginflag) {
        this.loginflag = loginflag;
    }

    public String getPtuiCBurl() {
        return ptuiCBurl;
    }

    public void setPtuiCBurl(String ptuiCBurl) {
        this.ptuiCBurl = ptuiCBurl;
    }

    public String getPtwebqq() {
        return ptwebqq;
    }

    public void setPtwebqq(String ptwebqq) {
        this.ptwebqq = ptwebqq;
    }

    public String getPsessionid() {
        return psessionid;
    }

    public void setPsessionid(String psessionid) {
        this.psessionid = psessionid;
    }

    public String getImageDir() {
        return imageDir;
    }

    public void setImageDir(String imageDir) {
        this.imageDir = imageDir;
    }

    public String getVfwebqq() {
        return vfwebqq;
    }

    public void setVfwebqq(String vfwebqq) {
        this.vfwebqq = vfwebqq;
    }

    public QQStatus getQqStatus() {
        return qqStatus;
    }

    public void setQqStatus(QQStatus qqStatus) {
        this.qqStatus = qqStatus;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getQrsig() {
        return qrsig;
    }

    public void setQrsig(String qrsig) {
        this.qrsig = qrsig;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public boolean isWriteCookie() {
        return writeCookie;
    }

    public void setWriteCookie(boolean writeCookie) {
        this.writeCookie = writeCookie;
    }

    public List<String> getNeedCookies() {
        return needCookies;
    }

    public void setNeedCookies(List<String> needCookies) {
        this.needCookies = needCookies;
    }

    public String getCookieDir() {
        return cookieDir;
    }

    public void setCookieDir(String cookieDir) {
        this.cookieDir = cookieDir;
    }

    public String getCookieTxtName() {
        return cookieTxtName;
    }

    public void setCookieTxtName(String cookieTxtName) {
        this.cookieTxtName = cookieTxtName;
    }

    public List<String> getSendFriends() {
        return sendFriends;
    }

    public void setSendFriends(List<String> sendFriends) {
        this.sendFriends = sendFriends;
    }

    public List<String> getSendGroupNames() {
        return sendGroupNames;
    }

    public void setSendGroupNames(List<String> sendGroupNames) {
        this.sendGroupNames = sendGroupNames;
    }

    public List<String> getSendDiscussNames() {
        return sendDiscussNames;
    }

    public void setSendDiscussNames(List<String> sendDiscussNames) {
        this.sendDiscussNames = sendDiscussNames;
    }
}
