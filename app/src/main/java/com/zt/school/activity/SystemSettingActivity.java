package com.zt.school.activity;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.KeyEvent;
import android.view.View;

import com.suke.widget.SwitchButton;
import com.tencent.tauth.Tencent;
import com.zt.school.R;
import com.zt.school.base.BaseActivity;
import com.zt.school.confige.Constant;
import com.zt.school.confige.MySharePreference;
import com.zt.school.fragment.FragmentMine;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

/**
 * Author: 九七
 */

public class SystemSettingActivity extends BaseActivity {
    @BindView(R.id.switch_button)
    SwitchButton switchButton;
    @Override
    protected int setContentViewId() {
        return R.layout.activity_syssetting;
    }
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.translate_none,R.anim.translate_between_to_down);
    }
    @Override
    protected int setStatueBarColor() {
        return R.color.text_center_tab_selected;
    }
    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }

    @Override
    protected void initView() {
        switchButton.setChecked(MySharePreference.getVideoState());
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                MySharePreference.setVideoState(isChecked);
                MainActivity.videoButtonOption(isChecked);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    //退出登陆
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @OnClick(R.id.logout) void click(View view){
        MySharePreference.setCurrentLoginState(Constant.STATE_UN_LOING);
        BmobUser.logOut();
        FragmentMine.initState();
        finish();
        //若为qq登陆状态，QQ注销接口
        Tencent mTencent = Tencent.createInstance(Constant.TENCENT_APP_ID, this.getApplicationContext());
        if (mTencent!=null && mTencent.isSessionValid()){
            mTencent.logout(this);
        }
    }
}
