package com.ckt.ckttodo.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.ckt.ckttodo.util.Constants;

public class User {

    private String userId;

    private String userName;
    private String password;
    private String token;
    private static SharedPreferences sharedPreferences;

    public User(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.SHARE_NAME_CKT, Context.MODE_PRIVATE);
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        if (TextUtils.isEmpty(userName)) {
            userName = sharedPreferences.getString("username", null);
        }
        return userName;
    }

    public void setUserName(String userName) {
        if (sharedPreferences.edit().putString("username", userName).commit()) {
            this.userName = userName;
        }
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getPassword() {
        return password;
    }


    public String getToken() {
        if (this.token == null) {
            this.token = sharedPreferences.getString("token", null);
        }
        return token;
    }


    public void setToken(String token) {
        if (sharedPreferences.edit().putString("token", token).commit()) {
            this.token = token;
        }
    }

    public String getUserId() {
        if (this.userId == null) {
            this.userId = sharedPreferences.getString("userid", null);
        }
        return userId;
    }

    public void setUserId(String userId) {
        if (sharedPreferences.edit().putString("userid", token).commit()) {
            this.userId = userId;
        }
    }
}
