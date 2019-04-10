package com.itheima.mobilesafe.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 归属地查询
 */
public class AddressDao {
    //指定访问数据库路径
    private static final String path = "data/user/0/com.itheima.mobilesafe/files/address.db";

    //开启数据库连接, 进行访问
    public static String getAddress(String phone) {

        if (phone.length() >= 7) {
            phone = phone.substring(0, 7);
        }
        //开启数据库连接
        SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        //数据库查询
        Cursor cursor = database.query("data1",
                new String[]{"outkey"},
                "id = ?",
                new String[]{phone},
                null,
                null,
                null);

        if (cursor.moveToNext()) {
            String outKey = cursor.getString(0);
        }

        cursor.close();

        return "查询归属地";
    }
}
