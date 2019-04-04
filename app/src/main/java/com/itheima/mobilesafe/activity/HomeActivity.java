package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.SPUtil;
import com.itheima.mobilesafe.utils.ToastUtil;

public class HomeActivity extends Activity {

    private GridView gvHome;
    private String[] mTitleStr;
    private int[] mDrawableIds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initUI();
        //初始化数据的方法
        initData();
    }

    private void initData() {
        //准备数据 (文字9组, 图片9张)
        mTitleStr = new String[]{
                "手机防盗", "通信卫士", "软件管理",
                "进程管理", "流量统计", "手机杀毒",
                "缓存清理", "高级工具", "设置中心"};
        mDrawableIds = new int[]{
                R.drawable.home_safe, R.drawable.home_callmsgsafe, R.drawable.home_apps,
                R.drawable.home_taskmanager, R.drawable.home_netmanager, R.drawable.home_trojan,
                R.drawable.home_sysoptimize, R.drawable.home_tools, R.drawable.home_settings};

        //设置数据适配器
        gvHome.setAdapter(new MyAdapter());
        //注册九宫格单个条目点击事件
        gvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //position 点中列表条目的索引
                switch (position) {
                    case 0: //手机防盗
                        showDialog();
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 8: //设置中心
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void showDialog() {
        //判断本地是否有存储密码
        String psd = SPUtil.getString(this, ConstantValue.MOBILE_SAFE_PSD, "");
        if (TextUtils.isEmpty(psd)) {
            //初始化密码对话框
            showSetPsdDialog();
        } else {
            //输入密码对话框
            showConfirmPsdDialog();
        }
    }

    /**
     * 设置密码对话框
     */
    private void showSetPsdDialog() {
        //因为需要自己去定义对话框的展示样式, 所以需要调用dialog.setView();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();

        final View view = View.inflate(this, R.layout.set_psd_dialog, null);
        //让对话框显示一个自己定义的对话框界面效果
        dialog.setView(view);
        dialog.show();

        Button btSubmit = view.findViewById(R.id.bt_submit);
        Button btCancel = view.findViewById(R.id.bt_cancel);

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etSetPsd = view.findViewById(R.id.et_set_psd);
                EditText etConfirmPsd = view.findViewById(R.id.et_confirm_psd);
                String setPsd = etSetPsd.getText().toString().trim();
                String confirmPsd = etConfirmPsd.getText().toString().trim();

                if (!TextUtils.isEmpty(setPsd) && !TextUtils.isEmpty(confirmPsd)) {
                    if (setPsd.equals(confirmPsd)) {
                        Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                        startActivity(intent);
                        //取消对话框
                        dialog.dismiss();

                        SPUtil.putString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, confirmPsd);
                    } else {
                        ToastUtil.show(getApplicationContext(), "两次密码输入不一致");
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), "密码不能为空");
                }
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 确认密码对话框
     */
    private void showConfirmPsdDialog() {
        //因为需要自己去定义对话框的展示样式, 所以需要调用dialog.setView();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();

        final View view = View.inflate(this, R.layout.confirm_psd_dialog, null);
        //让对话框显示一个自己定义的对话框界面效果
        dialog.setView(view);
        dialog.show();

        Button btSubmit = view.findViewById(R.id.bt_submit);
        Button btCancel = view.findViewById(R.id.bt_cancel);

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etConfirmPsd = view.findViewById(R.id.et_confirm_psd);
                String confirmPsd = etConfirmPsd.getText().toString().trim();

                if (!TextUtils.isEmpty(confirmPsd)) {
                    String mobileSafePsd = SPUtil.getString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, "");
                    if (mobileSafePsd.equals(confirmPsd)) {
                        Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                        startActivity(intent);
                        //取消对话框
                        dialog.dismiss();
                    } else {
                        ToastUtil.show(getApplicationContext(), "密码错误");
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), "密码不能为空");
                }
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void initUI() {
        gvHome = findViewById(R.id.gv_home);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            //条目的总数
            return mTitleStr.length;
        }

        @Override
        public Object getItem(int position) {
            return mTitleStr[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
            TextView titleTV = view.findViewById(R.id.iv_title);
            ImageView iconIV = view.findViewById(R.id.iv_icon);

            titleTV.setText(mTitleStr[position]);
            iconIV.setBackgroundResource(mDrawableIds[position]);
            return view;
        }
    }
}
