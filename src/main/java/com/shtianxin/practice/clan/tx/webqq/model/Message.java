package com.shtianxin.practice.clan.tx.webqq.model;

/**
 * Message
 * create at 2017/2/8
 * @author chenclannad@gmail.com
 */
public class Message {

    /**
     * 消息类型
     */
    public enum MessageType {
        GROUP_MESSAGE("群消息"),
        DISCU_MESSAGE("讨论组消息"),
        MESSAGE("个人消息"),
        KICK_MESSAGE("被踢线了"),
        SESS_MESSAGE("陌生人消息"),
        SYSTEM_MESSAGE("系统消息"),
        FILESRV_TRANSFER("文件传输消息"),
        FILE_MESSAGE("文件信道通知"),
        PUSH_OFFFILE("离线文件消息"),
        NOTIFY_OFFFILE("对方拒绝或成功接收离线文件通知消息");

        private String description;

        MessageType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    private MessageType type;

    /**
     * 内容
     */
    private String content;

    /**
     * 消息来源
     */
    private String from_uin;

    /**
     * 群组code
     */
    private String group_code;

    /**
     * 消息类型code
     */
    private String msg_type;

    /**
     * 发送来源
     */
    private String send_uin;

    /**
     * 时间
     */
    private String time;

    /**
     * 接收人
     */
    private String to_uin;

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFrom_uin() {
        return from_uin;
    }

    public void setFrom_uin(String from_uin) {
        this.from_uin = from_uin;
    }

    public String getGroup_code() {
        return group_code;
    }

    public void setGroup_code(String group_code) {
        this.group_code = group_code;
    }

    public String getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(String msg_type) {
        this.msg_type = msg_type;
    }

    public String getSend_uin() {
        return send_uin;
    }

    public void setSend_uin(String send_uin) {
        this.send_uin = send_uin;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTo_uin() {
        return to_uin;
    }

    public void setTo_uin(String to_uin) {
        this.to_uin = to_uin;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", content='" + content + '\'' +
                ", from_uin='" + from_uin + '\'' +
                ", group_code='" + group_code + '\'' +
                ", msg_type='" + msg_type + '\'' +
                ", send_uin='" + send_uin + '\'' +
                ", time='" + time + '\'' +
                ", to_uin='" + to_uin + '\'' +
                '}';
    }
}
