package com.itheima.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class ServiceUtil {

    private static ActivityManager mActivityManager;

    /**
     * 判断服务是否运行
     *
     * @param serviceName
     * @return
     */
    public static boolean isRunning(Context context, String serviceName) {
        //获取activityManager管理者对象, 可以去获取当前手机正在运行的所有服务
        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取手机中正在运行的服务
        List<ActivityManager.RunningServiceInfo> services = mActivityManager.getRunningServices(100);
        //遍历获取的所有服务集合,拿到每一个服务的类的名称,和传递进来的做比对
        for (ActivityManager.RunningServiceInfo service : services) {
            //获取每一个正在运行服务的名称
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
