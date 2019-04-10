package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.dao.BlackNumberDao;
import com.itheima.mobilesafe.domain.BlackNumberInfo;
import com.itheima.mobilesafe.utils.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.List;

public class BlackNumberActivity extends Activity {

    private BlackNumberDao dao;
    private Handler mHandler = new MyHandler(this);
    private List<BlackNumberInfo> mBlcakNumberList;
    private ListView lvBlacknumber;
    private Button btnAdd;
    private MyAdapter myAdapter = new MyAdapter();
    private String mode = "1";
    private boolean mIsLoad = false;
    private int mCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacknumber);

        initUI();
        //从数据库获取黑名单
        initDate();
    }

    private void initDate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dao = BlackNumberDao.getInstance(getApplicationContext());
                mBlcakNumberList = dao.find(0);
                mCount = dao.getCount();

                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    private void initUI() {
        btnAdd = findViewById(R.id.btn_add);
        lvBlacknumber = findViewById(R.id.lv_blacknumber);


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        //监听其滚动状态
        lvBlacknumber.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //AbsListView.OnScrollListener.SCROLL_STATE_FLING //飞速滚动
                //AbsListView.OnScrollListener.SCROLL_STATE_IDLE //空闲状态
                //AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL //拿手触摸去滚动

                if (mBlcakNumberList != null) {
                    //条件一:滚动到停止状态
                    //条件二:最后一个条目可见(最后一个条目的索引值>=数据适配器中集合总条目-1)
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                            && lvBlacknumber.getLastVisiblePosition() >= mBlcakNumberList.size() - 1
                            && !mIsLoad) {
                        /*mIsLoad防止重复加载的变量
                          如果当前正在加载,mIsLoad就会为true,本次加载完毕后,再将mIsLoad改为false
                          如果下一次加载需要去执行的时候,会判断上次mIsLoad是否为false*/
                        if (mCount > mBlcakNumberList.size()) {
                            //加载下一页数据
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    dao = BlackNumberDao.getInstance(getApplicationContext());
                                    List<BlackNumberInfo> moreData = dao.find(mBlcakNumberList.size());
                                    //添加下一页数据
                                    mBlcakNumberList.addAll(moreData);
                                    mHandler.sendEmptyMessage(1);
                                }
                            }).start();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();

        View view = View.inflate(getApplicationContext(), R.layout.dialog_add_blacknumber, null);
        dialog.setView(view);

        final EditText etPhone = view.findViewById(R.id.et_phone);
        RadioGroup rgGroup = view.findViewById(R.id.rg_group);
        Button btnAdd = view.findViewById(R.id.btn_add);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        //监听其选中条目的切换过程
        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_sms:
                        mode = "1";
                        break;
                    case R.id.rb_phone:
                        mode = "2";
                        break;
                    case R.id.rb_all:
                        mode = "3";
                        break;
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etPhone.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)) {
                    //数据库插入
                    dao.insert(phone, mode);
                    //让数据和集合保持同步
                    BlackNumberInfo info = new BlackNumberInfo();
                    info.phone = phone;
                    info.mode = mode;
                    mBlcakNumberList.add(0, info);
                    //通知数据适配器刷新
                    if (myAdapter != null) {
                        myAdapter.notifyDataSetChanged();
                    }
                    //隐藏对话框
                    dialog.dismiss();
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入拦截号码");
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mBlcakNumberList.size();
        }

        @Override
        public BlackNumberInfo getItem(int position) {
            return mBlcakNumberList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //使用ViewHolder对ListView做优化
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);

                //将findViewById过程封装到convertView == null中去做
                holder = new ViewHolder();
                holder.tvPhone = convertView.findViewById(R.id.tv_phone);
                holder.tvMode = convertView.findViewById(R.id.tv_mode);
                holder.ivDelete = convertView.findViewById(R.id.iv_delete);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvPhone.setText(mBlcakNumberList.get(position).phone);

            switch (mBlcakNumberList.get(position).mode) {
                case "1":
                    holder.tvMode.setText("拦截短信");
                    break;
                case "2":
                    holder.tvMode.setText("拦截电话");
                    break;
                case "3":
                    holder.tvMode.setText("拦截全部");
                    break;
            }

            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //数据库删除
                    dao.delete(mBlcakNumberList.get(position).phone);
                    //集合中删除,通知数据适配器刷新
                    mBlcakNumberList.remove(position);
                    if (myAdapter != null) {
                        myAdapter.notifyDataSetChanged();
                    }
                }
            });

            return convertView;
        }
    }

    static class ViewHolder {
        TextView tvPhone;
        TextView tvMode;
        ImageView ivDelete;
    }

    static class MyHandler extends Handler {
        private WeakReference<BlackNumberActivity> reference;

        MyHandler(BlackNumberActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BlackNumberActivity activity = reference.get();
            super.handleMessage(msg);
            if (msg.what == 0) {
                activity.lvBlacknumber.setAdapter(activity.myAdapter);
            }

            //刷新
            if (msg.what == 1) {
                activity.myAdapter.notifyDataSetChanged();
            }
        }
    }
}
