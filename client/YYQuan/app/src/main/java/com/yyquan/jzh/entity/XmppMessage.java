package com.yyquan.jzh.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/1/10.
 */
public class XmppMessage implements Serializable {
    private int id;
    private String to;
    private String type;
    private XmppUser user;
    private String time;
    private String content;
    private int result;//1.未处理 0.已处理
    private String main;

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public XmppUser getUser() {
        return user;
    }

    public void setUser(XmppUser user) {
        this.user = user;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public XmppMessage(int id, String to, String type, XmppUser user, String time, String content, int result, String main) {
        this.id = id;
        this.to = to;
        this.type = type;
        this.user = user;
        this.time = time;
        this.content = content;
        this.result = result;
        this.main = main;
    }

    public XmppMessage(String to, String type, XmppUser user, String time, String content, int result, String main) {

        this.to = to;
        this.type = type;
        this.user = user;
        this.time = time;
        this.content = content;
        this.result = result;
        this.main = main;
    }

    @Override
    public String toString() {
        return "XmppMessage{" +
                "id=" + id +
                ", to='" + to + '\'' +
                ", type='" + type + '\'' +
                ", user=" + user +
                ", time='" + time + '\'' +
                ", content='" + content + '\'' +
                ", result='" + result + '\'' +
                ", main='" + main + '\'' +
                '}';
    }


}
