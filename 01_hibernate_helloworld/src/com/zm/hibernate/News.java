package com.zm.hibernate;

public class News {
    private int id;
    private String title;
    private String auth;

    public News() {
    }

    public News(String title, String auth) {
        this.id = id;
        this.title = title;
        this.auth = auth;
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

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", auth='" + auth + '\'' +
                '}';
    }
}
