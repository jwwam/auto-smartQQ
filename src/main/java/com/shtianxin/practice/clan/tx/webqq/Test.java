package com.shtianxin.practice.clan.tx.webqq;


import com.shtianxin.practice.clan.tx.webqq.config.SmartQQConfig;
import com.shtianxin.practice.clan.tx.webqq.model.QQStatus;
import com.shtianxin.practice.clan.tx.webqq.webqq.SmartQQ;

/**
 * Test
 * create at 2017/2/7
 * @author chenclannad@gmail.com
 */
public class Test {

    public static void main(String[] args) throws Exception {
        SmartQQConfig smartQQConfig = new SmartQQConfig();
        smartQQConfig.setQqStatus(QQStatus.HIDDEN);
        smartQQConfig.setImageDir("F:\\smartQQ\\image");
        smartQQConfig.setCookieDir(smartQQConfig.getImageDir());
        /*List<String> sendFriends = new ArrayList<>();
        sendFriends.add("qq号");
        List<String> sendGroupNames = new ArrayList<>();
        sendGroupNames.add("群名称");
        List<String> sendDiscussNames = new ArrayList<>();
        sendDiscussNames.add("讨论组名称");
        smartQQConfig.setSendFriends(sendFriends);
        smartQQConfig.setSendGroupNames(sendGroupNames);
        smartQQConfig.setSendDiscussNames(sendDiscussNames);*/
        new SmartQQ(smartQQConfig).start();
    }
}
