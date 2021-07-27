package com.capi.ecomshoppingapp.Model;

public class User
{
    private String id;
    private String fullname;
    private String imageurl;
    private String birthday;
    private String phone;
    private String email;
    private String mode;

    public User(String id, String fullname, String imageurl, String birthday, String phone, String email, String mode) {
        this.id = id;
        this.fullname = fullname;
        this.imageurl = imageurl;
        this.birthday = birthday;
        this.phone = phone;
        this.email = email;
        this.mode = mode;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
