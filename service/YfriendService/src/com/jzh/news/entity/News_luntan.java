package com.jzh.news.entity;

import java.io.Serializable;

public class News_luntan implements Serializable {
    private int lid;
   
    
    private String user;
    private String content;
    private String image;
    private String time;
    
    private String location;
    
    
    

    

	public int getLid() {
        return lid;
    }

    public void setLid(int lid) {
        this.lid = lid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

   

    public News_luntan() {
        super();

    }

}
