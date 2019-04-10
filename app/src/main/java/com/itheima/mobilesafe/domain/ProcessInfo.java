package com.itheima.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class ProcessInfo {

    public String name; //名称
    public Drawable icon; //图标
    public long memSize; //使用的内存数
    public boolean isCheck; //是否被选中
    public boolean isSystem; //是否是系统进程
    public String packageName; //如果进程没有名称,则将其包名作为名称

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public long getMemSize() {
        return memSize;
    }

    public void setMemSize(long memSize) {
        this.memSize = memSize;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
