package com.zt.school.bmob;

import cn.bmob.v3.BmobObject;

/**
 * Author: 九七
 *新闻消息类
 * 注：此表在后台手动添加
 */

public class NewsBean extends BmobObject {
    public int type;//1：url连接 2：html字符串
    public String title,desc;//title:标题 desc：简介
    public String url,html;
    public boolean isShow;//是否显示

    public int sort;//排序 数字越大越在前
}
