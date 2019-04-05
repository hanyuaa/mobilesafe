package com.itheima.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.SPUtil;
import com.itheima.mobilesafe.utils.ToastUtil;

public class Setup3Activity extends BaseSetupActivity {

    private EditText etPhoneNum;
    private Button btnSelectNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);

        initUI();
    }

    private void initUI() {
        etPhoneNum = findViewById(R.id.et_phone_number);
        btnSelectNum = findViewById(R.id.btn_select_number);

        //获取联系人电话回显过程
        String phone = SPUtil.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, "");
        if (!TextUtils.isEmpty(phone)) {
            etPhoneNum.setText(phone);
        }

        btnSelectNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContactListActivity.class);

                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果直接点击返回data会为null
        if (data != null) {
            //返回到当前界面的时候,接收结果的方法
            String phone = data.getStringExtra("phone");
            //将特殊字符过滤掉(-,空格)
            phone = phone.replace("-", "").replace(" ", "").trim();
            etPhoneNum.setText(phone);

            //存储联系人
            SPUtil.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);
        }
    }

    @Override
    public void showNextPage() {
        //点击按钮以后,需要获取输入框中的联系人,再做下一页操作
        String etPhone = etPhoneNum.getText().toString().trim();

        //String phone = SPUtil.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, "");
        if (!TextUtils.isEmpty(etPhone)) {
            Intent intent = new Intent(this, Setup4Activity.class);
            startActivity(intent);

            finish();
            //如果现在是输入电话号码,则需要去保存
            SPUtil.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, etPhone);
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);

        } else {
            ToastUtil.show(this, "请输入电话号码");
        }
    }

    @Override
    public void showPrePage() {
        Intent intent = new Intent(this, Setup2Activity.class);
        startActivity(intent);

        finish();
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);

    }
}
