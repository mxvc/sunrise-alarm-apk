package io.github.mxvc.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.luckycatlabs.sunrisesunset.dto.Location;

import java.util.Date;

import io.github.mxvc.AlarmReceiver;

public class AlarmUtil {
    public static final int ALARM_REQUEST_CODE = 100;

    public static void setAlarm(Context context) {
        PrefUtil prefUtil = PrefUtil.getInstance(context);
        Location location = prefUtil.getLocation();
        Date nextTime = SunUtil.getNextTime(location);


        prefUtil.saveNextTime(nextTime.getTime());

        long alarmTime = nextTime.getTime();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setWindow(AlarmManager.RTC_WAKEUP, alarmTime,60, alarmIntent);
    }

    public static void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(alarmIntent);
    }
}
