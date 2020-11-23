package com.zt.school.bmob;

import cn.bmob.v3.BmobObject;

/**
 * Author: 九七
 * banner轮播图数据
 */

public class BannerData extends BmobObject{
    private String img;//图片地址
    private String url;//跳转链接
    public int type;//链接跳转类型（可自行设计）
    public int sort;//排序 数字越大越在前
    public boolean isShow = true;//是否在App端显示,默认true

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
