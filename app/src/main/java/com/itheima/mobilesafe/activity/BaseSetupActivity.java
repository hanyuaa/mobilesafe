package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSetupActivity extends Activity {
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //创建手势管理对象,用作管理在onTouchEvent(event)传递过来的手势动作
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //监听手势的移动
                if (e1.getX() - e2.getX() > 0) {
                    //由右向左, 移动到下一页
                    showNextPage();
                }

                if (e1.getX() - e2.getX() < 0) {
                    //由左向右, 移动到上一页
                    showPrePage();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    public abstract void showNextPage();

    public abstract void showPrePage();

    public void nextPage(View v) {
        showNextPage();
    }

    public void prePage(View v) {
        showPrePage();
    }

    //监听屏幕响应事件(按下,移动,抬起)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //通过手势处理类,接收多种类型的事件, 用作处理
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
