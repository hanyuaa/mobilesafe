package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.domain.AppInfo;
import com.itheima.mobilesafe.engine.AppInfoProvider;
import com.itheima.mobilesafe.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AppManagerActivity extends Activity implements View.OnClickListener {

    private List<AppInfo> mAppInfoList;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                myAdapter = new MyAdapter();
                lvAppList.setAdapter(myAdapter);

                if (tvDes != null && mCustomerList != null) {
                    tvDes.setText("12312");

                }
            }
        }
    };
    private ListView lvAppList;
    private MyAdapter myAdapter;
    private List<AppInfo> mCustomerList;
    private List<AppInfo> mSystemList;
    private TextView tvDes;
    private AppInfo mAppInfo;
    private PopupWindow popupWindow;

    class MyAdapter extends BaseAdapter {

        //获取数据适配器中条目类型的总数,修改成两种(纯文本, 图片+文字)
        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        //指定索引指向的条目类型,条目类型状态码指定(0复用系统,1)
        @Override
        public int getItemViewType(int position) {
            if (position == 0
                    || position == mCustomerList.size() + 1) {
                //返回0,代表纯文本的状态码
                return super.getItemViewType(position);
            } else {
                //返回1,代表图片+文字
                return 1;
            }
        }

        @Override
        public int getCount() {
            return mSystemList.size() + mCustomerList.size();
        }

        @Override
        public AppInfo getItem(int position) {
            if (position == 0 ||
                    position == mCustomerList.size() + 1) {
                return null;
            }
            if (position < mCustomerList.size() + 1) {
                return mCustomerList.get(position - 1);
            } else {
                //返回系统应用赌赢条目的对象
                return mSystemList.get(position - mCustomerList.size() - 2);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int itemViewType = getItemViewType(position);

            if (itemViewType == 0) {
                //展示灰色纯文本条目
                ViewTitleHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_app_item_title, null);
                    holder = new ViewTitleHolder();
                    holder.tvAppTitle = convertView.findViewById(R.id.tv_app_title);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewTitleHolder) convertView.getTag();
                }
                if (position == 0) {
                    holder.tvAppTitle.setText(String.format(Locale.CHINA, "用户应用1(%d)", mCustomerList.size()));
                } else {
                    holder.tvAppTitle.setText(String.format(Locale.CHINA, "系统应用1(%d)", mSystemList.size()));
                }

                return convertView;
            } else {
                //展示图片+文字条目
                ViewHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_app_item, null);
                    holder = new ViewHolder();
                    holder.ivIcon = convertView.findViewById(R.id.iv_icon);
                    holder.tvName = convertView.findViewById(R.id.tv_name);
                    holder.tvPath = convertView.findViewById(R.id.tv_path);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.ivIcon.setBackground(getItem(position).icon);
                holder.tvName.setText(getItem(position).name);
                holder.tvPath.setText(getItem(position).isSdCard ? "sd卡应用" : "系统应用");

                return convertView;
            }
        }
    }

    static class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvPath;
    }

    static class ViewTitleHolder {
        TextView tvAppTitle;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        initTitle();
        initAppList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                //划分为系统应用和非系统应用
                mSystemList = new ArrayList<>();
                mCustomerList = new ArrayList<>();
                for (AppInfo appInfo : mAppInfoList) {
                    if (appInfo.isSystem) {
                        //系统应用
                        mSystemList.add(appInfo);
                    } else {
                        //非系统应用
                        mCustomerList.add(appInfo);
                    }
                }


                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    private void initAppList() {
        lvAppList = findViewById(R.id.iv_app_list);
        tvDes = findViewById(R.id.tv_des);

        lvAppList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //滚动过程中调用方法
                if (mCustomerList != null && mSystemList != null) {
                    if (firstVisibleItem >= mCustomerList.size() + 1) {
                        //滚动 到了系统条目
                        tvDes.setText(String.format(Locale.CHINA, "系统应用2(%d)", mSystemList.size()));
                    } else {
                        //滚动到应用条目
                        tvDes.setText(String.format(Locale.CHINA, "用户应用2(%d)", mCustomerList.size()));
                    }
                }
            }
        });

        lvAppList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 ||
                        position == mCustomerList.size() + 1) {
                    return;
                }
                if (position < mCustomerList.size() + 1) {
                    mAppInfo = mCustomerList.get(position - 1);
                } else {
                    mAppInfo = mSystemList.get(position - mCustomerList.size() - 2);
                }

                showPopupWindow(view);
            }
        });
    }

    private void showPopupWindow(View v) {
        View view = View.inflate(this, R.layout.popupwindow_layout, null);

        TextView tvUninstall = view.findViewById(R.id.tv_uninstall);
        TextView tvStart = view.findViewById(R.id.tv_start);
        TextView tvShare = view.findViewById(R.id.tv_share);

        tvUninstall.setOnClickListener(this);
        tvStart.setOnClickListener(this);
        tvShare.setOnClickListener(this);

        //透明动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(800);
        alphaAnimation.setFillAfter(true);
        //缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5F);
        scaleAnimation.setDuration(800);
        scaleAnimation.setFillAfter(true);
        //动画集合
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);

        //创建窗体对象,指定宽高
        popupWindow = new PopupWindow(view,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        //设置一个透明背景
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        //指定窗体的位置
        popupWindow.showAsDropDown(v, 50, -v.getHeight());

        view.startAnimation(animationSet);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_uninstall:
               /* if (mAppInfo.isSystem) {
                    ToastUtil.show(getApplicationContext(), "此应用不能卸载");
                } else {*/
                    Intent intent = new Intent(Intent.ACTION_DELETE);
                    intent.setData(Uri.parse("package:" + mAppInfo.getPackageName()));
                    startActivity(intent);
                //}
                break;
            case R.id.tv_start:
                //通过桌面去启动
                PackageManager pm = getPackageManager();
                Intent intent2 = pm.getLaunchIntentForPackage(mAppInfo.packageName);
                //if (intent != null) {
                    startActivity(intent2);
                /*} else {
                    ToastUtil.show(getApplicationContext(), "此应用不能被开启");
                }*/
                break;
            case R.id.tv_share:
                Intent intent1 = new Intent(Intent.ACTION_SEND);
                intent1.putExtra(Intent.EXTRA_TEXT, "发送分享短信");
                intent1.setType("text/plain");
                startActivity(intent1);
                break;
        }

        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    private void initTitle() {
        //获取磁盘可用大小
        String path = Environment.getDataDirectory().getAbsolutePath();
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //获取文件夹可用大小
        String memoryAvailSpace = Formatter.formatFileSize(this, getAvailSpace(path));
        String sdAvailSpace = Formatter.formatFileSize(this, getAvailSpace(sdPath));

        TextView tvMemory = findViewById(R.id.tv_memory);
        TextView tvSDMemory = findViewById(R.id.tv_sd_memory);

        tvMemory.setText(String.format("磁盘可用:%s", memoryAvailSpace));
        tvSDMemory.setText(String.format("sd卡可用:%s", sdAvailSpace));
    }

    private long getAvailSpace(String path) {
        //获取可用磁盘大小
        StatFs statFs = new StatFs(path);
        long blocksLong = statFs.getAvailableBlocksLong();
        long blockSize = statFs.getBlockSizeLong();

        return blockSize * blocksLong;
    }
}
