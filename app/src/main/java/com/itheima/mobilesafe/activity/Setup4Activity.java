package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.SPUtil;
import com.itheima.mobilesafe.utils.ToastUtil;

public class Setup4Activity extends BaseSetupActivity {

    private CheckBox cbBox;
    private String onText = "安全设置已开启";
    private String offText = "安全设置已关闭";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);

        initUI();
    }


    private void initUI() {
        cbBox = findViewById(R.id.cb_box);
        //是否选中状态的回显过程
        boolean openSecurity = SPUtil.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
        cbBox.setChecked(openSecurity);
        //修改CheckBox文字
        if (openSecurity) {
            cbBox.setText(onText);
        } else {
            cbBox.setText(offText);
        }

        cbBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //isChecked点击之后的状态
                //点击过程中切换状态
                SPUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_SECURITY, isChecked);
                //根据状态显示文字
                if (isChecked) {
                    cbBox.setText(onText);
                } else {
                    cbBox.setText(offText);
                }
            }
        });
    }

    @Override
    public void showNextPage() {
        boolean openSecurity = SPUtil.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
        if (openSecurity) {
            Intent intent = new Intent(this, SetupOverActivity.class);
            startActivity(intent);

            finish();

            SPUtil.putBoolean(this, ConstantValue.SETUP_OVER, true);

            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
        } else {
            ToastUtil.show(this, "请开启防盗保护");
        }
    }

    @Override
    public void showPrePage() {
        Intent intent = new Intent(this, Setup3Activity.class);
        startActivity(intent);

        finish();

        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }
}
