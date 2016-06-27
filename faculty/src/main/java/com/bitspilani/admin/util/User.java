package com.bitspilani.admin.util;

/**
 * Created by I311849 on 16/Jun/2016.
 */
public class User {
    private String userId;
    private String email;

    public User(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}
