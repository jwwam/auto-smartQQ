package com.shtianxin.practice.clan.tx.webqq.webqq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shtianxin.practice.clan.tx.webqq.config.SmartQQConfig;
import com.shtianxin.practice.clan.tx.webqq.model.*;
import com.shtianxin.practice.clan.tx.webqq.utils.DateUtils;
import com.shtianxin.practice.clan.tx.webqq.utils.HttpTools;
import com.shtianxin.practice.clan.tx.webqq.utils.SmartQQUtils;
import com.shtianxin.practice.clan.tx.webqq.utils.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SmartQQConfig
 * create at 2017/2/6
 *
 * @author chenclannad@gmail.com
 */
public class SmartQQ {

    private Logger logger = LoggerFactory.getLogger(SmartQQ.class);
    private SmartQQConfig smartQQConfig;
    private SelfInfo selfInfo;

    public SmartQQ(SmartQQConfig smartQQConfig) {
        this.smartQQConfig = smartQQConfig;
    }

    /**
     * 开始
     */
    @SuppressWarnings("InfiniteLoopStatement")
    public void start() {
        String cookie = this.readCookie();
        SmartQQLogin smartQQLogin = new SmartQQLogin(smartQQConfig);
        for (; ; ) {
            if (StringUtils.isNotBlank(cookie)) {
                logger.info("检测到cookie,将使用cookie进行登录");
                smartQQConfig.setCookie(cookie);
                smartQQConfig.setWriteCookie(true);
                Map<String, String> cookieMap = HttpTools.getCookieMap(cookie);
                smartQQConfig.setPsessionid(cookieMap.get("psessionid"));
                smartQQConfig.setPtwebqq(cookieMap.get("ptwebqq"));
                smartQQConfig.setPsessionid(smartQQLogin.getPsessionid(cookie));
            } else {
                smartQQLogin.login();
                this.writeCookie();//写cookie
                smartQQConfig.setWriteCookie(false);
            }
            if (smartQQConfig.getRetcode() == 0) {//登录失败
                logger.info("登录成功");
                break;
            }
            logger.error("登录失败，即将使用二维码重新登录!");
            if (smartQQConfig.isWriteCookie()) {//登录失败，清掉cookie
                cookie = null;//清掉cookie
            }
        }
        this.smartQQConfig.setVfwebqq(this.getvfWebQQ());
        this.changeStatus();
        this.getOnlineBuddies();
        selfInfo = JSON.parseObject(this.getSelfInfo()).getObject("result", SelfInfo.class);
        this.smartQQConfig.setHash(SmartQQUtils.hash(selfInfo.getUin(), smartQQConfig.getPtwebqq()));
        Map<String, Friend> friends = this.getFriends();
        Map<String, Group> groups = this.getGroups();
        Map<String, Discuss> discusses = this.getDiscusses();
        for (; ; ) {//接收消息
            //this.replyFriend("","内容");
            List<Message> messages = SmartQQUtils.jsonToMessages(this.getQQMsg());
            for (Message message : messages) {
                String friendTrueQQStr;
                if (Message.MessageType.GROUP_MESSAGE == message.getType()) {
                    Group group = groups.get(message.getFrom_uin());
                    if (group == null) {
                        logger.error("未获取到group,message:{}", message);
                        continue;
                    }
                    friendTrueQQStr = this.getFriendTrueQQ(message.getSend_uin());
                    String friendTrueQQ = "";
                            //JSONObject.parseObject(friendTrueQQStr).getJSONObject("result").getString("account");
                    logger.info("接收到群消息,来自:{},发送人qq:{},发送时间:{},内容:{}", group.getName(), friendTrueQQ, message.getTime(), message.getContent());
                    String reply = "group reply";
                    if (smartQQConfig.getSendGroupNames() != null && smartQQConfig.getSendGroupNames().contains(group.getName())){
                        this.replyGroup(group.getGid(), reply);
                        logger.info("{}回复完毕,回复内容:{}", DateUtils.convert(System.currentTimeMillis()), reply);
                    }
                } else if (Message.MessageType.DISCU_MESSAGE == message.getType()) {
                    Discuss discuss = discusses.get(message.getFrom_uin());
                    if (discuss == null) {
                        logger.error("未获取到discuss,message:{}", message);
                        continue;
                    }
                    friendTrueQQStr = this.getFriendTrueQQ(message.getSend_uin());
                    String friendTrueQQ = "";
                    //JSONObject.parseObject(friendTrueQQStr).getJSONObject("result").getString("account");
                    logger.info("接收到讨论组消息,来自:{},发送人qq:{},发送时间:{},内容:{}", discuss.getName(), friendTrueQQ, message.getTime(), message.getContent());
                    String reply = "discuss reply";
                    if (smartQQConfig.getSendDiscussNames() != null && smartQQConfig.getSendDiscussNames().contains(discuss.getName())){
                        this.replyDiscuss(discuss.getDid(), reply);
                        logger.info("{}回复完毕,回复内容:{}", DateUtils.convert(System.currentTimeMillis()), reply);
                    }
                } else if (Message.MessageType.MESSAGE == message.getType()) {
                    Friend friend = friends.get(message.getFrom_uin());
                    if (friend == null) {
                        logger.error("未获取到friend,message:{}", message);
                        continue;
                    }
                    logger.info("接收到消息,来自qq:{},昵称:{},备注为:{},所在组:{},发送时间:{},内容:{}", friend.getTrueqq(), friend.getNick(), friend.getMarkname() == null ? "暂无" : friend.getMarkname(), friend.getCategory(), message.getTime(), message.getContent());
                    String reply = "测试回复xxxxx";
                    if (smartQQConfig.getSendFriends() != null && smartQQConfig.getSendFriends().contains(friend.getTrueqq())){
                        this.replyFriend(friend.getUin(),reply);
                    }
                    logger.info("{}回复完毕,回复内容:{}", DateUtils.convert(System.currentTimeMillis()), reply);
                }
            }
        }
    }

    /**
     * 写cookie
     */
    private void writeCookie() {
        Map<String, String> cookies = smartQQConfig.getCookies();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
        }
        try {
            FileWriter writer = new FileWriter(new File(smartQQConfig.getCookieDir(),smartQQConfig.getCookieTxtName()));
            writer.write(sb.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 读cookie
     * @return cookie
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private String readCookie() {
        StringBuilder sb = new StringBuilder();
        File cookieDir = new File(smartQQConfig.getCookieDir());
        if (!cookieDir.exists()){
            logger.info("cookie 存放目录未找到，开始创建目录:{}",smartQQConfig.getCookieDir());
            cookieDir.mkdirs();
        }
        File cookieTxtFile = new File(cookieDir, smartQQConfig.getCookieTxtName());
        try {
            if (!cookieTxtFile.exists()){
                logger.info("cookie存放文件名{}未找到，创建文件",smartQQConfig.getCookieTxtName());
                cookieTxtFile.createNewFile();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(cookieTxtFile)));
            String temp;
            while ((temp = reader.readLine()) != null) {
                sb.append(temp);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 得到好友
     */
    private Map<String, Friend> getFriends() {
        Map<String, Friend> friends = new HashMap<>();
        String userFriendsJsonStr = getUserFriends();
        if (logger.isDebugEnabled()) {
            logger.debug(userFriendsJsonStr);
        }
        JSONObject userFriendsJsonObject = JSON.parseObject(userFriendsJsonStr).getJSONObject("result");
        logger.info("您共有好友数量:{}", userFriendsJsonObject.getJSONArray("friends").size());
        JSONArray userFriends_friends = userFriendsJsonObject.getJSONArray("friends");
        JSONArray userFriends_marknames = userFriendsJsonObject.getJSONArray("marknames");//备注
        JSONArray userFriends_categories = userFriendsJsonObject.getJSONArray("categories"); //组
        JSONArray userFriends_info = userFriendsJsonObject.getJSONArray("info");
        Map<String, String> userFriends_categoriesMap = new HashMap<>();//组
        Map<String, String> marknames = new HashMap<>();//备注
        for (int i = 0; i < userFriends_categories.size(); i++) {
            JSONObject category_Json = userFriends_categories.getJSONObject(i);
            userFriends_categoriesMap.put(category_Json.getString("index"), category_Json.getString("name"));
        }
        for (int i = 0; i < userFriends_marknames.size(); i++) {
            JSONObject category_Json = userFriends_marknames.getJSONObject(i);
            marknames.put(category_Json.getString("uin"), category_Json.getString("markname"));
        }
        //填充friend
        for (int i = 0; i < userFriends_friends.size(); i++) {
            JSONObject friendJson = userFriends_friends.getJSONObject(i);
            JSONObject infoJson = userFriends_info.getJSONObject(i);
            Friend myfriend = new Friend();
            myfriend.setUin(friendJson.getString("uin"));
            String friendTrueQQJson = this.getFriendTrueQQ(myfriend.getUin());
            //myfriend.setTrueqq(JSONObject.parseObject(friendTrueQQJson).getJSONObject("result").getString("account"));
            myfriend.setTrueqq("");
            myfriend.setCategory(userFriends_categoriesMap.get(friendJson.getString("categories")));
            myfriend.setFace(infoJson.getString("face"));
            myfriend.setMarkname(marknames.get(myfriend.getUin()));
            myfriend.setNick(infoJson.getString("nick"));
            friends.put(myfriend.getUin(), myfriend);
        }
        userFriends_categoriesMap.clear();
        marknames.clear();//清理
        return friends;
    }

    /**
     * 得到群组
     */
    private Map<String, Group> getGroups() {
        Map<String, Group> groups = new HashMap<>();
        String groupListJsonStr = getGroupList();
        if (logger.isDebugEnabled()) {
            logger.debug(groupListJsonStr);
        }
        JSONArray groupsJsonArray = JSON.parseObject(groupListJsonStr).getJSONObject("result").getJSONArray("gnamelist");
        logger.info("群组数量:{}", groupsJsonArray.size());
        for (int i = 0; i < groupsJsonArray.size(); i++) {
            JSONObject groupsJsonGroup = groupsJsonArray.getJSONObject(i);
            Group group = new Group();
            group.setGid(groupsJsonGroup.getString("gid"));
            group.setName(groupsJsonGroup.getString("name"));
            group.setCode(groupsJsonGroup.getString("code"));
            groups.put(group.getGid(), group);
        }
        return groups;
    }

    /**
     * 得到自身信息
     *
     * @return 自身信息
     */
    private String getSelfInfo() {
        HttpGet httpGet = new HttpGet(String.format("http://s.web2.qq.com/api/get_self_info2?t=%s", System.currentTimeMillis()));
        httpGet.setHeader("Pragma", "no-cache");
        this.setRequestCookie(httpGet);
        httpGet.setHeader("Referer", "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1");
        httpGet.setHeader("User-Agent", SmartQQConfig.User_Agent);
        return HttpTools.getHttpGetResult(httpGet);
    }

    /**
     * 得到真实qq
     *
     * @param uin uin
     * @return 真实qq
     */
    private String getFriendTrueQQ(String uin) {
        //                                          http://s.web2.qq.com/api/get_friend_uin2?tuid=\#{uin}&type=1&vfwebqq=\#{vfwebqq}&t=0.1
        HttpGet httpGet = new HttpGet(String.format("http://s.web2.qq.com/api/get_friend_uin2?tuin=%s&type=1&vfwebqq=%s&t=0.1", uin, smartQQConfig.getVfwebqq()));
        httpGet.setHeader("Pragma", "no-cache");
        this.setRequestCookie(httpGet);
        httpGet.setHeader("Referer", "http://s.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2");
        httpGet.setHeader("User-Agent", SmartQQConfig.User_Agent);
        return HttpTools.getHttpGetResult(httpGet);
    }

    /**
     * 得到讨论组
     *
     * @return result
     */
    private Map<String, Discuss> getDiscusses() {
        Map<String, Discuss> discusses = new HashMap<>();
        String discussListJsonStr = getDiscussList();
        if (logger.isDebugEnabled()) {
            logger.debug(discussListJsonStr);
        }
        JSONArray discussJsonArray = JSON.parseObject(discussListJsonStr).getJSONObject("result").getJSONArray("dnamelist");
        logger.info("讨论组数量:{}", discussJsonArray.size());
        for (int i = 0; i < discussJsonArray.size(); i++) {
            JSONObject discussJsonDiscuss = discussJsonArray.getJSONObject(i);
            Discuss discuss = new Discuss();
            discuss.setDid(discussJsonDiscuss.getString("did"));
            discuss.setName(discussJsonDiscuss.getString("name"));
            discusses.put(discuss.getDid(), discuss);
        }
        return discusses;
    }

    /**
     * 获取当前在线状态
     *
     * @return result
     */
    private String getOnlineBuddies() {
        HttpGet httpGet = new HttpGet(String.format("http://d1.web2.qq.com/channel/get_online_buddies2?newstatus=hidden&clientid=53999199&psessionid=%s&t=%s", smartQQConfig.getPsessionid(), System.currentTimeMillis()));
        httpGet.setHeader("Referer", "http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2");
        httpGet.setHeader("User-Agent", SmartQQConfig.User_Agent);
        this.setRequestCookie(httpGet);
        return HttpTools.getHttpGetResult(httpGet);
    }

    //http://d1.web2.qq.com/channel/change_status2?newstatus=hidden&clientid=53999199&psessionid=8368046764001d636f6e6e7365727665725f77656271714031302e3133332e34312e383400001ad00000066b026e040015808a206d0000000a406172314338344a69526d0000002859185d94e66218548d1ecb1a12513c86126b3afb97a3c2955b1070324790733ddb059ab166de6857&t=1486398058963

    /**
     * 修改状态
     * 无法修改 即使使用浏览器修改状态也会返回error tx api问题
     */
    private void changeStatus() {
        if (smartQQConfig.getQqStatus() != QQStatus.ONLINE){
            HttpGet httpGet = new HttpGet(String.format("http://d1.web2.qq.com/channel/change_status2?newstatus=%s&clientid=53999199&psessionid=%s&t=%s", smartQQConfig.getQqStatus().name().toLowerCase(), smartQQConfig.getPsessionid(), System.currentTimeMillis()));
            httpGet.setHeader("Referer", "http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2");
            httpGet.setHeader("User-Agent", SmartQQConfig.User_Agent);
            this.setRequestCookie(httpGet);
            HttpTools.getHttpGetResult(httpGet);
        }
    }

    /**
     * 得到好友等所必需参数
     */
    private String getvfWebQQ() {
        HttpGet httpGet = new HttpGet(String.format("http://s.web2.qq.com/api/getvfwebqq?ptwebqq=%s&clientid=53999199&psessionid=&t=1486475657347", smartQQConfig.getPtwebqq()));
        httpGet.setHeader("Pragma", "no-cache");
        httpGet.setHeader("Referer", "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1");
        httpGet.setHeader("User-Agent", SmartQQConfig.User_Agent);
        this.setRequestCookie(httpGet);
        String vfwebqqJson = HttpTools.getHttpGetResult(httpGet);
        JSONObject jsonObject = JSON.parseObject(vfwebqqJson);
        return jsonObject.getJSONObject("result").get("vfwebqq").toString();
    }

    /**
     * 得到好友
     *
     * @return result
     */
    private String getUserFriends() {
        HttpPost httpPost = new HttpPost("http://s.web2.qq.com/api/get_user_friends2");
        httpPost.setHeader("Origin", "http://s.web2.qq.com");
        httpPost.setHeader("Referer", "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1");
        httpPost.setHeader("User-Agent", SmartQQConfig.User_Agent);
        this.setRequestCookie(httpPost);
        return HttpTools.getHttpPostResult(httpPost, "r", "{\"vfwebqq\":\"" + smartQQConfig.getVfwebqq() + "\",\"hash\":\"" + smartQQConfig.getHash() + "\"}");
    }

    /**
     * 获取群列表
     */
    private String getGroupList() {
        HttpPost httpPost = new HttpPost("http://s.web2.qq.com/api/get_group_name_list_mask2");
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setHeader("Origin", "http://s.web2.qq.com");
        httpPost.setHeader("Referer", "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1");
        this.setRequestCookie(httpPost);
        httpPost.setHeader("User-Agent", SmartQQConfig.User_Agent);
        return HttpTools.getHttpPostResult(httpPost, "r", "{\"vfwebqq\":\"" + smartQQConfig.getVfwebqq() + "\",\"hash\":\"" + smartQQConfig.getHash() + "\"}");
    }

    /**
     * 获得讨论组
     */
    private String getDiscussList() {
        HttpGet httpGet = new HttpGet(String.format("http://s.web2.qq.com/api/get_discus_list?clientid=53999199&psessionid=%s&vfwebqq=%s&t=1486478435789", smartQQConfig.getPsessionid(), smartQQConfig.getVfwebqq()));
        httpGet.setHeader("Referer", "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1");
        httpGet.setHeader("User-Agent", SmartQQConfig.User_Agent);
        this.setRequestCookie(httpGet);
        return HttpTools.getHttpGetResult(httpGet);
    }

    /**
     * 获取QQ消息
     */
    private String getQQMsg() {
        HttpPost httpPost = new HttpPost("https://d1.web2.qq.com/channel/poll2");
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setHeader("Origin", "https://d1.web2.qq.com");
        httpPost.setHeader("Referer", "https://d1.web2.qq.com/cfproxy.html?v=20151105001&callback=1");
        this.setRequestCookie(httpPost);
        httpPost.setHeader("pragma", "no-cache");
        httpPost.setHeader("User-Agent", SmartQQConfig.User_Agent);
        return HttpTools.getHttpPostResult(httpPost, "r", "{\"ptwebqq\":\"" + smartQQConfig.getPtwebqq() + "\",\"clientid\":53999199,\"psessionid\":\"" + smartQQConfig.getPsessionid() + "\",\"key\":\"\"}");
    }

    /**
     * 回复好友
     *
     * @param toUin   接收者
     * @param content 发送内容
     */
    private void replyFriend(String toUin, String content) {
        HttpPost httpPost = getReplyHttpPost("https://d1.web2.qq.com/channel/send_buddy_msg2");
        HttpTools.getHttpPostResult(httpPost, "r", String.format("{\"to\":%s,\"content\":\"[\\\"%s\\\",[\\\"font\\\",{\\\"name\\\":\\\"宋体\\\",\\\"size\\\":10,\\\"style\\\":[0,0,0],\\\"color\\\":\\\"000000\\\"}]]\",\"face\":%s,\"clientid\":53999199,\"msg_id\":%s,\"psessionid\":\"%s\"}", toUin, content, selfInfo.getFace(), SmartQQUtils.getMsgId(), smartQQConfig.getPsessionid()));
    }

    /**
     * 回复群
     *
     * @param to_group_uin 群组uin
     * @param content      内容
     */
    private void replyGroup(String to_group_uin, String content) {
        HttpPost httpPost = getReplyHttpPost("https://d1.web2.qq.com/channel/send_qun_msg2");
        HttpTools.getHttpPostResult(httpPost, "r", String.format("{\"group_uin\":%s,\"content\":\"[\\\"%s\\\",[\\\"font\\\",{\\\"name\\\":\\\"宋体\\\",\\\"size\\\":10,\\\"style\\\":[0,0,0],\\\"color\\\":\\\"000000\\\"}]]\",\"face\":%s,\"clientid\":53999199,\"msg_id\":%s,\"psessionid\":\"%s\"}", to_group_uin, content, selfInfo.getFace(), SmartQQUtils.getMsgId(), smartQQConfig.getPsessionid()));
    }

    /**
     * 回复讨论组
     *
     * @param to_discuss_did 讨论组did
     * @param content        内容
     */
    private void replyDiscuss(String to_discuss_did, String content) {
        HttpPost httpPost = getReplyHttpPost("https://d1.web2.qq.com/channel/send_discu_msg2");
        HttpTools.getHttpPostResult(httpPost, "r", String.format("{\"did\":%s,\"content\":\"[\\\"%s\\\",[\\\"font\\\",{\\\"name\\\":\\\"宋体\\\",\\\"size\\\":10,\\\"style\\\":[0,0,0],\\\"color\\\":\\\"000000\\\"}]]\",\"face\":%s,\"clientid\":53999199,\"msg_id\":%s,\"psessionid\":\"%s\"}", to_discuss_did, content, selfInfo.getFace(), SmartQQUtils.getMsgId(), smartQQConfig.getPsessionid()));
    }

    /**
     * 得到httpPost
     *
     * @param requestUrl 请求url
     * @return HttpPost
     */
    private HttpPost getReplyHttpPost(String requestUrl) {
        HttpPost httpPost = new HttpPost(requestUrl);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setHeader("Origin", "https://d1.web2.qq.com");
        httpPost.setHeader("Referer", "https://d1.web2.qq.com/cfproxy.html?v=20151105001&callback=1");
        httpPost.setHeader("pragma", "no-cache");
        this.setRequestCookie(httpPost);
        httpPost.setHeader("User-Agent", SmartQQConfig.User_Agent);
        return httpPost;
    }

    /**
     * 设置cookie
     *
     * @param requestBase requestBase
     */
    private void setRequestCookie(HttpRequestBase requestBase) {
        if (smartQQConfig.isWriteCookie()) {
            requestBase.setHeader("Cookie", smartQQConfig.getCookie());
        }
    }

}
