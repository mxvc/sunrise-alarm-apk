package io.github.mxvc.utils;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;

import androidx.core.app.ActivityCompat;

public class GPSUtil {


    public static boolean isGpsAble(LocationManager lm) {
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
