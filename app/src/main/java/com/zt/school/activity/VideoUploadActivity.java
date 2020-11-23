package com.zt.school.activity;

import android.widget.TextView;

import com.zt.school.R;
import com.zt.school.base.BaseActivity;

import butterknife.BindView;

/**
 * Author: 九七
 */

public class VideoUploadActivity extends BaseActivity {
    @BindView(R.id.id_base_title_tv)
    TextView mTitleTv;
    @Override
    protected int setContentViewId() {
        return R.layout.activity_video_upload;
    }

    @Override
    protected void initView() {
        mTitleTv.setText("视频上传");
    }
}
