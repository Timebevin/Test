package com.zt.school.confige;

import android.content.Context;
import android.content.SharedPreferences;

import com.zt.school.bean.MyLocationBean;
import com.zt.school.bmob.ShopBean;

/**
 * Created by 九七
 */
public class MySharePreference {
    private static SharedPreferences sp;
    public static SharedPreferences.Editor editor;

    public static void  init(Context context){
        sp = context.getSharedPreferences("sp", Context.MODE_PRIVATE);
        editor = sp.edit();
    }
    //标记是否是apk下载状态
    public static void setUpdateAppStatus(boolean isUpdate){
        editor.putBoolean("update", isUpdate);
        editor.commit();
    }
    public static boolean isUpdateApp(){
        return sp.getBoolean("update", false);
    }
    public static int getCurrentLoginState(){
        return sp.getInt("currentLoginType", Constant.STATE_UN_LOING);
    }
    public static void setCurrentLoginState(int loginState){
        editor.putInt("currentLoginType", loginState);
        editor.commit();
    }
    public static void setVideoState(boolean isShow){
        editor.putBoolean("video", isShow).commit();
    }
    public static boolean getVideoState(){
        return sp.getBoolean("video", true);
    }
    //商家
    public static void saveShopBean(ShopBean shopBean){
        editor.putString("name", shopBean.name)
                .putString("pass",shopBean.pass)
                .putString("tel", shopBean.tel)
                .putString("username", shopBean.username)
                .putString("createdTime", shopBean.getCreatedAt())
                .putString("logo",shopBean.logo!=null?shopBean.logo:null)
                .putString("address",shopBean.address)
                .putString("diatance",shopBean.distance)
                .putString("score",shopBean.score)
                .putString("objectId",shopBean.getObjectId())
                .commit();
    }
    public static ShopBean getShopBean(){
        String name = sp.getString("name", "");
        String pass = sp.getString("pass", "");
        String tel = sp.getString("tel", "");
        String username = sp.getString("username", "");
        String createdAt = sp.getString("createdTime", "");
        String logo = sp.getString("logo", null);
        String address = sp.getString("address", "");
        String diatance = sp.getString("diatance", "");
        String score = sp.getString("score", "");
        String objectId = sp.getString("objectId", "");
        ShopBean shopBean = new ShopBean();
        shopBean.name = name;
        shopBean.pass = pass;
        shopBean.tel = tel;
        shopBean.username = username;
        shopBean.createdTime = createdAt;
        shopBean.logo = logo;
        shopBean.address = address;
        shopBean.distance = diatance;
        shopBean.score = score;
        shopBean.setObjectId(objectId);
        return shopBean;
    }
    //定位信息
    public static void saveCurrentLocationInfo(MyLocationBean  myLocationBean){
        editor.putString("l_lng", myLocationBean.lng)
                .putString("l_lat", myLocationBean.lat)
                .putString("l_adcode", myLocationBean.adcode)
                .putString("l_address", myLocationBean.address)
                .putString("l_province", myLocationBean.province)
                .putString("l_city", myLocationBean.city)
                .putString("l_district", myLocationBean.district)
                .putString("l_street", myLocationBean.street)
                .commit();
    }
    public static MyLocationBean getCurrentLocationInfo(){
        MyLocationBean myLocationBean = new MyLocationBean();
        myLocationBean.lng = sp.getString("l_lng", null);
        myLocationBean.lat = sp.getString("l_lat", null);
        myLocationBean.adcode = sp.getString("l_adcode", null);
        myLocationBean.address = sp.getString("l_address", null);
        myLocationBean.province = sp.getString("l_province", null);
        myLocationBean.city = sp.getString("l_city", null);
        myLocationBean.district = sp.getString("l_district", null);
        myLocationBean.street = sp.getString("l_street", null);
        return myLocationBean;
    }
}
