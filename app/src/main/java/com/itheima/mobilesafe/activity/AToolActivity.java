package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.itheima.mobilesafe.R;

public class AToolActivity extends Activity {

    private TextView tvQueryPhone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool);

        initPhoneAddress();
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
}
