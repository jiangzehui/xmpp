package com.yyquan.jzh.entity;

import java.io.Serializable;

public class XmppUser implements Serializable {
	
	private String userName; 
	private String name;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "User [userName=" + userName + ", name=" + name + "]";
	}

	public XmppUser(String userName, String name) {
		this.userName = userName;
		this.name = name;
	}
}
