package com.bitspilani.admin.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by I311849 on 09/Jun/2016.
 */
public class Faculty {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phNo;
    private UserType userType;

    private String pickupLocation;
    private String dropLocation;

    private Time pickupTime;
    private Time returnTime;

    private Time sessionStart;
    private Time sessionEnd;


    public Faculty() {
        sessionStart = new Time();
        sessionEnd = new Time();

        pickupTime = new Time();
        returnTime = new Time();
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

    public void setPickupTime(String pickupTime) {
        this.pickupTime = AppUtil.getTime(pickupTime);
    }

    public Time getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = AppUtil.getTime(returnTime);
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

    public void setSessionStart(String sessionStart) {
        this.sessionStart = AppUtil.getTime(sessionStart);
    }

    public Time getSessionEnd() {
        return sessionEnd;
    }

    public void setSessionEnd(String sessionEnd) {
        this.sessionEnd = AppUtil.getTime(sessionEnd);
    }

    public Map<String, Object> getMap() {
        HashMap<String, Object> map = new HashMap();
        map.put("email", email);
        map.put("id", id);
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("sessionStart", sessionStart.toString());
        map.put("sessionEnd", sessionEnd.toString());
        map.put("pickupTime", pickupTime.toString());
        map.put("returnTime", returnTime.toString());
        map.put("pickupLocation", pickupLocation);
        map.put("dropLocation", dropLocation);
        map.put("phNo", phNo);
        return map;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    @Override
    public String toString() {
        return "Email: " + email +
                ";Id: " + id +
                ";First Name: " + firstName +
                ";Last Name: " + lastName +
                ";Session Start: " + sessionStart.toString() +
                ";Session End: " + sessionEnd.toString() +
                ";Pickup Time: " + pickupTime.toString() +
                ";Return Time: " + returnTime.toString() +
                ";Pickup Location: " + pickupLocation +
                ";Drop Location: " + dropLocation +
                ";Ph No: " + phNo;
    }
}
