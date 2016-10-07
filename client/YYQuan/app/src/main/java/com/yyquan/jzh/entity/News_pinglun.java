package com.yyquan.jzh.entity;

import java.io.Serializable;

/**
 * Created by jzh on 2015/9/28.
 */
public class News_pinglun implements Serializable {
    private int pid;
    private int pcid;
    private User user;
    private String plocation;
    private String ptime;
    private String pcontent;
    private String pzan;
    private String ispzan;
    News_luntan news_luntan;


    public News_pinglun(int pcid, User user, String plocation, String ptime, String pcontent, String pzan, String ispzan) {
        this.pcid = pcid;
        this.user = user;
        this.plocation = plocation;
        this.ptime = ptime;
        this.pcontent = pcontent;
        this.pzan = pzan;
        this.ispzan = ispzan;
    }

    public News_pinglun(int pid, int pcid, User user, String plocation, String ptime, String pcontent, String pzan, String ispzan) {
        this.pid = pid;
        this.pcid = pcid;
        this.user = user;
        this.plocation = plocation;
        this.ptime = ptime;
        this.pcontent = pcontent;
        this.pzan = pzan;
        this.ispzan = ispzan;
    }

    public News_luntan getNews_luntan() {
        return news_luntan;
    }

    public void setNews_luntan(News_luntan news_luntan) {
        this.news_luntan = news_luntan;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getPcid() {
        return pcid;
    }

    public void setPcid(int pcid) {
        this.pcid = pcid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPlocation() {
        return plocation;
    }

    public void setPlocation(String plocation) {
        this.plocation = plocation;
    }

    public String getPtime() {
        return ptime;
    }

    public void setPtime(String ptime) {
        this.ptime = ptime;
    }

    public String getPcontent() {
        return pcontent;
    }

    public void setPcontent(String pcontent) {
        this.pcontent = pcontent;
    }

    public String getPzan() {
        return pzan;
    }

    public void setPzan(String pzan) {
        this.pzan = pzan;
    }

    public String getIspzan() {
        return ispzan;
    }

    public void setIspzan(String ispzan) {
        this.ispzan = ispzan;
    }

    public News_pinglun() {
    }
}
