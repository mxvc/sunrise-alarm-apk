package io.github.mxvc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.versionedparcelable.ParcelUtils;

import java.util.Date;

import cn.hutool.core.date.DateUtil;
import io.github.mxvc.utils.PreferencesUtil;
import io.github.mxvc.utils.Util;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    ImageView imageView;
    Switch sw;
    private static final int ALARM_REQUEST_CODE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.backgroundImageView);

        sw = findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener(this);

        boolean on = PreferencesUtil.getInstance(this).getBoolean("status", false);
        sw.setChecked(on);
        handleStatus(on);



    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        handleStatus(b);

    }

    private void handleStatus(boolean b) {
        if (b) {
            enable();
        } else {
            disable();
        }
    }

    private void enable() {
        imageView.setColorFilter(null);
        PreferencesUtil.getInstance(this).saveBoolean("status", true);


        Date nextTime = Util.getNextTime();
        Toast.makeText(this, DateUtil.formatDateTime(nextTime), Toast.LENGTH_SHORT).show();


       // setAlarm(System.currentTimeMillis() + 1000 * 5);
    }

    private void disable() {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0); // 设置饱和度为0，使图片变为灰度图
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imageView.setColorFilter(filter);

        PreferencesUtil.getInstance(this).saveBoolean("status", false);

        cancelAlarm();
    }


    private void setAlarm(long alarmTime) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent);


    }

    public  void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(alarmIntent);
    }

}