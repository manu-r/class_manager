package com.bitspilani.admin.util;

import android.util.Log;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by I311849 on 14/Jun/2016.
 */
public class AppUtil {

    private static final String TAG = "AppUtil";

    public static String formatTime(Time time) {
        return AppUtil.formatTime(time.getHour(), time.getMinute());
    }

    public static String formatTime(int hourOfDay, int minute) {
        int iHour = hourOfDay % 12;
        if(iHour == 0) {
            iHour = 12;
        }
        String a = hourOfDay / 12 == 0 ? "AM" : "PM";
        return String.format(Locale.ENGLISH, "%02d:%02d %s", iHour, minute, a);
    }

    public static Time getTime(String time) {
        String pattern = "([0][1-9]|[1][0-2]):([0-5][0-9]) (AM|PM)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(time);
        m.matches();
        String hour = m.group(1);
        String minute = m.group(2);
        String a = m.group(3);

        Log.d(TAG, "getTime: " +hour);
        int iHour = Integer.parseInt(hour);
        Log.d(TAG, "getTime: " +minute);
        int iMinute = Integer.parseInt(minute);
        iHour += a.equals("PM") ? 12 : 0;

        return new Time(iHour, iMinute);
    }
}
