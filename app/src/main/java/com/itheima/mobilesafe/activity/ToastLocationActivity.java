package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.SPUtil;
import com.itheima.mobilesafe.utils.ToastUtil;

public class ToastLocationActivity extends Activity {

    private ImageView ivDrag;
    private Button btnBotto;
    private Button btnTop;
    private WindowManager mWM;
    private int screenHeight;
    private int screenWidth;
    private long startTime;
    private long[] mHis = new long[2];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_location);

        initUI();
    }

    private void initUI() {
        //可拖拽双击居中的图片空间
        ivDrag = findViewById(R.id.iv_drag);
        btnTop = findViewById(R.id.btn_top);
        btnBotto = findViewById(R.id.btn_bottom);

        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        screenHeight = mWM.getDefaultDisplay().getHeight();
        screenWidth = mWM.getDefaultDisplay().getWidth();

        int locationX = SPUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_X, ivDrag.getLeft());
        int locationY = SPUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, ivDrag.getTop());

        //Relativ
        //左上角坐标作用在ivDrag上
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        //将左上角的坐标作用在ivDrag对应规则参数上
        params.leftMargin = locationX;
        params.topMargin = locationY;
        //将以上规则作用在ivDrag上
        ivDrag.setLayoutParams(params);
        if (locationY > screenHeight / 2) {
            btnTop.setVisibility(View.VISIBLE);
            btnBotto.setVisibility(View.INVISIBLE);
        } else {
            btnTop.setVisibility(View.INVISIBLE);
            btnBotto.setVisibility(View.VISIBLE);
        }

        //监听某一个空间的拖拽过程(按下, 移动, 抬起)
        ivDrag.setOnTouchListener(new View.OnTouchListener() {

            private int starY;
            private int starX;

            //对不同的事件做不同的逻辑处理
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

                        //当前控件所在屏幕左,上角的位置
                        int left = ivDrag.getLeft();
                        int top = ivDrag.getTop();
                        int right = ivDrag.getRight();
                        int bottom = ivDrag.getBottom();
                        //容错处理(ivDrag不能拖拽出手机屏幕)
                        //左边缘不能超出屏幕
                        if (left + disX < 0) {
                            return true;
                        }
                        //有边缘不能超出屏幕
                        if (right + disX > screenWidth) {
                            return true;
                        }
                        //上边缘不能超出屏幕
                        if (top + disY < 0) {
                            return true;
                        }

                        //下边缘(屏幕的高度-22 = 低边缘显示最大值)
                        if (bottom + disY > screenHeight - 22) {
                            return true;
                        }

                        if (top + disY > screenHeight / 2) {
                            btnTop.setVisibility(View.VISIBLE);
                            btnBotto.setVisibility(View.INVISIBLE);
                        } else {
                            btnTop.setVisibility(View.INVISIBLE);
                            btnBotto.setVisibility(View.VISIBLE);
                        }

                        //告知移动的控件,按计算出来的坐标做展示
                        ivDrag.layout(left + disX, top + disY, right + disX, bottom + disY);

                        //重置一次起始坐标
                        starX = (int) event.getRawX();
                        starY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        //存储移动到的位置
                        SPUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_X, ivDrag.getLeft());
                        SPUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, ivDrag.getTop());
                        break;
                }
                //在当前的情况下返回false不响应移动事件
                //既要响应点击事件,又要响应拖拽事件, 此返回值结果需要返回为false
                return false;
            }
        });

        //设置双击居中的事件
        ivDrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.arraycopy(mHis, 1, mHis, 0, mHis.length - 1);
                mHis[mHis.length - 1] = SystemClock.uptimeMillis();

                if (mHis[mHis.length - 1] - mHis[0] <= 500) {
                    ToastUtil.show(getApplicationContext(), "点了");

                    int left = screenWidth / 2 - ivDrag.getWidth() / 2;
                    int top = screenHeight / 2 - ivDrag.getHeight() / 2;
                    int right = screenWidth / 2 + ivDrag.getWidth() / 2;
                    int bottom = screenHeight / 2 + ivDrag.getHeight() / 2;

                    ivDrag.layout(left, top, right, bottom);

                    SPUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_X, ivDrag.getLeft());
                    SPUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, ivDrag.getTop());

                }
            }
        });
    }
}
