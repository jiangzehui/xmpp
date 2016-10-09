package com.jzh.news.entity;

public class News_type {
	private int id;
	private String type_name;
	private String type_url;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType_name() {
		return type_name;
	}

	public void setType_name(String type_name) {
		this.type_name = type_name;
	}

	public String getType_url() {
		return type_url;
	}

	public void setType_url(String type_url) {
		this.type_url = type_url;
	}

	public News_type(int id, String type_name, String type_url) {
		super();
		this.id = id;
		this.type_name = type_name;
		this.type_url = type_url;
	}

	public News_type() {

	}

}
