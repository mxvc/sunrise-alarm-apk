package io.github.mxvc.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.luckycatlabs.sunrisesunset.dto.Location;


public class PrefUtil {

    private static final String PREFS_NAME = "SUNRISE";
    private static PrefUtil instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private PrefUtil(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized PrefUtil getInstance(Context context) {
        if (instance == null) {
            instance = new PrefUtil(context);
        }
        return instance;
    }

    public Location getLocation() {
        String loc = getString("loc", null);
        if (loc != null) {
            String[] arr = loc.split(",");

            double lng = Double.parseDouble(arr[0]);
            double lat = Double.parseDouble(arr[1]);
            return new Location(lat, lng);
        }
        return null;
    }
    public String getLocationStr() {
        return getString("loc", "无");
    }

    public void saveLocation(android.location.Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        saveString("loc", longitude + "," + latitude);
    }

    // 保存字符串
    public void saveString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    // 获取字符串
    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    // 保存布尔值
    public void saveBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    // 获取布尔值
    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    // 保存整数
    public void saveInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    // 获取整数
    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    // 保存长整数
    public void saveLong(String key, long value) {
        editor.putLong(key, value);
        editor.apply();
    }

    // 获取长整数
    public long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    // 保存浮点数
    public void saveFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.apply();
    }

    // 获取浮点数
    public float getFloat(String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    // 删除指定的键
    public void remove(String key) {
        editor.remove(key);
        editor.apply();
    }

    // 清除所有数据
    public void clear() {
        editor.clear();
        editor.apply();
    }
}
