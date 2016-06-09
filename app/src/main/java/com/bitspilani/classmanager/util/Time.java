package com.bitspilani.classmanager.util;

/**
 * Created by I311849 on 08/Jun/2016.
 */
public class Time {
    private int hour;
    private int minute;

    public Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getMinute() {
        return minute;
    }

    public int getHour() {
        return hour;
    }
}
