package com.itheima.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.SPUtil;

public class SettingItemView extends RelativeLayout {

    private CheckBox cbBox;
    private TextView tvDes;
    private TextView tvTitle;
    private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.itheima.mobilesafe";
    private String mDesTitle;
    private String mDesOff;
    private String mDesOn;

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //xml->view 将设置界面的一个条目转换成view对象
        View view = View.inflate(context, R.layout.setting_item_view, this);
       /*等同于上面
        View view = View.inflate(context, R.layout.setting_item_view, this);
        this.addView(view);*/
        //自定义组合控件中的标题描述
        tvTitle = findViewById(R.id.tv_title);
        tvDes = findViewById(R.id.tv_des);
        cbBox = findViewById(R.id.cb_box);

        cbBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = cbBox.isChecked();
                if (checked) {
                    //开启
                    tvDes.setText(mDesOn);
                } else {
                    //关闭
                    tvDes.setText(mDesOff);
                }

                SPUtil.putBoolean(getContext(), ConstantValue.UPDATE_CONFIG, checked);
            }
        });

        //获取自定义以及原生属性的操作,写在此处AttributeSet attrs中获取
        initAttrs(attrs);
        tvTitle.setText(mDesTitle);
    }

    /**
     * 构造方法中维护好的属性集合
     * 返回属性集合中自定义属性的属性值
     */
    private void initAttrs(AttributeSet attrs) {
        mDesTitle = attrs.getAttributeValue(NAMESPACE, "des_title");
        mDesOff = attrs.getAttributeValue(NAMESPACE, "des_off");
        mDesOn = attrs.getAttributeValue(NAMESPACE, "des_on");
    }


    /**
     * 返回当前SettingItemView是否选中状态
     *
     * @return true开启 false关闭
     */
    public boolean isCheck() {
        return cbBox.isChecked();
    }

    /**
     * @param isCheck
     */
    public void setCheck(boolean isCheck) {
        cbBox.setChecked(isCheck);
        if (isCheck) {
            //开启
            tvDes.setText(mDesOn);
        } else {
            //关闭
            tvDes.setText(mDesOff);
        }
    }
}
