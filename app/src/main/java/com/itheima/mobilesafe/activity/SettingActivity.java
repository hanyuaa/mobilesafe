package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.SPUtil;
import com.itheima.mobilesafe.view.SettingItemView;

public class SettingActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initUpdate();
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
