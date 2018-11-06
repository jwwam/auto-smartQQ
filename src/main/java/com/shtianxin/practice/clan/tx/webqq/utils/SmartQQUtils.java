package com.shtianxin.practice.clan.tx.webqq.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shtianxin.practice.clan.tx.webqq.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * SmartQQUtils
 * create at 2017/2/8
 *
 * @author chenclannad@gmail.com
 */
public class SmartQQUtils {

    private static Logger logger = LoggerFactory.getLogger(SmartQQUtils.class);

    //用于生成msgid
    private static int sequence = 0;
    private static long t = new Date().getTime();
    static {
        t = (t - t % 1000L) / 1000L;
        t = t % 10000L * 10000L;
    }

    //获取msgId
    public static long getMsgId() {
        sequence++;
        return t + sequence;
    }

    /**
     * 得到pgv_pvid
     * @return pgv_pvid
     */
    public static String getPgv_pvid(){
        long result = (long) (Math.round(Math.abs(Math.random() - 1) * 2147483647) * getUTCMilliseconds() % 1e10);
        return String.valueOf(result);
    }

    /**
     * 得到pgv_info
     * @return pgv_info
     */
    public static String getPgv_info(){
        return "ssid=s" + getPgv_pvid();
    }

    private static int getUTCMilliseconds(){
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
        int dstOffset = calendar.get(Calendar.DST_OFFSET);
        calendar.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        return calendar.get(Calendar.MILLISECOND);
    }

    /**
     * 得到hash
     *
     * @return hash
     */
    public static String hash(int uin, String ptvfwebqq) {
        int[] ptb = new int[4];
        for (int i = 0; i < ptvfwebqq.length(); i++) {
            int ptbIndex = i % 4;
            ptb[ptbIndex] ^= ptvfwebqq.charAt(i);
        }
        String[] salt = {"EC", "OK"};
        int[] uinByte = new int[4];
        uinByte[0] = (((uin >> 24) & 0xFF) ^ salt[0].charAt(0));
        uinByte[1] = (((uin >> 16) & 0xFF) ^ salt[0].charAt(1));
        uinByte[2] = (((uin >> 8) & 0xFF) ^ salt[1].charAt(0));
        uinByte[3] = ((uin & 0xFF) ^ salt[1].charAt(1));
        int[] result = new int[8];
        for (int i = 0; i < 8; i++) {
            if (i % 2 == 0)
                result[i] = ptb[i >> 1];
            else
                result[i] = uinByte[i >> 1];
        }
        return byte2hex(result);
    }

    private static String byte2hex(int[] result) {
        char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        String buf = "";

        for (int aResult : result) {
            buf += (hex[(aResult >> 4) & 0xF]);
            buf += (hex[aResult & 0xF]);
        }
        return buf;
    }

    public static int hash3(String qrsig) {
        int e=0;
        for (int i = 0,n=qrsig.length(); n > i; ++i) {
            e += (e <<5) + qrsig.charAt(i);
        }
        return 2147483647 & e;
    }

    /**
     * 将json格式字符串转为message
     *
     * @return Message
     */
    public static List<Message> jsonToMessages(String json) {
        List<Message> messages = new ArrayList<>();
        JSONObject jsonObject = null;
        try {
            jsonObject = JSON.parseObject(json);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        if (jsonObject == null) {
            return messages;
        }
        JSONArray jsonArrayResult = jsonObject.getJSONArray("result");
        if (jsonArrayResult != null) {
            for (int i = 0; i < jsonArrayResult.size(); i++) {
                JSONObject jsonResult = jsonArrayResult.getJSONObject(i);
                Message message = new Message();
                message.setType(Message.MessageType.valueOf(jsonResult.getString("poll_type").toUpperCase()));
                JSONObject jsonValue = jsonResult.getJSONObject("value");
                JSONArray contentArray = jsonValue.getJSONArray("content");
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < contentArray.size(); j++) {//第一个为字体，之后才是正文
                    if (contentArray.get(j).getClass().isAssignableFrom(JSONArray.class)) {//如果是数组
                        JSONArray jsonFirstArray = contentArray.getJSONArray(j);
                        String mainTxt = jsonFirstArray.getString(1);
                        switch (jsonFirstArray.getString(0)) {
                            case "font"://字体, 暂时不处理
                                break;
                            case "face"://表情
                                break;
                            case "cface"://自定义表情
                                break;
                            case "offpic"://
                                break;
                            case "sendfile"://发送文件给对方
                                sb.append("您发送文件").append(mainTxt).append("给对方。");
                                break;
                            case "transtimeout"://传输超时
                                sb.append("接收文件").append(mainTxt).append("超时，文件传输失败。");
                                break;
                            case "refusedbyclient":
                                sb.append("对方取消了接收文件").append(mainTxt).append("，文件传输失败。");
                                break;
                            case "transok":
                                sb.append("文件").append(mainTxt).append("传输成功。");
                                break;
                            case "transerror":
                                sb.append("对方取消了接收文件").append(mainTxt).append("或传输错误，文件传输失败。");
                                break;
                            case "rffile":
                                sb.append("对方取消了接收文件").append(mainTxt).append("，文件传输失败。");
                                break;
                            case "agfile":
                                sb.append("您同意了接收文件").append(mainTxt).append("。");
                                break;
                            case "rtfile":
                                sb.append("接收文件").append(mainTxt).append("超时，文件传输失败。");
                                break;
                            case "wrfile":
                                sb.append("对方已同意接收").append(mainTxt).append("，开始传输文件。");
                                break;
                            case "wrffile":
                                sb.append("对方拒绝了接收文件").append(mainTxt).append("，文件传输失败。");
                                break;
                            case "rfile"://暂不接收文件
                                break;
                            case "offfile":
                                break;
                            case "sendofffile":
                            case "sendofffileerror":
                            case "refuseofffile":
                            case "nextofffile":
                            case "canceloffupload":
                            case "notifyagreeofffile":
                            case "notifyrefuseofffile": // 离线文件上传成功提示
                                sb.append(mainTxt);
                                break;
                            default:
                                sb.append(mainTxt);
                                break;
                        }
                    } else {
                        sb.append(contentArray.getString(j));
                    }
                }
                message.setContent(sb.toString());
                message.setFrom_uin(jsonValue.getString("from_uin"));
                message.setGroup_code(jsonValue.getString("group_code"));
                message.setMsg_type(jsonValue.getString("msg_type"));
                message.setSend_uin(jsonValue.getString("send_uin"));
                message.setTime(DateUtils.convert(jsonValue.getLongValue("time") * 1000L));
                message.setTo_uin(jsonValue.getString("to_uin"));
                messages.add(message);
            }
        } else {
            logger.error("接受消息错误,消息:{},错误代码:{}", jsonObject.getString("errmsg"), jsonObject.getString("retcode"));
        }
        return messages;
    }

    public static void main(String[] args) {
        System.out.println(SmartQQUtils.hash(1727691203,"039d2d51934bbaef2208e62a57042b32a86c00612598d15bcdc17cb8c12d1c7848a5a136f19ca706"));
        System.out.println(getPgv_pvid());
    }
}
