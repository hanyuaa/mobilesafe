package com.itheima.mobilesafe.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SmsBackup {

    private static int index = 0;

    //备份短信
    public static void backup(Context context, String path, CallBack callBack) {
        FileOutputStream fos = null;
        Cursor cursor = null;

        try {
            //需要用到对象上下文环境,备份文件夹路径,进度条所在的对话框对象用于备份过程中进度的更新
            File file = new File(path);
            //获取内容解析器
            cursor = context.getContentResolver().query(Uri.parse("content://sms/"),
                    new String[]{"address", "date", "type", "body"},
                    null, null, null);
            if (cursor == null) {
                return;
            }
            //文件响应的输出流
            fos = new FileOutputStream(file);

            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "utf-8");
            serializer.startDocument("utf-8", true);
            serializer.startTag(null, "smss");

            //备份短信的总数 ,callbak非空判断
            if (callBack != null) {
                callBack.setMax(cursor.getCount());
            }

            while (cursor.moveToNext()) {
                serializer.startTag(null, "sms");

                serializer.startTag(null, "address");
                serializer.text(cursor.getString(0));
                serializer.endTag(null, "address");

                serializer.startTag(null, "date");
                serializer.text(cursor.getString(1));
                serializer.endTag(null, "date");

                serializer.startTag(null, "type");
                serializer.text(cursor.getString(2));
                serializer.endTag(null, "type");

                serializer.startTag(null, "body");
                serializer.text(cursor.getString(3));
                serializer.endTag(null, "body");

                serializer.endTag(null, "sms");

                //没循环一次就需要让进度条叠加
                index++;
                //睡眠, 让用户看到
                Thread.sleep(500);
                if (callBack != null) {
                    callBack.setProgress(index);
                }
            }

            serializer.endTag(null, "smss");
            serializer.endDocument();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && fos != null) {
                cursor.close();
                try {
                    fos.close();
                } catch (IOException e) {

                }
            }
        }
    }

    /*
        回调
        1,定义一个借口
        2,定义借口中为实现的业务逻辑方法
        3,传递一个实现了此借口的类的对象,接口的实现类,一定实现了方法
        4,获取传递过来的对象, 在合适的地方调用
     */
    public interface CallBack {
        public void setMax(int max);

        public void setProgress(int progress);
    }
}
