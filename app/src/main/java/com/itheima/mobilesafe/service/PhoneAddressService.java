package com.itheima.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
    private int screenHeight;
    private int screenWidth;
    private InnerOutGoingCallReceiver outGoingCallReceiver;

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
        screenHeight = mWindowManager.getDefaultDisplay().getHeight();
        screenWidth = mWindowManager.getDefaultDisplay().getWidth();

        //去电归属地查询
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);

        outGoingCallReceiver = new InnerOutGoingCallReceiver();
        registerReceiver(outGoingCallReceiver,filter);
    }

    class InnerOutGoingCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取播出电话号码的字符串
            String phone = getResultData();
            showToast(phone);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消服务,取消监听状态
        if (myPhoneStateListener != null) {
            mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE); //不再监听
        }

        if (outGoingCallReceiver != null) {
            unregisterReceiver(outGoingCallReceiver);
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

        mViewToast.setOnTouchListener(new View.OnTouchListener() {
            private int starY;
            private int starX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //int starX = (int) event.getX();
                        starX = (int) event.getRawX();
                        starY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();

                        int disX = moveX - starX;
                        int disY = moveY - starY;

                        params.x = starX + disX;
                        params.y = starY + disY;
                        //告知窗体吐司需要按照手势的移动去做位置的更新
                        mWindowManager.updateViewLayout(mViewToast, params);
                        //容错处理(ivDrag不能拖拽出手机屏幕)
                        //左边缘不能超出屏幕
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        //有边缘不能超出屏幕
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        //上边缘不能超出屏幕
                        if (params.x > screenWidth - mViewToast.getWidth()) {
                            params.x = screenWidth - mViewToast.getWidth();
                        }

                        //下边缘(屏幕的高度-22 = 低边缘显示最大值)
                        if (params.y > screenHeight - mViewToast.getHeight() - 22) {
                            params.y = screenHeight - mViewToast.getHeight() - 22;
                        }

                        //重置一次起始坐标
                        starX = (int) event.getRawX();
                        starY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        //存储移动到的位置
                        SPUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_X, params.x);
                        SPUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, params.y);
                        break;
                }
                //在当前的情况下返回false不响应移动事件
                //既要响应点击事件,又要响应拖拽事件, 此返回值结果需要返回为false
                return true;
            }
        });


        //读入sp中存储吐司位置的坐标值
        params.x = SPUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_X, 0);
        params.y = SPUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, 0);

        //从sp中获取色值文字的索引,匹配图片
        drawableIds = new int[]{R.drawable.call_locate_white,
                R.drawable.call_locate_orange,
                R.drawable.call_locate_blue,
                R.drawable.call_locate_gray,
                R.drawable.call_locate_green};
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
