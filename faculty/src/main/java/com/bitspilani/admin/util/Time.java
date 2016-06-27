package com.bitspilani.admin.util;

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

    public Time() {
        this(00, 00);
    }

    public int getMinute() {
        return minute;
    }

    public int getHour() {
        return hour;
    }

    @Override
    public String toString() {
        return AppUtil.formatTime(this);
    }
}
