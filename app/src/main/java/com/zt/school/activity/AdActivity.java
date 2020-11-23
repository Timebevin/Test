package com.zt.school.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.flyco.roundview.RoundTextView;
import com.jaeger.library.StatusBarUtil;
import com.zt.school.R;
import com.zt.school.base.BaseActivity;
import com.zt.school.bmob.AdBean;
import com.zt.school.confige.Constant;
import com.zt.school.utils.GlideUtils;
import com.zt.school.utils.UIUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by 九七
 */

public class AdActivity extends BaseActivity {
    @BindView(R.id.id_img)
    ImageView imageView;
    @BindView(R.id.id_count_tv)
    RoundTextView countTv;
    @BindView(R.id.root_rl)
    RelativeLayout rootRl;
    private CountTime countTime;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_ad;
    }

    @Override
    protected void statueBar() {
        StatusBarUtil.setTranslucentForImageView(this,0,null);
    }

    @Override
    protected void initView() {
        loadAd();
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) countTv.getLayoutParams();
        lp.topMargin = UIUtils.getStateBarHeight() + UIUtils.getStateBarHeight()/2;
        countTv.setLayoutParams(lp);
        countTime = new CountTime(5*1000,1000);
        countTime.start();
        initAnimation();
    }
    AdBean adBean;
    private void loadAd(){
        BmobQuery<AdBean> query = new BmobQuery<>();
        query.addWhereEqualTo("isShow", true);//显示
        query.order("-sort");//倒序查询
        query.setLimit(1);//查询一条
        query.findObjects(new FindListener<AdBean>() {
            @Override
            public void done(List<AdBean> list, BmobException e) {
                if (e==null && list!=null && list.size()>0){
                    adBean = list.get(0);
                    GlideUtils.load(adBean.thumbnail,imageView);
                }else {
                    //网络请求失败/所有广告被关闭显示/AdBean表没有添加任何数据，则加载下面这张默认图片
                    GlideUtils.load("http://bmob-cdn-8720.b0.upaiyun.com/2019/02/24/3c9d8cb24004d3cc807f8d78608b6b3c.jpg",imageView);
                }
            }
        });
    }

    @OnClick({R.id.id_img,R.id.id_count_tv}) void click(View view){
        switch (view.getId()){
            case R.id.id_img:
                if (adBean!=null && adBean.isClick){
                    toAdDetailAty();
                }
                break;
            case R.id.id_count_tv:
                toMain();
                break;
        }
    }
    class  CountTime extends CountDownTimer {
        public CountTime(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long l) {
            countTv.setText(l/1000+" 跳过");
        }
        @Override
        public void onFinish() {
            toMain();
        }
    }
    private void  toMain(){
        countTime.cancel();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_scale,R.anim.translate_none);
        finish();
    }
    private void toAdDetailAty(){
        countTime.cancel();
        Intent intent = new Intent(this, AdWebDetailActivity.class);
        intent.putExtra(Constant.URL, adBean.url);
        intent.putExtra(Constant.TITLE, adBean.title);
        startActivity(intent);
        finish();
    }
    private void initAnimation() {
        //旋转动画
        RotateAnimation rotateAnimation = new RotateAnimation(0,720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);//旋转时间
        rotateAnimation.setFillAfter(true);//旋转后停止在最后,保持动画结束状态
        //缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setFillAfter(true);
        //渐变动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(2000);
        alphaAnimation.setFillAfter(true);
        //动画集合来存储3个动画
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(rotateAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        //开启动画
        rootRl.startAnimation(animationSet);

        //动画监听
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
            //动画结束回调监听方法
            @Override
            public void onAnimationEnd(Animation animation) {

            }
        });
    }

    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }
    /**
     *禁止点击back返回键退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }
}
