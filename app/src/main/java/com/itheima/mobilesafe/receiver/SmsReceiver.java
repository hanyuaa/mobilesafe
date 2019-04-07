package com.itheima.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.service.LocationService;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.SPUtil;

import java.io.IOException;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //判断是否开启了防盗保护
        boolean openSecurity = SPUtil.getBoolean(context, ConstantValue.OPEN_SECURITY, false);
        if (openSecurity) {
            //获取短信内容
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            if (objects != null) {
                for (Object object : objects) {
                    //获取消息对象
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
                    String address = smsMessage.getOriginatingAddress();
                    String body = smsMessage.getMessageBody();

                    //判断是否包含了播放音乐的关键字
                    if (body.contains("#*alarm*#")) {
                        try {
                            //播放音乐(准备音乐)
                            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
                            mediaPlayer.setLooping(true); //循环播放
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    //判断是否包含了位置的关键字
                    if (body.contains("#*location*#")) {
                        Intent intent1 = new Intent(context, LocationService.class);
                        context.startService(intent1);
                    }
                }
            }
        }
    }
}
