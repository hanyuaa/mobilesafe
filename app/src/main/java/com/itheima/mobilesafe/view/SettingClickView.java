package com.itheima.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.mobilesafe.R;

public class SettingClickView extends RelativeLayout {

    private TextView tvTitle;
    private TextView tvDes;

    public SettingClickView(Context context) {
        this(context, null);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //xml->view 将设置界面的一个条目转换成view对象
        View view = View.inflate(context, R.layout.setting_click_view, this);

        //自定义组合控件中的标题描述
        tvTitle = findViewById(R.id.tv_title);
        tvDes = findViewById(R.id.tv_des);

    }

    public void setTvTitle(String title){
        tvTitle.setText(title);
    }

    public void setTvDes(String des) {
        tvDes.setText(des);
    }
}
