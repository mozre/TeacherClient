package com.ckt.ckttodo.database;

import android.content.Context;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {

    @PrimaryKey
    private String userId;

    private String userName;
    private String password;
    private String mail;
    private String phone;
    private String userIconAddress;
    private String token;

    public User() {
    }

    public User(Context context) {
    }

    public User(String userName, String password, String mail, String phone, String userIconAddress) {
        this.userName = userName;
        this.password = password;
        this.mail = mail;
        this.phone = phone;
        this.userIconAddress = userIconAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUserIconAddress(String userIconAddress) {
        this.userIconAddress = userIconAddress;
    }

    public String getPassword() {
        return password;
    }

    public String getUserIconAddress() {
        return userIconAddress;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
