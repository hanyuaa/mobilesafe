package com.itheima.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Debug;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.domain.ProcessInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProcessProvider {

    /**
     * 获取进程总数
     */
    public static int getProcessCount(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();

        if (processes != null) {
            return processes.size();
        }

        return 0;
    }

    /**
     * 获取进可用内存数
     */
    public static long getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

        //给memoryInfo对象赋值
        am.getMemoryInfo(memoryInfo);
        //获取memoryInfo中相应可用内存大小
        return memoryInfo.availMem;
    }

    /**
     * 获取全部内存数
     */
    public static long getTotalMemory(Context context) {
        //只能用于4.0以上版本
        /*ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

        //给memoryInfo对象赋值
        am.getMemoryInfo(memoryInfo);
        //获取memoryInfo中相应可用内存大小
        return memoryInfo.totalMem;*/

        //读入Proc/meminfo文件,滴入第一行  MemTotal:  1016396 kB
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader("proc/meminfo");
            bufferedReader = new BufferedReader(fileReader);
            String lineOne = bufferedReader.readLine();
            //将字符串转换成字符数组
            char[] chars = lineOne.toCharArray();
            StringBuffer buffer = new StringBuffer();
            for (char aChar : chars) {
                if (aChar >= '0' && aChar <= '9') {
                    buffer.append(aChar);
                }
            }

            return Long.parseLong(buffer.toString()) * 1024;
        } catch (Exception e) {

        } finally {
            if (fileReader != null && bufferedReader != null) {
                try {
                    fileReader.close();
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return 0;
    }

    /**
     * 获取进程信息
     */
    public static List<ProcessInfo> getProcessInfo(Context context) {
        List<ProcessInfo> result = new ArrayList<>();
        //获取ActivityManager
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        //获取正在运行的进程集合
        List<ActivityManager.RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            ProcessInfo processInfo = new ProcessInfo();
            //进程名称 = 应用的包名
            processInfo.packageName = appProcess.processName;
            //获取使用内存大小
            Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(new int[]{appProcess.pid});
            Debug.MemoryInfo memoryInfo1 = memoryInfo[0];
            processInfo.memSize = memoryInfo1.getTotalPrivateDirty() * 1024;
            //获取应用名称
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.packageName, 0);
                processInfo.name = applicationInfo.loadLabel(pm).toString();
                processInfo.icon = applicationInfo.loadIcon(pm);

                //判断是否为系统进程
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    processInfo.isSystem = true;
                } else {
                    processInfo.isSystem = false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                //异常处理
                processInfo.name = appProcess.processName;
                processInfo.icon = context.getResources().getDrawable(R.mipmap.ic_launcher);
                processInfo.isSystem = true;
            }

            result.add(processInfo);
        }

        return result;
    }
}
