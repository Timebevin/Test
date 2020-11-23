package com.zt.school.bmob;

import cn.bmob.v3.BmobObject;

/**
 * 菜品分类
 */
public class FoodCateBean extends BmobObject {
    public String shopId;
    public String name;
    public int total;

    public FoodCateBean(String shopId,String name) {
        this.shopId = shopId;
        this.name = name;
    }
    public FoodCateBean(){}
}
