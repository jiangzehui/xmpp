package com.jzh.news.entity;

/**
 * Created by jzh on 2015/10/3.
 */
public class User {
	private int id;
	private String user;// 用户名
	private String password;// 密码
	private String qq;// qq号码
	private String icon;// 头像地址
	private String nickname;// 昵称
	private String city;// 城市
	private String sex;// 性别
	private String years;// 年龄
	private String location;// 注册地址
	private String qianming;// 个性签名

	public User() {
	}

	public User(String user, String password, String qq, String icon,
			String nickname, String city, String sex, String years,
			String location, String qianming) {
		super();
		this.user = user;
		this.password = password;
		this.qq = qq;
		this.icon = icon;
		this.nickname = nickname;
		this.city = city;
		this.sex = sex;
		this.years = years;
		this.location = location;
		this.qianming = qianming;
	}

	public User(int id, String user, String password, String qq, String icon,
			String nickname, String city, String sex, String years,
			String location, String qianming) {
		super();
		this.id = id;
		this.user = user;
		this.password = password;
		this.qq = qq;
		this.icon = icon;
		this.nickname = nickname;
		this.city = city;
		this.sex = sex;
		this.years = years;
		this.location = location;
		this.qianming = qianming;
	}

	public String getQianming() {
		return qianming;
	}

	public void setQianming(String qianming) {
		this.qianming = qianming;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getYears() {
		return years;
	}

	public void setYears(String years) {
		this.years = years;
	}

}
