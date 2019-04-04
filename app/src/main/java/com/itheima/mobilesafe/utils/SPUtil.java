package com.itheima.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {

    private static SharedPreferences sp;

    /**
     * 写 boolean
     */
    public static void putBoolean(Context context, String key, boolean value) {
        //存储节点文件的名称
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).apply();
    }

    /**
     * 读 boolean
     */
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, defValue);
    }

    /**
     * 写 string
     */
    public static void putString(Context context, String key, String value) {
        //存储节点文件的名称
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, value).apply();
    }

    /**
     * 读 string
     */
    public static String getString(Context context, String key, String defValue) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getString(key, defValue);
    }
}
