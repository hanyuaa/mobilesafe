package com.itheima.mobilesafe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.engine.AddressDao;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.SPUtil;

import java.lang.ref.WeakReference;

public class PhoneAddressService extends Service {

    private TelephonyManager mTM;
    private MyPhoneStateListener myPhoneStateListener;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    final Handler mHandler = new MyHandler(this);
    private View mViewToast;
    private WindowManager mWindowManager;
    private String address;
    private TextView tv;
    private int[] drawableIds;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //服务开始的时候去监听,关闭的时候不需要监听
        //第一次开启服务后,就需要去管理吐司的显示
        mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        myPhoneStateListener = new MyPhoneStateListener();
        mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        //获取窗体对象
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消服务,取消监听状态
        if (myPhoneStateListener != null) {
            mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE); //不再监听
        }
    }

    class MyPhoneStateListener extends PhoneStateListener {
        //重写电话状态发生改变会触发的方法
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    //空闲状态
                    //取消窗体
                    if (mWindowManager != null && mViewToast != null) {
                        mWindowManager.removeView(mViewToast);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //响铃
                    showToast(phoneNumber);
                    break;
            }
        }
    }

    private void showToast(String phoneNumber) {
        // XXX This should be changed to use a Dialog, with a Theme.Toast
        // defined that sets up the layout params appropriately.
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        //在响铃的时候显示
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.TOP + Gravity.START;

        //将Toast挂载到windowManager窗体上
        LayoutInflater inflate = (LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewToast = inflate.inflate(R.layout.toast_view, null);
        tv = mViewToast.findViewById(R.id.tv_toast);

        //从sp中获取色值文字的索引,匹配图片
        drawableIds = new int[]{R.drawable.call_locate_white,
                R.drawable.call_locate_orange,
                R.drawable.call_locate_blue,
                R.drawable.call_locate_gray, R.drawable.call_locate_green};
        int idx = SPUtil.getInt(this, ConstantValue.TOAST_STYLE, 0);
        tv.setBackgroundResource(drawableIds[idx]);

        //在窗体上挂载view需要权限
        mWindowManager.addView(mViewToast, params);

        //查询来电号码
        query(phoneNumber);
    }

    private void query(final String phoneNumber) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                address = AddressDao.getAddress(phoneNumber);

                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    static final class MyHandler extends Handler {
        WeakReference<PhoneAddressService> mWeakReference;
        PhoneAddressService phoneAddressService;

        MyHandler(PhoneAddressService activity) {
            mWeakReference = new WeakReference<>(activity);
            phoneAddressService = mWeakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (phoneAddressService != null) {
                switch (msg.what) {
                    case 0:
                        phoneAddressService.tv.setText(phoneAddressService.address);
                        break;
                }
            }
        }
    }
}
