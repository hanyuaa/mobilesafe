package com.itheima.mobilesafe.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.SPUtil;
import com.itheima.mobilesafe.utils.ToastUtil;
import com.itheima.mobilesafe.view.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {

    private SettingItemView simBand;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        initUI();
    }

    private void initUI() {
        simBand = findViewById(R.id.siv_sim_band);
        //判断是否选中绑定sim卡
        final String simNumber = SPUtil.getString(this, ConstantValue.SIM_NUMBER, "");

        if (TextUtils.isEmpty(simNumber)) {
            simBand.setCheck(false);
        } else {
            simBand.setCheck(true);
        }

        simBand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = simBand.isCheck();
                simBand.setCheck(!check);
                if (!check) {
                    //如果绑定
                    //获取sim卡号,存储
                    boolean permission = checkReadPermission(Manifest.permission.READ_PHONE_STATE, 110);
                    if (permission) {
                        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

                    }
                    //String simSerialNumber = tm.getSimSerialNumber();
                    String simSerialNumber = "simSerialNumber";
                    SPUtil.putString(getApplicationContext(), ConstantValue.SIM_NUMBER, simSerialNumber);
                } else {
                    //如果解绑
                    SPUtil.remove(getApplicationContext(), ConstantValue.SIM_NUMBER);
                }
            }
        });
    }

    public boolean checkReadPermission(String string_permission, int request_code) {
        boolean flag = false;
        if (ContextCompat.checkSelfPermission(this, string_permission) == PackageManager.PERMISSION_GRANTED) {//已有权限
            flag = true;
        } else {//申请权限
            ActivityCompat.requestPermissions(this, new String[]{string_permission}, request_code);
        }
        return flag;
    }


    @Override
    public void showNextPage() {
        String simNumber = SPUtil.getString(this, ConstantValue.SIM_NUMBER, "");
        if (!TextUtils.isEmpty(simNumber)) {
            Intent intent = new Intent(this, Setup3Activity.class);
            startActivity(intent);

            finish();
            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);

        } else {
            ToastUtil.show(this, "请绑定sim卡");
        }
    }

    @Override
    public void showPrePage() {
        Intent intent = new Intent(this, Setup1Activity.class);
        startActivity(intent);

        finish();

        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }
}
