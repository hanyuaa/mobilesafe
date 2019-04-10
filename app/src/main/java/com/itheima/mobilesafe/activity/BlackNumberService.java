package com.itheima.mobilesafe.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.itheima.mobilesafe.dao.BlackNumberDao;

import java.lang.reflect.Method;

public class BlackNumberService extends Service {

    private InnerSmsReceiver receiver;
    private BlackNumberDao dao;
    private MyPhoneStateListener myPhoneStateListener;
    private TelephonyManager mTM;
    private MyContentObserver myContentObserver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        dao = BlackNumberDao.getInstance(this);
        //拦截短信
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(1000);

        receiver = new InnerSmsReceiver();
        registerReceiver(receiver, filter);

        //监听电话状态
        mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        myPhoneStateListener = new MyPhoneStateListener();
        mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    class MyPhoneStateListener extends PhoneStateListener {
        //重写电话状态发生改变会触发的方法
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    //空闲状态

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //响铃
                    endCall(phoneNumber);
                    break;
            }
        }
    }

    private void endCall(String phone) {
        try {
            int mode = dao.getMode(phone);
            if (mode == 2 || mode == 3) {
                Class<?> clazz = Class.forName("android.os.ServiceManager");
                Method method = clazz.getMethod("getService", String.class);
                IBinder iBinder = (IBinder) method.invoke(clazz, "phone");
                ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
                iTelephony.endCall();

                //getContentResolver().delete(Uri.parse("content://call_log/calls"),"number = ?",new String[]{phone});
                //通过内容观察者,观察数据库的变化
                myContentObserver = new MyContentObserver(new Handler(), phone);
                getContentResolver().registerContentObserver(
                        Uri.parse("content://call_log/calls"), true, myContentObserver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MyContentObserver extends ContentObserver {

        private String phone;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler, String phone) {
            super(handler);
            this.phone = phone;
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            //插入一条数据后再删除
            getContentResolver().delete(Uri.parse("content://call_log/calls"), "number = ?", new String[]{phone});
        }
    }

    class InnerSmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取短信内容
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            if (objects != null) {
                for (Object object : objects) {
                    //获取消息对象
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
                    String address = smsMessage.getOriginatingAddress();

                    int mode = dao.getMode(address);

                    if (mode == 1 || mode == 3) {
                        //中断广播
                        abortBroadcast();
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (receiver != null) {
            unregisterReceiver(receiver);
        }

        if (myContentObserver != null) {
            getContentResolver().unregisterContentObserver(myContentObserver);
        }

        //取消对电话状态的监听
        if (myPhoneStateListener != null) {
            mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }
}
