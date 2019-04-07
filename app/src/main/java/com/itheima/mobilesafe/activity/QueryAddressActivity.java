package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.itheima.mobilesafe.R;

import java.io.IOException;

public class QueryAddressActivity extends Activity {

    private EditText etPhone;
    private Button btnQuery;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_address);

        String path = getFilesDir().getAbsolutePath() + "address.db";
        Log.i("QueryAddress", path);
        //AddressDao.getAddress("130000000000");

        etPhone = findViewById(R.id.et_phone);
        btnQuery = findViewById(R.id.btn_query);

        //添加抖动效果
        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                //interpolator 插补器,数学函数
                //自定义插补器
                /*shake.setInterpolator(new Interpolator() {
                    @Override
                    public float getInterpolation(float input) {
                        return 0;
                    }
                });*/
                etPhone.startAnimation(shake);
                
                //手机震动效果
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                //震动的毫秒值
                vibrator.vibrate(2000);
            }
        });

        //添加内容变更监听器
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
