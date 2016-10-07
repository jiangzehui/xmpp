package com.yyquan.jzh.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/1/26.
 */
public class XmppChat implements Serializable {

    private String main;//当前登录的用户名
    private String user;//发送人-用户名
    private String nickname;//发送人-昵称
    private String icon;//发送人-头像
    private int type;//1.发送 2.接收
    private String content;//发送人-消息内容
    private String sex;//发送人-性别
    private String too;//接收人
    private int viewType;//消息类型 1.普通带表情消息 2.图片 3.语音 4.视频
    private long time;//消息时间

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getToo() {
        return too;
    }

    public void setToo(String too) {
        this.too = too;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public XmppChat(String mian, String user, String nickname, String icon, int type, String content, String sex, String too, int viewType, long time) {
        this.main = mian.toUpperCase();
        this.user = user.toUpperCase();
        this.nickname = nickname;
        this.icon = icon;
        this.type = type;
        this.content = content;
        this.sex = sex;
        this.too = too.toUpperCase();
        this.viewType = viewType;
        this.time = time;
    }

    public XmppChat() {
    }
}
