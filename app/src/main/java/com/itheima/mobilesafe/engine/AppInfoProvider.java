package com.itheima.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.itheima.mobilesafe.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class AppInfoProvider {

    /**
     * 返回当前手机所有的应用的相应信息(名称,包名,图标 内存/sd卡,系统/用户)
     */
    public static List<AppInfo> getAppInfoList(Context context) {
        List<AppInfo> appInfos = new ArrayList<>();
        //包的管理者对象
        PackageManager pm = context.getPackageManager();
        //获取安装在手机上的应用的集合
        List<PackageInfo> infoList = pm.getInstalledPackages(0);
        //循环遍历应用信息的集合
        for (PackageInfo packageInfo : infoList) {
            AppInfo app = new AppInfo();
            //获取应用的包名
            app.packageName = packageInfo.packageName;
            //应用名称
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            app.name = applicationInfo.loadLabel(pm).toString();
            //图标
            app.icon = applicationInfo.loadIcon(pm);
            //判断是否是系统应用
            // & 两个数转换成二进制,相同位都为1,则为1
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                //系统应用
                app.isSystem = true;
            } else {
                //非系统应用
                app.isSystem = false;
            }
            //是否为sd卡中安装应用
            app.isSdCard = (applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE;

            appInfos.add(app);
        }
        return appInfos;
    }
}
