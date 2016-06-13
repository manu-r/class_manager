package com.bitspilani.classmanager.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by I311849 on 09/Jun/2016.
 */
public class Faculty {
    private String email;
    private String firstName;
    private String lastName;
    private String phNo;
    private UserType userType;

    private Time pickupTime;
    private Time returnTime;

    private Time sessionStart;
    private Time sessionEnd;


    public Faculty(String email) {
        this.email = email;
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

    public String getPhNo() {
        return phNo;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Time getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(Time pickupTime) {
        this.pickupTime = pickupTime;
    }

    public Time getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Time returnTime) {
        this.returnTime = returnTime;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhNo(String phNo) {
        this.phNo = phNo;
    }

    public Time getSessionStart() {
        return sessionStart;
    }

    public void setSessionStart(Time sessionStart) {
        this.sessionStart = sessionStart;
    }

    public Time getSessionEnd() {
        return sessionEnd;
    }

    public void setSessionEnd(Time sessionEnd) {
        this.sessionEnd = sessionEnd;
    }

    public Map<String, Object> getMap() {
        HashMap<String, Object> map = new HashMap();
        map.put("email", email);
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("sessionStart", sessionStart);
        map.put("sessionEnd", sessionEnd);
        map.put("pickupTime", pickupTime);
        map.put("returnTime", returnTime);
        map.put("phNo", phNo);
        return map;
    }
}
