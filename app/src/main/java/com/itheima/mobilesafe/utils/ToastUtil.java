package com.itheima.mobilesafe.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    /**
     * @param context
     * @param msg
     */
    public static void show(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
