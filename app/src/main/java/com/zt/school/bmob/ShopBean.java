package com.zt.school.bmob;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Author: 九七
 */

public class ShopBean extends BmobObject implements Serializable {
    public String name,tel,username,pass, createdTime,logo;
    public String address,score, distance;
    public String cateId,cateName;
    public String lat, lng;
    public String hot;
    public boolean isPass = true;//是否审核通过，默认true

    public ShopBean(){}

    @Override
    public String toString(){
        return "ShopBean{" +
                "name='" + name + '\'' +
                ", tel='" + tel + '\'' +
                ", username='" + username + '\'' +
                ", pass='" + pass + '\'' +
                ", createdTime='" + createdTime + '\'' +
                ", logo='" + logo + '\'' +
                '}';
    }
}
