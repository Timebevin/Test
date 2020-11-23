package com.zt.school.bmob;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * 菜品
 */
public class FoodBean extends BmobObject implements Serializable{
    public String shopId;
    public String cateId;
    public String cateName;
    public String name;
    public String price;
    public String imgs;//字符串数组形式

    public int total;
    public int status = 1;//1上架 0下架

    public FoodBean(String shopId,String cateId, String cateName,String name, String price, String imgs) {
        this.shopId = shopId;
        this.cateId = cateId;
        this.cateName = cateName;
        this.name = name;
        this.price = price;
        this.imgs = imgs;
    }
    public FoodBean(){}
}
