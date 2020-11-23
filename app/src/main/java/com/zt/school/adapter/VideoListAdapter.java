package com.zt.school.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.zt.school.R;
import com.zt.school.bmob.VideoBean;
import com.zt.school.common.CommonAdapter;
import com.zt.school.common.CommonViewHolder;
import com.zt.school.utils.GlideUtils;
import com.zt.school.views.CircleImageView;
import com.zt.school.views.CustomJzVideo;

import java.util.List;

import cn.jzvd.JzvdStd;

public class VideoListAdapter extends CommonAdapter<VideoBean> {

    public VideoListAdapter(Context context, List<VideoBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(CommonViewHolder holder, VideoBean videoBean) {
        CustomJzVideo jzvdStd = holder.getView(R.id.videoplayer);
        ImageView imageView = holder.getView(R.id.id_iv);
        if(holder.getmPosition()==0){
            imageView.setVisibility(View.VISIBLE);
        }else {
            imageView.setVisibility(View.GONE);
        }
        jzvdStd.thumbImageView.setScaleType(ImageView.ScaleType.FIT_XY);//设置视频的缩略图全屏
        jzvdStd.setUp(videoBean.videourl,videoBean.title, JzvdStd.SCREEN_WINDOW_LIST);
        GlideUtils.load(videoBean.thumb,jzvdStd.thumbImageView);
        jzvdStd.positionInList = holder.getmPosition();

        holder.setTvText(R.id.id_video_title_tv, videoBean.title)
                .setTvText(R.id.id_name_tv, videoBean.userName != null ? videoBean.userName : "匿名")
                .setTvText(R.id.zang_tv, String.valueOf(videoBean.zangCount))
                .setTvText(R.id.comment_tv, String.valueOf(videoBean.commentCount));
        CircleImageView logoIv = holder.getView(R.id.logo_iv);
        GlideUtils.load(videoBean.userLogo,logoIv);
    }
}
