package us.logaming.myufrplanning.data.utils;

import android.content.Context;

import com.google.android.material.timepicker.TimeFormat;

public class AlarmUtils {
    public static String buildTime(Context context, int hour, int minutes) {
        StringBuilder stringBuilder = new StringBuilder();

        int adaptedHour = hour <= 12 || getClockFormat(context) == TimeFormat.CLOCK_24H ? hour : hour - 12;

        if (adaptedHour < 10) {
            stringBuilder.append("0");
        }
        stringBuilder.append(adaptedHour).append(":");

        if (minutes < 10) {
            stringBuilder.append("0");
        }
        stringBuilder.append(minutes);
        if (getClockFormat(context) == TimeFormat.CLOCK_12H) {
            stringBuilder.append(" ")
                    .append(hour < 12 ? "A.M." : "P.M.");
        }
        return stringBuilder.toString();
    }

    public static int getClockFormat(Context context) {
        return android.text.format.DateFormat.is24HourFormat(context) ? TimeFormat.CLOCK_24H : TimeFormat.CLOCK_12H;
    }
}
