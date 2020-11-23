package com.zt.school.bmob;

import cn.bmob.v3.BmobObject;

/**
 * 视频评论表
 */
public class VideoCommentBean extends BmobObject {
    public String userId,userName, userLogo;
    public String videoId;
    public String content;
}
