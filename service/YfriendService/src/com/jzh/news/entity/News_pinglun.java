package com.jzh.news.entity;

import java.io.Serializable;

/**
 * Created by jzh on 2015/9/28.
 */
public class News_pinglun implements Serializable {
	private int pid;
	private int pcid;
	private String user;
	private String plocation;
	private String ptime;
	private String pcontent;
	private String pzan;
	
	
	//plid, user, location, time,
	//content
	
	

	public News_pinglun(int pcid, String user, String plocation, String ptime,
			String pcontent, String pzan) {
		super();
		this.pcid = pcid;
		this.user = user;
		this.plocation = plocation;
		this.ptime = ptime;
		this.pcontent = pcontent;
		this.pzan = pzan;
	}

	

	public News_pinglun(int pid, int pcid, String user, String plocation,
			String ptime, String pcontent, String pzan) {
		super();
		this.pid = pid;
		this.pcid = pcid;
		this.user = user;
		this.plocation = plocation;
		this.ptime = ptime;
		this.pcontent = pcontent;
		this.pzan = pzan;
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

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
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

	public News_pinglun() {
	}
}
