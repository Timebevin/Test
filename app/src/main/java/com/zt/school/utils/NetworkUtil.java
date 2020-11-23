package com.zt.school.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by 九七
 *  网络检查工具类
 *  http://blog.csdn.net/csdn576038874/article/details/51460180
 */

public class NetworkUtil {
    /**
     * 没有网络
     */
    public static final int NONETWORK = 0;
    /**
     * 当前是wifi连接
     */
    public static final int WIFI = 1;
    /**
     * 不是wifi连接
     */
    public static final int NOWIFI = 2;

    /**
     * 检测当前网络的类型 是否是wifi
     */
    public static int checkedNetWorkType(Context context){
        if(!checkedNetWork(context)){
            return NONETWORK;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting() ){
            return WIFI;
        }else{
            return NOWIFI;
        }
    }

    /**
     * 检查是否连接网络
     */
    public static boolean  checkedNetWork(Context context){
        // 1.获得连接设备管理器
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm == null) return false;
        /**
         * 获取网络连接对象
         */
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if(networkInfo == null || !networkInfo.isAvailable()){
            return false;
        }
        return true;
    }
}