package com.zt.school.activity;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kongzue.dialog.v2.MessageDialog;
import com.tencent.tauth.Tencent;
import com.zt.school.R;
import com.zt.school.base.BaseActivity;
import com.zt.school.bmob.UserBean;
import com.zt.school.confige.Constant;
import com.zt.school.confige.MySharePreference;
import com.zt.school.fragment.FragmentMine;
import com.zt.school.utils.CallPhoneUtils;
import com.zt.school.utils.GlideUtils;
import com.zt.school.utils.ToasUtils;
import com.zt.school.views.CircleImageView;
import org.json.JSONException;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
/**
 * 微信/qq首次登陆需绑定手机号
 */
public class BindMobileActivity extends BaseActivity {
    @BindView(R.id.id_base_title_tv)
    TextView mTitleTv;
    @BindView(R.id.id_register_mobile_ed)
    EditText mobileEdit;
    @BindView(R.id.id_register_code_ed)
    EditText codeEdit;
    @BindView(R.id.id_getcode_tv)
    TextView mSendCodeTv;
    @BindView(R.id.id_protrait)
    CircleImageView circleImageView;
    @BindView(R.id.id_username_tv)
    TextView usernameTv;
    @BindView(R.id.id_noti_tv)
    TextView notiTv;

    private String plat_type;
    private CountTime countTime;

    @Override
    protected int setContentViewId(){
        return R.layout.activity_bind_mobile;
    }

    @Override
    protected boolean setStatusBarFontColorIsBlack(){
        return true;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        countTime = new CountTime(60 * 1000, 1000);
    }

    JSONObject jsonObject;
    @Override
    protected void initView() {
        mTitleTv.setText("绑定手机号");
        countTime = new CountTime(60 * 1000, 1000);
        try {
            jsonObject = new JSONObject(getIntent().getStringExtra("open_platform"));
            plat_type = jsonObject.getString("plat_type");
            if (plat_type.equals("wechat")){
                notiTv.setText("温馨提示:首次使用微信登陆需要绑定手机号");
            }else if (plat_type.equals("qq")){
                notiTv.setText("温馨提示:首次使用QQ登陆需要绑定手机号");
            }
            GlideUtils.load(jsonObject.getString("avatar"),circleImageView);
            usernameTv.setText(jsonObject.getString("nickname"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @OnClick({R.id.id_getcode_tv,R.id.id_bind_tv}) void click(View view){
        switch (view.getId()){
            case R.id.id_getcode_tv:
                String mobile = mobileEdit.getText().toString().trim();
                if(!CallPhoneUtils.isPhone(mobile)){
                    ToasUtils.showToastMessage("请输入正确的手机号");
                    return;
                }
                Toast.makeText(this,"获取验证码待实现，可使用默认验证码123456",Toast.LENGTH_LONG).show();
                countTime.start();
                break;
            case R.id.id_bind_tv:
                bind();
                break;
        }
    }
    class  CountTime extends CountDownTimer {
        public CountTime(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long l) {
            mSendCodeTv.setText(l/1000+"s");
            mSendCodeTv.setClickable(false);
        }
        @Override
        public void onFinish() {
            mSendCodeTv.setText("重新发送");
            mSendCodeTv.setClickable(true);
        }
    }
    private void bind(){
        String mobile = mobileEdit.getText().toString().trim();
        if(!CallPhoneUtils.isPhone(mobile)){
            ToasUtils.showToastMessage("请输入正确的手机号");
            return;
        }
        String code = codeEdit.getText().toString().trim();
        if (TextUtils.isEmpty(code)){
            ToasUtils.showToastMessage("请输入验证码，或使用默认验证码123456");
            return;
        }
        UserBean userBean = new UserBean();
        userBean.motto = "早睡身体好，睡前别吃太饱";
        userBean.setMobilePhoneNumber(mobile);
        try {
            String openid = jsonObject.getString("openid");
            String name = jsonObject.getString("nickname");
            String avatar = jsonObject.getString("avatar");
            String plat_type = jsonObject.getString("plat_type");
            userBean.setUsername(openid);
            userBean.openid = openid;
            userBean.nickname = name;
            userBean.logo = avatar;
            userBean.plat_type = plat_type;
            userBean.three_nickname = name;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        userBean.setPassword("123456");//第三方登陆默认密码123456
        userBean.signUp(new SaveListener<UserBean>(){
            @Override
            public void done(UserBean user, BmobException e){
                if (e == null) {
                    loginByPhone(mobile,"123456");
                } else {
                    MessageDialog.show(BindMobileActivity.this, "绑定失败", e.getMessage());
                }
            }
        });
    }
    /**
     * 手机号码密码登录
     */
    private void loginByPhone(String phone,String password){
        //TODO 此处替换为你的手机号码和密码
        BmobUser.loginByAccount(phone, password, new LogInListener<UserBean>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void done(UserBean user, BmobException e) {
                if (e == null && user!=null) {
                    //登录成功
                    MySharePreference.setCurrentLoginState(Constant.STATE_NORMAL_LOGIN_SUC);
                    FragmentMine.initState();

                    ToasUtils.showToastMessage("绑定成功");
                    isBindSuccess = true;
                    setResult(200);
                    finish();
                } else {
                    //登录失败
                    ToasUtils.showToastMessage("绑定失败");
                }
            }
        });
    }

    private boolean isBindSuccess;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销qq登陆，解决点击qq不能执行登陆方法的bug
        if ("qq".equals(plat_type) && !isBindSuccess){
            Tencent mTencent = Tencent.createInstance(Constant.TENCENT_APP_ID, this.getApplicationContext());
            mTencent.logout(this);
        }
    }
}
