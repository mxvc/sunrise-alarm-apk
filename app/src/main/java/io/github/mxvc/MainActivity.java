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
import android.os.Handler;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import io.github.mxvc.utils.AlarmUtil;
import io.github.mxvc.utils.GPSUtil;
import io.github.mxvc.utils.PrefUtil;
import io.github.mxvc.utils.SunUtil;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, LocationListener {

    private static final String TAG = "SUN-RISE";


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    public static final String KEY_STATUS = "status";


    private ImageView imageView;
    private Switch sw;
    private TextView timeLabel;

    private PrefUtil prefUtil ;

    private  Location currentLocation;

    private Handler handler = new Handler();
    private Runnable updateFrame = new Runnable() {
        @Override
        public void run() {
            // 更新界面
            long nextTime = prefUtil.getNextTime();

            if(nextTime <= 0){
                timeLabel.setText("未启用");
            }else {
                String between = DateUtil.formatBetween(new Date(), new Date(nextTime), BetweenFormatter.Level.SECOND);
                timeLabel.setText(between);
            }

            // 每隔一秒再次执行
            handler.postDelayed(this, 1000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.backgroundImageView);
        sw = findViewById(R.id.switch1);
        timeLabel = findViewById(R.id.timeLabel);

        prefUtil = PrefUtil.getInstance(this);

        sw.setOnCheckedChangeListener(this);


        boolean status = prefUtil.getBoolean(KEY_STATUS, false);

        render();

        boolean gps = getGPS();

        sw.setChecked(status);
        sw.setEnabled(gps);
        handleStatus(status);


        handler.postDelayed(updateFrame,1000);
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


        AlarmUtil.setAlarm(this);
    }

    private void disable() {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0); // 设置饱和度为0，使图片变为灰度图
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imageView.setColorFilter(filter);


        prefUtil.saveBoolean(KEY_STATUS, false);

        AlarmUtil.cancelAlarm(this);

        prefUtil.removeNextTime();
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
        render();
    }

    private void handleGetGps(Location location) {
        currentLocation = location;
        prefUtil.saveLocation(location);
        Log.i(TAG, "获取到经纬度" + location);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.removeUpdates(this);

        sw.setEnabled(true);

        if(sw.isChecked()){
            AlarmUtil.cancelAlarm(this);
            AlarmUtil.setAlarm(this);
        }
    }


    private void render() {
        StringBuilder sb = new StringBuilder();

        String zone = TimeZone.getDefault().getDisplayName();
        sb.append("时区: ").append(zone).append("\n");

        if(currentLocation != null){
            double lng = currentLocation.getLongitude();
            double lat = currentLocation.getLatitude();
            sb.append("经度: ").append(lng).append("\n");
            sb.append("纬度: ").append(lat).append("\n");
            SunriseSunsetCalculator calc = new SunriseSunsetCalculator(new com.luckycatlabs.sunrisesunset.dto.Location(lat,lng),TimeZone.getDefault());
            Calendar now = Calendar.getInstance();

            sb.append("日出（天文）:").append(calc.getAstronomicalSunriseForDate(now)).append("\n");
            sb.append("日出（航海）:").append(calc.getNauticalSunriseForDate(now)).append("\n");
            sb.append("日出（民用）:").append(calc.getCivilSunriseForDate(now)).append("\n");
            sb.append("日出（官方）:").append(calc.getOfficialSunriseForDate(now)).append("\n");
        }

        sb.append("当前时间: ").append(DateUtil.now()).append("\n");


        long time = prefUtil.getNextTime();
        if(time > 0){
            String alertTime = DateUtil.formatDateTime(new Date(time));
            sb.append("闹钟时间: ").append(alertTime).append("\n");
        }else {
            sb.append("闹钟时间: ").append("未设置").append("\n");
        }

        TextView textView = findViewById(R.id.settingLabel);
        textView.setText(sb);
    }

    private void renderTime(){

    }


}