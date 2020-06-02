package com.zm.c3p0;

import java.sql.Blob;
import java.util.Date;

public class News {
    private int id;
    private String title;
    private String auth;

    private Date date;
    //大文本
    private String content;
    //二进制
    private Blob image;

    public News() {
    }

    public News(String title, String auth, Date date) {
        this.title = title;
        this.auth = auth;
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", auth='" + auth + '\'' +
                ", date=" + date +
                '}';
    }
}
