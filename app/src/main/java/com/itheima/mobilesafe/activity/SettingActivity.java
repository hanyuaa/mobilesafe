package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.service.PhoneAddressService;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.SPUtil;
import com.itheima.mobilesafe.utils.ServiceUtil;
import com.itheima.mobilesafe.view.SettingClickView;
import com.itheima.mobilesafe.view.SettingItemView;

public class SettingActivity extends Activity {

    private String[] mToastStyleDes;
    private SettingClickView scvToast;
    private int idx;
    private SettingClickView scvLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initUpdate();
        initPhoneAddress();
        initToastStyle();
        initLocation();
        initBlacknumber();
    }

    /**
     * 拦截黑名单
     */
    private void initBlacknumber() {
        final SettingItemView sivBlacknumber = findViewById(R.id.siv_blacknumber);

        boolean isRunning = ServiceUtil.isRunning(this, BlackNumberService.class.getName());

        sivBlacknumber.setCheck(isRunning);
        sivBlacknumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果之前是选中的, 点击后变成未选中
                //修改成点击CheckBox判断
                boolean check = sivBlacknumber.isCheck();
                sivBlacknumber.setCheck(!check);

                if (!check) {
                    //开启服务
                    startService(new Intent(getApplicationContext(), BlackNumberService.class));
                } else {
                    //关闭服务
                    stopService(new Intent(getApplicationContext(), BlackNumberService.class));
                }
            }
        });
    }

    /**
     * 归属地在屏幕的位置
     */
    private void initLocation() {
        scvLocation = findViewById(R.id.scv_location);
        scvLocation.setTvTitle("归属地提示框的位置");
        scvLocation.setTvDes("设置归属地提示框的位置");

        scvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ToastLocationActivity.class));
            }
        });
    }

    private void initToastStyle() {
        scvToast = findViewById(R.id.scv_toast_view);
        scvToast.setTvTitle("设置归属地显示风格");
        //创建描述文字所在的String类型数组
        mToastStyleDes = new String[]{"透明", "橙色", "蓝色", "灰色", "绿色"};
        //获取之前设置的索引值,用于获取描述文字
        idx = SPUtil.getInt(this, ConstantValue.TOAST_STYLE, 0);
        scvToast.setTvDes(mToastStyleDes[idx]);
        //监听点击事件,弹出对话框
        scvToast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToastStyleDialog();
            }
        });
    }

    /**
     * 创建选中显示样式的对话框
     */
    private void showToastStyleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.ic_launcher_background);
        builder.setTitle("请选择归属地样式");
        //选择单个条目事件监听
        builder.setSingleChoiceItems(mToastStyleDes, SPUtil.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE, 0), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //记录选中条目的索引值
                SPUtil.putInt(getApplicationContext(), ConstantValue.TOAST_STYLE, which);
                //关闭对话框
                dialog.dismiss();
                //显示选中色值文字
                scvToast.setTvDes(mToastStyleDes[which]);
            }
        });

        //取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //显示对话框
        builder.show();
    }

    /**
     * 电话号码归属地
     */
    private void initPhoneAddress() {
        final SettingItemView sivPhoneAddres = findViewById(R.id.siv_phone_address);

        //对服务是否开启的状态进行显示
        boolean isRunning = ServiceUtil.isRunning(this, PhoneAddressService.class.getName());
        sivPhoneAddres.setCheck(isRunning);
        //设置点击事件
        sivPhoneAddres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果之前是选中的, 点击后变成未选中
                //修改成点击CheckBox判断
                boolean check = sivPhoneAddres.isCheck();
                sivPhoneAddres.setCheck(!check);

                if (!check) {
                    //开启服务
                    startService(new Intent(getApplicationContext(), PhoneAddressService.class));
                } else {
                    //关闭服务
                    stopService(new Intent(getApplicationContext(), PhoneAddressService.class));
                }
            }
        });
    }

    private void initUpdate() {
        final SettingItemView sivUpdate = findViewById(R.id.siv_update);

        boolean updateConfig = SPUtil.getBoolean(this, ConstantValue.UPDATE_CONFIG, false);
        sivUpdate.setCheck(updateConfig);
        sivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果之前是选中的, 点击后变成未选中
                //修改成点击CheckBox判断
                boolean check = sivUpdate.isCheck();
                sivUpdate.setCheck(!check);

                SPUtil.putBoolean(getApplicationContext(), ConstantValue.UPDATE_CONFIG, !check);
            }
        });
    }
}
