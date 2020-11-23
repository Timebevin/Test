package com.zt.school.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.jaeger.library.StatusBarUtil;
import com.lzy.okgo.OkGo;
import com.zt.school.R;
import butterknife.ButterKnife;
import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;
/**
 * activity 基类
 */
public abstract class BaseActivity extends AppCompatActivity implements BGASwipeBackHelper.Delegate {
    private BGASwipeBackHelper mSwipeBackHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initSwipeBackFinish();
        super.onCreate(savedInstanceState);
//        ScreenUtil.resetDensity(this);
        setContentView(setContentViewId());
        bind();
        statueBar();
        setTopBarFontColor();
        initView();
        initView(savedInstanceState);
        initListener();
        initData();
    }
    protected void statueBar() {
        StatusBarUtil.setColorForSwipeBack(this,getResources().getColor(setStatueBarColor()),0);
    }
    /**
     * 子类可重写此方法实现状态栏自定义
     */
    protected int setStatueBarColor(){
        return R.color.white;
    }
    /**
     * 子类实现此方法
     */
    protected abstract int setContentViewId();
    /**
     * 数据加载
     */
    protected void initData(){}
    /**
     * 子类可重写此方法初始化控件
     */
    protected void initView(){}
    /**
     * 子类可重写此方法初始化控件
     */
    protected void initView(Bundle savedInstanceState){}
    /**
     * 注解库绑定控件和事件
     */
    private void bind(){
        ButterKnife.bind(this);
    }
    private  void initListener(){
        View backIv = findViewById(R.id.back_colse_view);
        if(backIv!=null)backIv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                doBackClick(v);
            }
        });
    }
    /**
     * 子类可重写此方法实现back返回事件的自定义
     * @param view
     */
    protected void doBackClick(View view){
        if(view.getId() == R.id.back_colse_view){
            finish();
            closeSoftware();
        }
    }
    /**
     * 打开activity的切换动画
     * @param intent
     */
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
//        overridePendingTransition(R.anim.translate_right_to_between,R.anim.translate_between_to_left);
    }

    /**
     * activity销毁时的切换动画
     */
    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.translate_left_to_between,R.anim.translate_between_to_right);
    }

    /**
     * 关闭软键盘
     */
    public void closeSoftware() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 设置android 6.x以上系统 系统状态栏字体颜色
     */
    /**
     * 状态栏时间字体颜色,子类可重写实现自定义，默认黑色
     * 注意:当使用到手势侧滑删除(bga-swipebacklayout:1.2.0)时需使用 StatusBarUtil.setColorForSwipeBack(this, Color.WHITE,0);
     *      若不要侧滑使用  StatusBarUtil.setColor(this, Color.WHITE,0); 这内容布局会占入到状态栏会里
     * 注意:该方法setStatusBarfontColor()得于StatusBarUtil.setColorForSwipeBack(this, Color.WHITE,0);方法后执行才起效
     */
    protected void setTopBarFontColor(){
       if(setStatusBarFontColorIsBlack()){
           StatusBarUtil.setLightMode(this);
       }else {
           StatusBarUtil.setDarkMode(this);
       }
    }
    protected boolean setStatusBarFontColorIsBlack(){
        return false;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //取消请求
        OkGo.getInstance().cancelTag(this);
    }

    /**
     * 在某种情况下需要Activity的视图初始完毕Application中DisplayMetrics相关参数才能起效果，例如toast.
     * @param
     */
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    /**
     * 侧滑删除库api
     */
    /**
     * 初始化滑动返回。在 super.onCreate(savedInstanceState) 之前调用该方法
     */
    private void initSwipeBackFinish() {
        mSwipeBackHelper = new BGASwipeBackHelper(this, this);
        // 设置滑动返回是否可用。默认值为 true
        mSwipeBackHelper.setSwipeBackEnable(true);
        // 设置是否仅仅跟踪左侧边缘的滑动返回。默认值为 true
        mSwipeBackHelper.setIsOnlyTrackingLeftEdge(true);
        // 设置是否是微信滑动返回样式。默认值为 true
        mSwipeBackHelper.setIsWeChatStyle(true);
        // 设置阴影资源 id。默认值为 R.drawable.bga_sbl_shadow
        mSwipeBackHelper.setShadowResId(R.drawable.bga_sbl_shadow);
        // 设置是否显示滑动返回的阴影效果。默认值为 true
        mSwipeBackHelper.setIsNeedShowShadow(true);
        // 设置阴影区域的透明度是否根据滑动的距离渐变。默认值为 true
        mSwipeBackHelper.setIsShadowAlphaGradient(true);
        // 设置触发释放后自动滑动返回的阈值，默认值为 0.3f
        mSwipeBackHelper.setSwipeBackThreshold(0.3f);
        // 设置底部导航条是否悬浮在内容上，默认值为 false
        mSwipeBackHelper.setIsNavigationBarOverlap(false);
    }

    /**
     * 是否支持滑动返回。这里在父类中默认返回 true 来支持滑动返回，如果某个界面不想支持滑动返回则重写该方法返回 false 即可
     *
     * @return
     */
    @Override
    public boolean isSupportSwipeBack() {
        return true;
    }

    /**
     * 正在滑动返回
     * @param slideOffset 从 0 到 1
     */
    @Override
    public void onSwipeBackLayoutSlide(float slideOffset) {
    }

    /**
     * 没达到滑动返回的阈值，取消滑动返回动作，回到默认状态
     */
    @Override
    public void onSwipeBackLayoutCancel() {
    }

    /**
     * 滑动返回执行完毕，销毁当前 Activity
     */
    @Override
    public void onSwipeBackLayoutExecuted() { mSwipeBackHelper.swipeBackward();
    }

    @Override
    public void onBackPressed() {
        // 正在滑动返回的时候取消返回按钮事件
        if (mSwipeBackHelper.isSliding()) {
            return;
        }
        mSwipeBackHelper.backward();
    }
}
