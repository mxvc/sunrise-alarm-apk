// java/AlarmReceiver.java
package io.github.mxvc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.util.Date;

import io.github.mxvc.utils.AlarmUtil;
import io.github.mxvc.utils.PrefUtil;
import io.github.mxvc.utils.SunUtil;

public class AlarmReceiver extends BroadcastReceiver {

    private MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 在这里执行闹钟触发时的操作
        Toast.makeText(context, "Alarm! Wake up!", Toast.LENGTH_SHORT).show();
        playAlarmSound(context);

        // 设置下次闹钟
        AlarmUtil.setAlarm(context);
    }

    private void playAlarmSound(Context context) {
        mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound);
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }



    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
