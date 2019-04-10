package com.itheima.mobilesafe.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.engine.SmsBackup;

import java.io.File;

public class AToolActivity extends Activity {

    private TextView tvQueryPhone;
    private TextView tvSmsBackup;
    private ProgressBar pbBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool);

        initPhoneAddress();
        initSmsBackUp();
    }

    private void initSmsBackUp() {
        tvSmsBackup = findViewById(R.id.tv_sms_backup);
        pbBar = findViewById(R.id.pb_bar);
        tvSmsBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSmsBackUpDialog();
            }
        });
    }

    private void showSmsBackUpDialog() {
        //创建一个带进度条的对话框
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIcon(R.drawable.ic_launcher_background);
        dialog.setTitle("短信备份");
        //指定进度条的样式 水平
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //展示进度条
        dialog.show();

        //检查权限,直接调用备份短信方法
        boolean permission = checkReadPermission(Manifest.permission.READ_SMS, 112);
        if (permission) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String path = getFilesDir().getAbsolutePath() + File.separator + "sms_bak.xml";
                    //SmsBackup.backup(getApplicationContext(), path, dialog);
                    SmsBackup.backup(getApplicationContext(), path, new SmsBackup.CallBack() {
                        @Override
                        public void setMax(int max) {
                            dialog.setMax(max);
                            pbBar.setMax(max);
                        }

                        @Override
                        public void setProgress(int progress) {
                            dialog.setProgress(progress);
                            pbBar.setProgress(progress);
                        }
                    });

                    dialog.dismiss();
                }
            }).start();
        }
    }

    private void initPhoneAddress() {
        tvQueryPhone = findViewById(R.id.tv_query_phone_address);
        tvQueryPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), QueryAddressActivity.class));
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
}
