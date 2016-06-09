package com.bitspilani.classmanager.util;

/**
 * Created by I311849 on 09/Jun/2016.
 */
public class Faculty {
    private String email;
    private String firstName;
    private String lastName;
    private int[] phNo;
    private UserType userType;


    public Faculty(String email, String firstName, String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int[] getPhNo() {
        return phNo;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}
