package com.shtianxin.practice.clan.tx.webqq.model;

/**
 * Friend
 * create at 2017/2/7
 * @author chenclannad@gmail.com
 */
public class Friend {

    /**
     * 用户唯一标识
     */
    private String uin;

    /**
     * 真实qq
     */
    private String trueqq;

    /**
     * 备注
     */
    private String markname;

    /**
     * 所在组名称
     */
    private String category;

    /**
     * 头像
     */
    private String face;

    /**
     * 昵称
     */
    private String nick;

    public String getUin() {
        return uin;
    }

    public void setUin(String uin) {
        this.uin = uin;
    }

    public String getTrueqq() {
        return trueqq;
    }

    public void setTrueqq(String trueqq) {
        this.trueqq = trueqq;
    }

    public String getMarkname() {
        return markname;
    }

    public void setMarkname(String markname) {
        this.markname = markname;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
