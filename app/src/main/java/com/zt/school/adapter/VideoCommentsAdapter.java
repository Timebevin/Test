package com.zt.school.adapter;

import android.content.Context;

import com.zt.school.R;
import com.zt.school.bmob.VideoCommentBean;
import com.zt.school.common.CommonAdapter;
import com.zt.school.common.CommonViewHolder;
import com.zt.school.utils.GlideUtils;
import com.zt.school.views.CircleImageView;

import java.util.List;

public class VideoCommentsAdapter extends CommonAdapter<VideoCommentBean> {

    public VideoCommentsAdapter(Context context, List<VideoCommentBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(CommonViewHolder holder, VideoCommentBean bena) {
        CircleImageView logoIv = holder.getView(R.id.logo_iv);
        GlideUtils.load(bena.userLogo, logoIv);
        holder.setTvText(R.id.user_name_tv, bena.userName)
                .setTvText(R.id.time_tv, bena.getCreatedAt())
                .setTvText(R.id.content_tv, bena.content);
    }
    public void addComment(VideoCommentBean bean){
        mDatas.add(0, bean);
        notifyDataSetChanged();
    }
}
