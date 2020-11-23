package com.zt.school.bmob;

import cn.bmob.v3.BmobObject;

/**
 * Author: 九七
 * 广告类（app开屏页数据）
 * 注：此表在后台手动添加
 */
public class AdBean  extends BmobObject{
    public String title;//标题
    public String thumbnail;//缩略图
    public String url;//跳转链接
    public int sort;//排序(数字越大越在前)
    public boolean isClick;//广告是否可以点击跳转
    public boolean isShow;//是否显示
}
