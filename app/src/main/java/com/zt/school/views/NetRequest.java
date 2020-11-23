package com.zt.school.views;

import android.os.Build;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.PostRequest;
import com.zt.school.utils.UIUtils;

import java.io.File;
/**
 * 公共请求头
 */
public class NetRequest {
    public static PostRequest<String> getStringPostRequest(String url){
        PostRequest<String> request = OkGo.<String>post(url)
                .headers("device-type", "android")
                .headers("x-timestamp", UIUtils.getCurrentSysTimeStamp())
                .headers("system-version", Build.VERSION.RELEASE);
//        if(MySharedPreferences.getUser().token!=null){
//            request.headers("token", MySharedPreferences.getUser().token);
//        }
        return request;
    }
    public static PostRequest<File> getFilePostRequest(String url){
        PostRequest<File> request = OkGo.<File>post(url)
                .headers("device-type", "android")
                .headers("x-timestamp", UIUtils.getCurrentSysTimeStamp())
                .headers("system-version", Build.VERSION.RELEASE);
//        if(MySharedPreferences.getUser().token!=null){
//            request.headers("token", MySharedPreferences.getUser().token);
//        }
        return request;
    }
}
