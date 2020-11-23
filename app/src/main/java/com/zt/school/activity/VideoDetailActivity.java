package com.zt.school.activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.jaeger.library.StatusBarUtil;
import com.kongzue.dialog.v2.SelectDialog;
import com.zt.school.R;
import com.zt.school.adapter.VideoCommentsAdapter;
import com.zt.school.base.BaseActivity;
import com.zt.school.bean.AndroidBug5497Workaround;
import com.zt.school.bmob.UserBean;
import com.zt.school.bmob.VideoBean;
import com.zt.school.bmob.VideoCommentBean;
import com.zt.school.confige.Constant;
import com.zt.school.utils.CustomListView;
import com.zt.school.utils.GlideUtils;
import com.zt.school.utils.ToasUtils;
import com.zt.school.utils.UIUtils;
import com.zt.school.views.CircleImageView;
import com.zt.school.views.CustomJzVideo2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
/**
 * Author: 九七
 */
public class VideoDetailActivity extends BaseActivity {
    @BindView(R.id.top_back_view)
    LinearLayout topBackView;
    @BindView(R.id.jzvideo)
    CustomJzVideo2 jzvdStd;
    @BindView(R.id.id_title_tv)
    TextView titleTv;
    @BindView(R.id.comment_edit)
    EditText commentEdit;
    @BindView(R.id.logo_iv)
    CircleImageView userLogoIv;
    @BindView(R.id.id_user_name_tv)
    TextView userNameTv;
    @BindView(R.id.zang_iv)
    ImageView zangIv;
    @BindView(R.id.zang_tv)
    TextView zangTv;
    @BindView(R.id.comment_tv)
    TextView commentCountTv;
    VideoBean video;
    @BindView(R.id.listview)
    CustomListView listView;
    @BindView(R.id.noti_tv)
    TextView notiTv;
    @BindView(R.id.zang)
    LinearLayout zangView;
    @BindView(R.id.see_time_tv)
    TextView seeTimeTv;

    Animation animation1;
    Animation animation2;
    VideoCommentsAdapter adapter;

    int zangCount,commentCount;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_video_detail;
    }

    @Override
    protected void statueBar() {
        StatusBarUtil.setTranslucentForImageView(this,0,null);
        AndroidBug5497Workaround.assistActivity(findViewById(android.R.id.content));
    }

    @Override
    protected void initView(){
        //padingTop顶出
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) topBackView.getLayoutParams();
            lp.height = lp.height + UIUtils.getStateBarHeight();
            topBackView.setPadding(0, UIUtils.getStateBarHeight(),0,0);
            topBackView.setLayoutParams(lp);
        }
        video = (VideoBean) getIntent().getSerializableExtra(Constant.VIDEO);
        jzvdStd.thumbImageView.setScaleType(ImageView.ScaleType.FIT_XY);//设置视频的缩略图全屏
        jzvdStd.setUp(video.videourl,video.title, JzvdStd.SCREEN_WINDOW_LIST);
        GlideUtils.load(video.thumb,jzvdStd.thumbImageView);
        jzvdStd.startVideo(); //https://www.imooc.com/article/254593

        titleTv.setText(video.title);
        GlideUtils.load(video.userLogo,userLogoIv);
        userNameTv.setText(video.userName!=null?video.userName:"匿名");


        //查询视频数据
        BmobQuery<VideoBean> queryVideo = new BmobQuery<>();
        queryVideo.addWhereEqualTo("objectId", video.getObjectId());
        queryVideo.getObject(video.getObjectId(), new QueryListener<VideoBean>() {
            @Override
            public void done(VideoBean videoBean, BmobException e) {
                if (e==null && videoBean!=null){
                    zangCount = videoBean.zangCount;
                    commentCount = videoBean.commentCount;
                    zangTv.setText(String.valueOf(zangCount));
                    seeTimeTv.setText(videoBean.seeTime+"次观看");
                    commentCountTv.setText(String.valueOf(commentCount));
                    //观看次数+1
                    video.seeTime = videoBean.seeTime + 1;
                    video.update(new UpdateListener(){
                        @Override
                        public void done(BmobException e) {

                        }
                    });
                }
            }
        });
        //查询评论数据
        BmobQuery<VideoCommentBean> query = new BmobQuery<>();
        query.addWhereEqualTo("videoId", video.getObjectId());
        query.order("-createdAt");
        query.findObjects(new FindListener<VideoCommentBean>() {
            @Override
            public void done(List<VideoCommentBean> list, BmobException e) {
                if (e==null && list!=null && list.size()>0){
                    listView.setAdapter(adapter = new VideoCommentsAdapter(VideoDetailActivity.this, list, R.layout.video_comment_item));
                }else {
                    notiTv.setVisibility(View.VISIBLE);
                }
            }
        });
        //初始化动画
        animation1 = AnimationUtils.loadAnimation(this, R.anim.anim_item_scale1);
        animation2 = AnimationUtils.loadAnimation(this, R.anim.anim_item_scale2);
    }
    @OnClick({R.id.zang,R.id.id_send_tv}) void click(View view){
        switch (view.getId()){
            case R.id.zang:
                addZang();
                break;
            case R.id.id_send_tv:
                sendComment();
                break;
        }
    }
    private boolean isZangClick;//标记是否已经点赞
    private void addZang(){
        if (BmobUser.isLogin()){
            animationZoom();
        }else {
            ToasUtils.showToastMessage("登陆后方可点赞!");
        }
    }
    private void animationZoom(){
        //缩放动画
        zangView.startAnimation(animation1);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                zangView.startAnimation(animation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isZangClick){
                    //取消点赞
                    zangCount = zangCount - 1;
                    video.zangCount = zangCount;
                    video.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                isZangClick = false;
                                zangIv.setImageResource(R.mipmap.zang_n);
                                zangTv.setText(String.valueOf(video.zangCount));
                            }
                        }
                    });
                }else {
                    //点赞
                    zangCount = zangCount + 1;
                    video.zangCount = zangCount;
                    video.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                isZangClick = true;
                                zangIv.setImageResource(R.mipmap.zang_p);
                                zangTv.setText(String.valueOf(video.zangCount));
                            }
                        }
                    });
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    private void sendComment(){
        closeSoftware();
        if (BmobUser.isLogin()){
            String content = commentEdit.getText().toString().trim();
            if (TextUtils.isEmpty(content)){
                ToasUtils.showToastMessage("说点什么吧...");
                return;
            }
            UserBean user = BmobUser.getCurrentUser(UserBean.class);
            VideoCommentBean videoCommentBean = new VideoCommentBean();
            videoCommentBean.videoId = video.getObjectId();
            videoCommentBean.userId = user.getObjectId();
            videoCommentBean.userLogo = user.logo;
            videoCommentBean.userName = user.nickname!=null?user.nickname:user.getUsername();
            videoCommentBean.content = content;
            videoCommentBean.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e==null){
                        ToasUtils.showToastMessage("评论成功");
                        commentEdit.setText(null);
                        if (adapter==null){
                            notiTv.setVisibility(View.GONE);
                            List<VideoCommentBean> list = new ArrayList<>();
                            list.add(videoCommentBean);
                            listView.setAdapter(adapter = new VideoCommentsAdapter(VideoDetailActivity.this, list, R.layout.video_comment_item));
                        }else {
                            adapter.addComment(videoCommentBean);
                        }
                        //更新评论数量
                        commentCount = commentCount + 1;
                        video.commentCount = commentCount;
                        video.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e==null)commentCountTv.setText(String.valueOf(video.commentCount));
                            }
                        });
                    }
                }
            });
        }else {
            SelectDialog.show(this, "提示", "登陆后方可评论", "登陆", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(VideoDetailActivity.this, UserLoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.translate_down_to_between, R.anim.translate_none);
                }
            }).setCanCancel(true);
        }
    }
   //返回界面继续播放
    @Override
    protected void onResume() {
        super.onResume();
        //home back
        JzvdStd.goOnPlayOnResume();
    }
    //Home键退出界面暂停播放/界面暂停
    @Override
    protected void onPause() {
        super.onPause();
        //home back
       JzvdStd.goOnPlayOnPause();
    }
    //activity销毁 释放所有视频资源
    @Override
    protected void onDestroy() {
        super.onDestroy();
        JzvdStd.releaseAllVideos();
    }
    @Override
    public void onBackPressed(){
        if (Jzvd.backPress()){ //Jz视频能否退出(是否是横屏播放暂停)
            return;
        }
        super.onBackPressed();
    }
}
