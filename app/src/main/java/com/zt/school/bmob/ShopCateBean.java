package com.zt.school.bmob;

import cn.bmob.v3.BmobObject;

/**
 *商家所属分类类
 * 注：此表在后台手动添加
 */
public class ShopCateBean extends BmobObject {
    public String name;

    public ShopCateBean(String name) {
        this.name = name;
    }
}
