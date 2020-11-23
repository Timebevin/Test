package com.zt.school.bmob;

import cn.bmob.v3.BmobObject;
/**
 * Video视频
 * 注：此表在后台手动添加
 */
public class VideoBean extends BmobObject {
    public String userId,userName,userLogo;
    public int zangCount,commentCount,seeTime;
    //必有字段
    public String title,thumb,videourl;
    public boolean isShow = true;//是否在手机端显示此视频
}
