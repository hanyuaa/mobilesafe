package com.itheima.mobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itheima.mobilesafe.db.BlackNumberOpenHelper;
import com.itheima.mobilesafe.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

public class BlackNumberDao {
    private static BlackNumberDao blackNumberDao;
    private BlackNumberOpenHelper openHelper;

    private static final String TABLE_NAME = "blacknumber";

    private BlackNumberDao(Context context) {
        openHelper = new BlackNumberOpenHelper(context);
    }

    public static BlackNumberDao getInstance(Context context) {
        if (blackNumberDao == null) {
            blackNumberDao = new BlackNumberDao(context);
        }
        return blackNumberDao;
    }

    /**
     * 添加
     */
    public void insert(String phone, String mode) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone", phone);
        values.put("mode", mode);
        db.insert(TABLE_NAME, null, values);

        db.close();
    }

    /**
     * 删除
     */
    public void delete(String phone) {
        SQLiteDatabase db = openHelper.getWritableDatabase();

        db.delete(TABLE_NAME, "phone = ?", new String[]{phone});

        db.close();
    }

    /**
     * 修改
     */
    public void update(String phone, String mode) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode", mode);
        db.update(TABLE_NAME, values, "phone = ?", new String[]{phone});

        db.close();
    }

    /**
     * 查询
     */
    public List<BlackNumberInfo> findAll() {
        SQLiteDatabase db = openHelper.getWritableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{"phone", "mode"},
                null, null, null, null,
                "_id desc");

        List<BlackNumberInfo> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            BlackNumberInfo info = new BlackNumberInfo();
            info.phone = cursor.getString(0);
            info.mode = cursor.getString(1);
            list.add(info);
        }
        cursor.close();
        db.close();

        return list;
    }

    /**
     * 查询
     */
    public List<BlackNumberInfo> find(int index) {
        SQLiteDatabase db = openHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "select phone,mode from blacknumber order by _id desc limit ?, 20;", new String[]{index + ""}
        );

        List<BlackNumberInfo> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            BlackNumberInfo info = new BlackNumberInfo();
            info.phone = cursor.getString(0);
            info.mode = cursor.getString(1);
            list.add(info);
        }
        cursor.close();
        db.close();

        return list;
    }


    /**
     * 获取总条数
     */
    public int getCount() {
        int count = 0;
        SQLiteDatabase db = openHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "select count(*) from blacknumber;", null
        );
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();

        return count;
    }

    /**
     * 获取总条数
     */
    public int getMode(String phone) {
        int mode = 0;
        SQLiteDatabase db = openHelper.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{"mode"},"phone = ?",new String[]{phone},null,null,null);
        if (cursor.moveToNext()) {
            mode = cursor.getInt(0);
        }
        cursor.close();
        db.close();

        return mode;
    }
}
