package com.zt.school.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jaeger.library.StatusBarUtil;
import com.zt.school.R;
import com.zt.school.base.BaseActivity;
import com.zt.school.bmob.ShopBean;
import com.zt.school.confige.Constant;
import com.zt.school.confige.MySharePreference;
import com.zt.school.fragment.FragmentMine;
import com.zt.school.utils.CallPhoneUtils;
import com.zt.school.utils.ToasUtils;
import com.zt.school.utils.UIUtils;

import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
/**
 * Author: 九七
 */
public class ShopLoginActivity extends BaseActivity{
    @BindView(R.id.back_colse_view)
    LinearLayout backLinearLayout;
    @BindView(R.id.tel)
    EditText phoneEdit;
    @BindView(R.id.pass)
    EditText passEdit;
    @BindView(R.id.see)
    ImageView seeIv;
    private boolean isSee;
    @Override
    protected int setContentViewId(){
        return R.layout.activity_shop_login;
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.translate_none,R.anim.translate_between_to_down);
    }

    @Override
    protected void statueBar(){
        StatusBarUtil.setTranslucentForImageView(this,0,null);
    }

    @Override
    public boolean isSupportSwipeBack(){
        return false;
    }

    @Override
    protected void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) backLinearLayout.getLayoutParams();
            lp.topMargin = UIUtils.getStateBarHeight();
            backLinearLayout.setLayoutParams(lp);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @OnClick({R.id.login,R.id.register,R.id.see}) void click(View view){
        switch (view.getId()){
            case R.id.login:
                login();
                break;
            case R.id.register:
                Intent intent = new Intent(this, ShopRegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.see:
                if(isSee){
                    seeIv.setImageResource(R.mipmap.icon_see_n);
                    passEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }else {
                    seeIv.setImageResource(R.mipmap.icon_see_p);
                    passEdit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                isSee = !isSee;
                //光标靠后
                Editable etable = passEdit.getText();
                Selection.setSelection(etable, etable.length());
                break;
        }
    }
    private void login(){
        String phone = phoneEdit.getText().toString().trim();
        String pass = passEdit.getText().toString().trim();
        if(!CallPhoneUtils.isPhone(phone)){
            shake(phoneEdit);
            return;
        }
        if(TextUtils.isEmpty(pass)){
            shake(passEdit);
            return;
        }
        BmobQuery<ShopBean> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("tel", phone);
        bmobQuery.addWhereEqualTo("pass", pass);
        bmobQuery.findObjects(new FindListener<ShopBean>(){
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void done(List<ShopBean> list, BmobException e){
                if (e == null) {
                    if(list.size()==1){
                        ToasUtils.showToastMessage("登录成功");
                        MySharePreference.setCurrentLoginState(Constant.STATE_BUSINESS_LOGIN_SUC);
                        ShopBean bean = list.get(0);
                        bean.createdTime = bean.getCreatedAt();
                        MySharePreference.saveShopBean(bean);
                        FragmentMine.initState();
                        finish();
                    }else {
                        shake(phoneEdit);
                        shake(passEdit);
                        ToasUtils.showToastMessage("手机号或密码错误");
                    }
                }else {
                    Log.v("TAG", e.toString() + "\n" + e.getErrorCode());
                }
            }
        });
    }
    private void shake(EditText editText){
        //控件横向来回抖动
        ObjectAnimator anim = ObjectAnimator.ofFloat(editText, "translationX", -30,30,-25,25,-20,20,-15,15,-10,10,-5,5,0);
        anim.setDuration(1000);
        anim.start();
    }
}
