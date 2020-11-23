package com.zt.school.utils;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * Created by 九七
 * app信息工具类
 */
public class AppInfoManagerUtils {
    private PackageManager manager; //包管理器
    private PackageInfo info;   //包信息
    private Context context;
    public AppInfoManagerUtils(Context context){
        this.context = context;
        manager = context.getPackageManager();
        try {
            info = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取app Logo
     */
    public Drawable getAppLogo(){
        Drawable logo;
        try {
            logo = manager.getApplicationIcon(context.getPackageName());
            return logo;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取app名称
     */
    public String getAppName(){
        String appName = null;
        appName = (String) manager.getApplicationLabel(info.applicationInfo);
        return appName;
    }
    /**
     * 获取app版本号
     */
    public  int getAppVersionCode(){
        return info.versionCode;
    }
    /**
     * 获取app版本名称
     */
    public String getAppVersionName(){
        return info.versionName;
    }
    /**
     * 判断app版本是否升级
     */
    public  boolean isUpGrade(int newVersionCode){
        return newVersionCode > getAppVersionCode() ? true : false;
    }
}
