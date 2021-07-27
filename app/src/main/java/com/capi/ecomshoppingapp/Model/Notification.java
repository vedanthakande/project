package com.capi.ecomshoppingapp.Model;

public class Notification
{
    String title;
    String image;
    String date;
    String text;
    Integer type;

    public Notification(String title, String image, String date, String text, Integer type) {
        this.title = title;
        this.image = image;
        this.date = date;
        this.text = text;
        this.type = type;
    }

    public Notification() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
