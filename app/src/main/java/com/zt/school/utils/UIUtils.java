package com.zt.school.utils;

import android.content.Context;
import android.os.Handler;
import android.view.View;

import com.zt.school.confige.MyApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: 九七
 */
public class UIUtils {
    public static Context getContext(){
        return MyApplication.getContext();
    }
    public static Handler getHandler(){
        return MyApplication.getHandler();
    }
    // 根据布局id返回一个view布局
    public static View inflater(int layoutId){

        return View.inflate(getContext(), layoutId, null);
    }
    public static float px2dp(int px){
        float density = getContext().getResources().getDisplayMetrics().density;//设备密度
        float dp = px / density;
        return dp;
    }
    //px像素适配不同屏幕的手机分辨率
    public static int dp2px(float dp){
        float density = getContext().getResources().getDisplayMetrics().density;
        int px = (int) (dp * density+0.5f);
        return px;
    }
    //判断当前线程是否运行在主线程
    public static boolean isRunMainThread(){
        int currentThread = android.os.Process.myTid();
        if(currentThread==MyApplication.getMainThreadId()){
            return true;
        }
        return false;
    }
    //将子线程运行在UI线程
    public static void runOnMainThread(Runnable runnable){
        if(isRunMainThread()){
            runnable.run();//回调其run方法执行操作
        }else {
            getHandler().post(runnable); //若为子线程，则调用post方法运行在UI线程
        }
    }
    //获取字符串数组
    public static String[] getStringArray(int resId){
        String[] stringArray = getContext().getResources().getStringArray(resId);
        return stringArray;
    }
    /**
     * 获取状态栏高度 主要用于沉浸式自动顶出高度padding 在不设置颜色的时候需要
     */
    public static  int getStateBarHeight() {
        // 获得状态栏高度
        int resourceId = UIUtils.getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        return UIUtils.getContext().getResources().getDimensionPixelSize(resourceId);
    }

    /*
    * 将时间戳转换为时间
    * timeStamp：时间戳
    * timeFormat：转化成的时间格式
    */
    public static String timeStampToTime(String timeStamp, String timeFormat){
        SimpleDateFormat sdr = new SimpleDateFormat(timeFormat);
        int i = Integer.parseInt(timeStamp);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }

    /**
     * 获取当前系统的时间戳
     */
    public static String getCurrentSysTimeStamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String data = sdf.format(new Date());
        String timeStamp = dateStrToStamp(data);
        return timeStamp;
    }

    /**
     * Date或者String转化为时间戳
     *java中Date类中的getTime()是获取时间戳的，java中生成的时间戳精确到毫秒级别，而unix中精确到秒级别，所以通过java生成的时间戳需要除以1000
     *  String time="1970-01-06 11:05:00";
     */
    public static String dateStrToStamp(String timeStr){
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(timeStr);
            long stamp = date.getTime() / 1000;
            return String.valueOf(stamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 将时间转化为时间戳
     * timeFormat:哪种格式的时间
     * time：时间
     */
    public static String timeTotimeStamp(String timeFormat, String time){
        SimpleDateFormat format =  new SimpleDateFormat(timeFormat);
        Date date = null;
        try {
            date = format.parse(time);
            long stamp = date.getTime() / 1000;
            return String.valueOf(stamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * @param
     * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
     */
    public static String formatDuring(long mss) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        return days + " 天 " + hours + " 小时 " + minutes + " 分钟 " + seconds + " 秒 ";
    }
    /**
     *
     * @param begin 时间段的开始
     * @param end   时间段的结束
     * @return  输入的两个Date类型数据之间的时间间格用* days * hours * minutes * seconds的格式展示
     * @author fy.zhang
     */
    public static String formatDuring(Date begin, Date end) {
        return formatDuring(end.getTime() - begin.getTime());
    }
    public static void showErrorToast(Context context, int responseCode){
        if(responseCode==404){
            ToasUtils.showToastMessage("未找到服务器(404)");
        }else if(responseCode==500){
            ToasUtils.showToastMessage("服务器内部错误(500)");
        }else {
            if(!NetworkUtil.checkedNetWork(context)){
                ToasUtils.showToastMessage("网络异常,请连接成功后再试");
            }else {
                ToasUtils.showToastMessage("操作失败，请重试");
            }
        }
    }

}
