package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.SPUtil;

import org.w3c.dom.Text;

public class SetupOverActivity extends Activity {

    private TextView tvReset;
    private TextView tvPhone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean setupOver = SPUtil.getBoolean(this, ConstantValue.SETUP_OVER, false);

        if (setupOver) {
            //设置完成
            setContentView(R.layout.activity_setup_over);

            initUI();
        } else {
            //未设置,跳转到设置界面1
            Intent intent = new Intent(this, Setup1Activity.class);

            startActivity(intent);
            finish();
        }
    }

    private void initUI() {
        tvPhone = findViewById(R.id.tv_phone);
        //设置联系人号码
        String phone = SPUtil.getString(this, ConstantValue.CONTACT_PHONE, "");
        tvPhone.setText(phone);

        //重新设置条目被点击
        tvReset = findViewById(R.id.tv_reset_setup);
        tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
