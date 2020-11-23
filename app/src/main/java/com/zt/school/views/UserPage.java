package com.zt.school.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.kongzue.dialog.v2.SelectDialog;
import com.zt.school.R;
import com.zt.school.activity.AboutOurActivity;
import com.zt.school.activity.MyOrderStateListActivity;
import com.zt.school.activity.OpinionFeedBackActivity;
import com.zt.school.activity.ReciveAddressListActivity;
import com.zt.school.activity.SystemSettingActivity;
import com.zt.school.activity.UserInfoActivity;
import com.zt.school.activity.UserLoginActivity;
import com.zt.school.activity.MainActivity;
import com.zt.school.activity.ShopLoginActivity;
import com.zt.school.adapter.ItemLvAdapter;
import com.zt.school.base.ItemBean;
import com.zt.school.bmob.UserBean;
import com.zt.school.confige.Constant;
import com.zt.school.confige.MySharePreference;
import com.zt.school.utils.CustomListView;
import com.zt.school.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

/**
 * Author: 九七
 * 普通用户显示页
 */

public class UserPage extends FrameLayout {
    @BindView(R.id.custom_lv)
    CustomListView listView;
    MainActivity mainActivity;

    @BindView(R.id.id_photo)
    CircleImageView circleImageView;
    @BindView(R.id.id_name)
    TextView usernameTv;
    @BindView(R.id.id_motto_tv)
    TextView mottoTv;
    private ObjectAnimator mAnimator;

    public UserPage(@NonNull Context content) {
        super(content);
        this.mainActivity = (MainActivity) content;
        init();
    }

    private void init(){
        View view = UIUtils.inflater(R.layout.page_user);
        this.addView(view);
        ButterKnife.bind(this);
        final List<ItemBean> list = new ArrayList<>();
        list.add(new ItemBean(R.mipmap.icon_qb,"我的订单"));
        list.add(new ItemBean(R.mipmap.icon_bj,"信息编辑"));
        list.add(new ItemBean(R.mipmap.icon_pj,"收货地址"));
        list.add(new ItemBean(R.mipmap.icon_our,"意见反馈"));
        list.add(new ItemBean(R.mipmap.icon_yl,"关于我们"));
        list.add(new ItemBean(R.mipmap.icon_sz,"系统设置"));
        listView.setAdapter(new ItemLvAdapter(mainActivity,list,R.layout.item_mine_lv));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                switch (i){
                    case 0:
                        loginCheck(MyOrderStateListActivity.class);
                        break;
                    case 1:
                        loginCheck(UserInfoActivity.class);
                        break;
                    case 2:
                        loginCheck(ReciveAddressListActivity.class);
                        break;
                    case 3:
                        loginCheck(OpinionFeedBackActivity.class);
                        break;
                    case 4:
                        loginCheck(AboutOurActivity.class);
                        break;
                    case 5:
                        syssetting();
                        break;
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void updateUi(){
        mainActivity.orderButtonOption(false);//此句代码：修复商家等登录成功后续而在登录用户端【底部订单tab】没显示出来的bug
        if(BmobUser.isLogin()){
            UserBean user = BmobUser.getCurrentUser(UserBean.class);
            if (user.logo!=null){
                showRoteAnimatorImage(user.logo);
            }else {
                circleImageView.setImageResource(R.mipmap.logo);
                initAnimator();
            }
            usernameTv.setText(user.getUsername());
            if (user.nickname==null){
                usernameTv.setText(user.getUsername());
            }else {
                usernameTv.setText(user.nickname);
            }
            if (user.motto!=null)mottoTv.setText(user.motto);
        }else {
            circleImageView.setImageResource(R.mipmap.logo);
            if (mAnimator!=null)mAnimator.setFloatValues(0);
            usernameTv.setText("未登录");
            mottoTv.setText("登录后设置个性座右铭");
        }
    }
    @OnClick({R.id.id_photo}) void click(View view){
        switch (view.getId()){
            case R.id.id_photo:
                loginCheck(UserInfoActivity.class);
                break;
        }
    }
    private void loginCheck(Class atyClasss){
        int loginState = MySharePreference.getCurrentLoginState();
        if (loginState== Constant.STATE_UN_LOING){
            SelectDialog.show(mainActivity, "提示", "请选择登录类型", "会员登录", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(mainActivity, UserLoginActivity.class);
                    mainActivity.startActivity(intent);
                    mainActivity.overridePendingTransition(R.anim.translate_down_to_between, R.anim.translate_none);
                }
            }, "商家登录", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(mainActivity, ShopLoginActivity.class);
                    mainActivity.startActivity(intent);
                    mainActivity.overridePendingTransition(R.anim.translate_down_to_between, R.anim.translate_none);
                }
            }).setCanCancel(true);
        }else {
            Intent intent = new Intent(mainActivity, atyClasss);
            mainActivity.startActivity(intent);
        }
    }
    private void syssetting(){
        int loginState = MySharePreference.getCurrentLoginState();
        if (loginState== Constant.STATE_UN_LOING){
            SelectDialog.show(mainActivity, "提示", "请选择登录类型", "会员登录", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(mainActivity, UserLoginActivity.class);
                    mainActivity.startActivity(intent);
                    mainActivity.overridePendingTransition(R.anim.translate_down_to_between, R.anim.translate_none);
                }
            }, "商家登录", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(mainActivity, ShopLoginActivity.class);
                    mainActivity.startActivity(intent);
                    mainActivity.overridePendingTransition(R.anim.translate_down_to_between, R.anim.translate_none);
                }
            }).setCanCancel(true);
        }else {
            Intent intent = new Intent(mainActivity, SystemSettingActivity.class);
            mainActivity.startActivity(intent);
            mainActivity.overridePendingTransition(R.anim.translate_down_to_between,R.anim.translate_none);
        }
    }
    //Glide 加载图片回调方法与控件匀速旋转
    private void showRoteAnimatorImage(String thumb){
        Glide.with(mainActivity).load(thumb).listener(new RequestListener<String, GlideDrawable>(){
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }
            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                initAnimator();
                return false;
            }
        }).into(circleImageView);
    }
    //动画 https://www.jianshu.com/p/52abfdeb1c38
    public void initAnimator(){
        mAnimator = ObjectAnimator.ofFloat(circleImageView, "rotation", 0.0f, 360.0f);
        mAnimator.setDuration(5000);//设定转一圈的时间
        mAnimator.setRepeatCount(Animation.INFINITE);//设定无限循环
        mAnimator.setRepeatMode(ObjectAnimator.RESTART);// 循环模式
        mAnimator.setInterpolator(new LinearInterpolator());// 匀速
        mAnimator.start();//动画开始
    }
}
