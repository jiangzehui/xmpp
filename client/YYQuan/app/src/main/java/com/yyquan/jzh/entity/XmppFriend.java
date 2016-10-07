package com.yyquan.jzh.entity;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by Administrator on 2016/1/31.
 */
public class XmppFriend implements Serializable, Comparable<Object> {

    private User user;
    private int status;//0.在线 1.Q我吧 2.忙碌 3.勿扰 4.离开 5.隐身 6.离线

    public XmppFriend(User user, int status) {
        this.user = user;
        this.status = status;
    }

    public XmppFriend(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int compareTo(Object o) {

        if (this == o) {
            return 0;
        } else if (o != null && o instanceof XmppFriend) {
            XmppFriend xf = (XmppFriend) o;
            if (status <= xf.status) {
                return -1;
            } else {
                return 1;
            }
        } else {
            return -1;
        }

    }
}
