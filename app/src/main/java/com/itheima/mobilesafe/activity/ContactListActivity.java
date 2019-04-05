package com.itheima.mobilesafe.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactListActivity extends Activity {

    private MyHandler mHandler = new MyHandler(this);
    private MyAdapter myAdapter;
    private List<HashMap<String, String>> contactList = new ArrayList<>();

    private static final int CONTACT_OK = 100; //联系人状态吗
    private ListView lvContact;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        initUI();
        initDate();
    }

    /**
     * 获取系统联系人
     */
    private void initDate() {
        //读入联系人可能是一个耗时操作,放置到子线程中处理
        new Thread(new Runnable() {
            @Override
            public void run() {
                //动态获取权限
                boolean permission = checkReadPermission(Manifest.permission.READ_CONTACTS, 111);
                if (permission) {
                    //获取内容解析器对象
                    ContentResolver resolver = getContentResolver();
                    //查询联系人数据库表过程
                    Cursor cursor = resolver.query(
                            Uri.parse("content://com.android.contacts/raw_contacts"),
                            new String[]{"contact_id"},
                            null,
                            null,
                            null);
                    contactList.clear();
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            String id = cursor.getString(0);
                            Cursor indexCursor = resolver.query(Uri.parse("content://com.android.contacts/data"),
                                    new String[]{"data1", "mimetype"},
                                    "raw_contact_id=?",
                                    new String[]{id},
                                    null);
                            if (indexCursor != null) {
                                HashMap<String, String> map = new HashMap<>();
                                while (indexCursor.moveToNext()) {
                                    /*
                                    110:vnd.android.cursor.item/phone_v2
                                    zhangsan:vnd.android.cursor.item/name
                                     */
                                    String data = indexCursor.getString(0);
                                    String mimetype = indexCursor.getString(1);
                                    if (!TextUtils.isEmpty(data)) {
                                        if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
                                            map.put("phone", data);
                                        } else if (mimetype.equals("vnd.android.cursor.item/name")) {
                                            map.put("name", data);
                                        }
                                    }
                                }
                                contactList.add(map);
                                indexCursor.close();
                            }
                        }
                        cursor.close();
                        //消息机制
                        mHandler.sendEmptyMessage(CONTACT_OK);
                    }
                }
            }
        }).start();
    }

    private void initUI() {
        lvContact = findViewById(R.id.lv_contact);
        //设置点击事件
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取点中条目索引指向集合中的对象
                if (myAdapter != null) {
                    HashMap<String, String> map = myAdapter.getItem(position);
                    //获取当前条目指向集合对应的电话号码
                    String phone = map.get("phone");
                    //此电话号码需要给第三个导航界面使用
                    Intent intent = new Intent();
                    intent.putExtra("phone", phone);

                    setResult(0, intent);
                    finish();
                }
            }
        });
    }

    private static class MyHandler extends Handler {
        WeakReference<ContactListActivity> mWeakReference;
        ContactListActivity contactListActivity;

        MyHandler(ContactListActivity activity) {
            mWeakReference = new WeakReference<>(activity);
            contactListActivity = mWeakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (contactListActivity != null) {
                switch (msg.what) {
                    case CONTACT_OK: //联系人准备完成,显示
                        contactListActivity.showContact();
                        break;
                }
            }
        }
    }

    private void showContact() {
        if (myAdapter == null) {
            myAdapter = new MyAdapter();
        }
        lvContact.setAdapter(myAdapter);
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return contactList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.listview_contact_item, null);
            TextView tvName = view.findViewById(R.id.tv_name);
            TextView tvPhone = view.findViewById(R.id.tv_phone);

            tvName.setText(getItem(position).get("name"));
            tvPhone.setText(getItem(position).get("phone"));
            return view;
        }
    }

    /**
     * 判断是否有某项权限
     *
     * @param string_permission 权限
     * @param request_code      请求码
     * @return
     */
    public boolean checkReadPermission(String string_permission, int request_code) {
        boolean flag = false;
        if (ContextCompat.checkSelfPermission(this, string_permission) == PackageManager.PERMISSION_GRANTED) {//已有权限
            flag = true;
        } else {//申请权限
            ActivityCompat.requestPermissions(this, new String[]{string_permission}, request_code);
        }
        return flag;
    }

    /**
     * 检查权限后的回调
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1111: //拨打电话
                if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {//失败
                    Toast.makeText(this, "请允许拨号权限后再试", Toast.LENGTH_SHORT).show();
                } else {//成功
                    //call("tel:"+"10086");
                }
                break;
        }
    }
}
