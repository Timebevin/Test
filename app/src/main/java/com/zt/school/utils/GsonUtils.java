package com.zt.school.utils;
import com.google.gson.Gson;

/**
 * 单列懒汉式
 */
public class GsonUtils {
    private static Gson gson;
    private GsonUtils(){}
    public static Gson getInstance(){
        if(gson==null){
            synchronized (GsonUtils.class){
                gson = new Gson();
            }
        }
        return gson;
    }
}
