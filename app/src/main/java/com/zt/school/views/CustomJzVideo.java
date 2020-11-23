package com.zt.school.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

/**
 * 自定义
 */
public class CustomJzVideo extends JzvdStd {

    public CustomJzVideo(Context context) {
        super(context);
        Jzvd.setVideoImageDisplayType(Jzvd.VIDEO_IMAGE_DISPLAY_TYPE_FILL_SCROP);//宽高父控件填充模式
        Jzvd.WIFI_TIP_DIALOG_SHOWED=true;//取消播放时在非WIFIDialog提示
    }

    public CustomJzVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
        Jzvd.setVideoImageDisplayType(Jzvd.VIDEO_IMAGE_DISPLAY_TYPE_FILL_SCROP);//宽高父控件填充模式
        Jzvd.WIFI_TIP_DIALOG_SHOWED=true;//取消播放时在非WIFIDialog提示
    }
    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();
        thumbImageView.setVisibility(View.GONE);//播放完成不显示预览图
    }
}
