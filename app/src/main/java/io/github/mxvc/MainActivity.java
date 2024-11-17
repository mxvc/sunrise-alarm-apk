package io.github.mxvc;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import io.github.mxvc.utils.GPSUtil;
import io.github.mxvc.utils.PrefUtil;
import io.github.mxvc.utils.Util;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, LocationListener {

    private static final String TAG = "SUN-RISE";
    private static final int ALARM_REQUEST_CODE = 100;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    public static final String KEY_STATUS = "status";
    public static final String KEY_TIME = "time";

    private ImageView imageView;
    private Switch sw;

    private PrefUtil prefUtil ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.backgroundImageView);
        sw = findViewById(R.id.switch1);
        prefUtil = PrefUtil.getInstance(this);

        sw.setOnCheckedChangeListener(this);


        boolean status = prefUtil.getBoolean(KEY_STATUS, false);

        render();

        boolean gps = getGPS();

        sw.setChecked(status);
        sw.setEnabled(gps);
        handleStatus(status);
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        handleStatus(b);

    }

    private void handleStatus(boolean b) {
        if (b) {
            boolean loc = getGPS();
            if (!loc) {
                return;
            }
            enable();
        } else {
            disable();
        }
        render();
    }

    private void enable() {
        imageView.setColorFilter(null);
        prefUtil.saveBoolean(KEY_STATUS, true);



        Date nextTime = Util.getNextTime(prefUtil.getLocation());
        prefUtil.saveLong(KEY_TIME,nextTime.getTime());

        setAlarm(nextTime.getTime());
    }

    private void disable() {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0); // 设置饱和度为0，使图片变为灰度图
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imageView.setColorFilter(filter);


        prefUtil.saveBoolean(KEY_STATUS, false);

        cancelAlarm();

        prefUtil.remove(KEY_TIME);
    }


    private void setAlarm(long alarmTime) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent);


    }

    public void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(alarmIntent);
    }

    private boolean getGPS() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //设置间隔两秒获得一次GPS定位信息
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            Toast.makeText(this, "请开启位置权限", Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean gpsAble = GPSUtil.isGpsAble(lm);
        Log.i(TAG, "GPS是否可用" + gpsAble);
        if (!gpsAble) {
            Toast.makeText(this, "GPS不可用", Toast.LENGTH_LONG).show();
            return false;
        }

        Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc == null) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0.1F, this);
            return false;
        } else {
            handleGetGps(loc);
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            this.getGPS();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("位置变化" + location);

        handleGetGps(location);
    }

    private void handleGetGps(Location location) {


        prefUtil.saveLocation(location);
        Log.i(TAG, "获取到经纬度" + location);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.removeUpdates(this);

        sw.setEnabled(true);
    }


    private void render() {
        CharSequence label = getResources().getText(R.string.settingLabel);


        long time = prefUtil.getLong(KEY_TIME, 0);
        String alertTime = DateUtil.formatDateTime(new Date(time));


        String zone = TimeZone.getDefault().getDisplayName();

        String now = DateUtil.formatDateTime(new Date());

        com.luckycatlabs.sunrisesunset.dto.Location location = prefUtil.getLocation();

        String locInfo = location.getLongitude()  + "," + location.getLatitude();

        String format = String.format(label.toString(), zone, now, locInfo, alertTime);

        TextView textView = findViewById(R.id.settingLabel);
        textView.setText(format);
    }


}