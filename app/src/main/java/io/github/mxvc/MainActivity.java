package io.github.mxvc;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.versionedparcelable.ParcelUtils;

import io.github.mxvc.utils.PreferencesUtil;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    ImageView imageView;
    Switch sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      imageView= findViewById(R.id.backgroundImageView);

       sw = findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener(this);

       boolean on = PreferencesUtil.getInstance(this).getBoolean("status",false);
       sw.setChecked(on);
       handleStatus(on);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        handleStatus(b);

    }

    private void handleStatus(boolean b) {
        if(b){
            enable();
        }else {
            disable();
        }
    }

    private void enable(){
        imageView.setColorFilter(null);
        PreferencesUtil.getInstance(this).saveBoolean("status",true);
    }

    private void disable(){

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0); // 设置饱和度为0，使图片变为灰度图
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imageView.setColorFilter(filter);


        PreferencesUtil.getInstance(this).saveBoolean("status",false);

    }

}