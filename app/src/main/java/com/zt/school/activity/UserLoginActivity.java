package com.zt.school.activity;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.kongzue.dialog.v2.WaitDialog;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tencent.connect.UserInfo;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zt.school.R;
import com.zt.school.base.BaseActivity;
import com.zt.school.bean.OpenPlatform;
import com.zt.school.bmob.UserBean;
import com.zt.school.confige.Constant;
import com.zt.school.confige.MyApplication;
import com.zt.school.confige.MySharePreference;
import com.zt.school.fragment.FragmentMine;
import com.zt.school.utils.CallPhoneUtils;
import com.zt.school.utils.GsonUtils;
import com.zt.school.utils.ToasUtils;
import com.zt.school.utils.UIUtils;
import com.zt.school.views.NetRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Author: 九七
 */
public class UserLoginActivity extends BaseActivity{
    @BindView(R.id.username_mobile_edit)
    EditText usernameMobileEdit;
    @BindView(R.id.pass)
    EditText passEdit;
    @BindView(R.id.see)
    ImageView seeIv;
    @BindView(R.id.user_mobile_view)
    RelativeLayout userMobileView;
    @BindView(R.id.id_change_login_tv)
    TextView loginTypeTv;
    @BindView(R.id.flage_iv)
    ImageView flageIv;
    @BindView(R.id.back_colse_view)
    LinearLayout backLinearLayout;

    Animation animation1;
    Animation animation2;

    public static final int WX_LOGIN = 100;
    public static Handler mHandler;
    private boolean isSee;
    private int loginType;//0用户名登陆 1手机号码登陆

    //第三方登陆--qq
    private LoginListener listener;//授权登录监听器
    private UserInfoListener userInfoListener;//获取用户信息监听器
    private UserInfo userInfo; //qq用户信息
    private String scope = "all"; //获取信息的范围参数,all所以用户信息
    private Tencent mTencent;

    @Override
    protected int setContentViewId(){
        return R.layout.activity_login;
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
        //处理微信回掉
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case WX_LOGIN:
                        getAccess_token_openId((String) msg.obj);
                        break;
                }
            }
        };
        //初始化qq登陆相关回掉接口
        listener = new LoginListener();
        userInfoListener = new UserInfoListener();

        animation1 = AnimationUtils.loadAnimation(this, R.anim.anim_item_scale1);
        animation2 = AnimationUtils.loadAnimation(this, R.anim.anim_item_scale2);
    }
    //0,获取code
    //1,code请求微信接口获取access_token和openid
    //2.access_token和openid请求微信接口获取用户基本信息
    //3.请求App服务器接口注册/登陆操作
    private void getAccess_token_openId(String code){
        NetRequest.getStringPostRequest("https://api.weixin.qq.com/sns/oauth2/access_token")
                .params("appid",Constant.WX_APPID)
                .params("secret",Constant.WX_APPSECRET)
                .params("code",code)
                .params("grant_type","authorization_code")
                .execute(new StringCallback(){
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            String access_token = jsonObject.getString("access_token");
                            String openid = jsonObject.getString("openid");
                            getWeixin_User_Info(access_token,openid);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
    private void getWeixin_User_Info(String access_token,String openid){
        NetRequest.getStringPostRequest("https://api.weixin.qq.com/sns/userinfo")
                .params("access_token",access_token)
                .params("openid",openid)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            OpenPlatform open_platform = new OpenPlatform();
                            open_platform.openid = jsonObject.getString("openid");
                            open_platform.nickname = jsonObject.getString("nickname");
                            open_platform.avatar = jsonObject.getString("headimgurl");
                            open_platform.gender = String.valueOf(jsonObject.getInt("sex"));
                            open_platform.plat_type = "wechat";
                            String json = GsonUtils.getInstance().toJson(open_platform);
                            bind_mobile_register_or_login(json,open_platform.openid);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
    /**
     * 根据第三方登陆平台获取到的用户信息，调用app服务器接口绑定手机号/注册/登陆
     */
    private void bind_mobile_register_or_login(String open_platform,String openid){
        //根据openid判断是否已经绑定过手机号码了
        BmobQuery<UserBean> query = new BmobQuery<>();
        query.addWhereEqualTo("openid", openid);
        query.findObjects(new FindListener<UserBean>() {
            @Override
            public void done(List<UserBean> list, BmobException e) {
                if (e==null && list!=null && list.size()>0){
                    //已经绑定过，登陆操作
                    UserBean user = list.get(0);
                    //第三方登陆默认密码123456，除非你修过
                    loginByPhone(user.getMobilePhoneNumber(),"123456");
                }else {
                    //去绑定
                    Intent intent = new Intent(UserLoginActivity.this, BindMobileActivity.class);
                    intent.putExtra("open_platform", open_platform);
                    startActivityForResult(intent,100);
                }
            }
        });
    }
    public void wxLogin() {
        if (!MyApplication.iwxapi.isWXAppInstalled()) {
            ToasUtils.showToastMessage("您还未安装微信客户端");
            return;
        }
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "diandi_wx_login";
        MyApplication.iwxapi.sendReq(req);
    }
    //QQ登录
    public void loginQQ()
    {
        mTencent = Tencent.createInstance(Constant.TENCENT_APP_ID, this.getApplicationContext());
        if (!mTencent.isSessionValid())
        {
            mTencent.login(this, scope, listener);
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
    @OnClick({R.id.login,R.id.register,R.id.see,R.id.id_change_login_tv,R.id.login_wx,R.id.login_qq}) void click(View view){
        switch (view.getId()){
            case R.id.login:
                login();
                break;
            case R.id.register:
                startActivity(new Intent(this,UserRegisterActivity.class));
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
            case R.id.id_change_login_tv:
                changeLoginType();
                break;
            case R.id.login_wx:
                animationBtn(view,1);
                break;
            case R.id.login_qq:
                animationBtn(view,2);
                break;
        }
    }
    private void animationBtn(View view,int type){
        //缩放动画
        view.startAnimation(animation1);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.startAnimation(animation2);
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
                threeLogin(type);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    private void threeLogin(int type){
        if (type == 1){
            WaitDialog.show(this, "登陆中...");
            wxLogin();
            WaitDialog.dismiss();
        }else if (type == 2){
            WaitDialog.show(this, "登陆中...");
            loginQQ();
            WaitDialog.dismiss();
        }
    }
    private void changeLoginType(){
        usernameMobileEdit.setText(null);
        if (loginType==0){
            //手机号码登陆
            loginType = 1;
            loginTypeTv.setText("用户名登陆");
            usernameMobileEdit.setHint("请输入手机号码");
            flageIv.setImageResource(R.mipmap.icon_phone);
            usernameMobileEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        }else {
            //用户名登陆
            loginType = 0;
            loginTypeTv.setText("手机号码登陆");
            usernameMobileEdit.setHint("请输入用户名");
            flageIv.setImageResource(R.mipmap.icon_count);
            usernameMobileEdit.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        //旋转动画
        RotateAnimation rotateAnimation = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);//旋转时间
        rotateAnimation.setFillAfter(true);//旋转后停止在最后,保持动画结束状态
        userMobileView.startAnimation(rotateAnimation);
    }
    private void login(){
        String username = usernameMobileEdit.getText().toString().trim();
        String pass = passEdit.getText().toString().trim();
        if(TextUtils.isEmpty(username)){
            shake(usernameMobileEdit);
            return;
        }
        if(TextUtils.isEmpty(pass)){
            shake(passEdit);
            return;
        }
        closeSoftware();
        if (loginType==0){
            loginByUsername(username,pass);
        }else {
            if (!CallPhoneUtils.isPhone(username)){
                shake(usernameMobileEdit);
                ToasUtils.showToastMessage("请输入正确的手机号码");
                return;
            }
            loginByPhone(username,pass);
        }
    }
    private void shake(EditText editText){
        //控件横向来回抖动
        ObjectAnimator anim = ObjectAnimator.ofFloat(editText, "translationX", -30,30,-25,25,-20,20,-15,15,-10,10,-5,5,0);
        anim.setDuration(1000);
        anim.start();
    }
    /**
     * 账号密码登录
     */
    private void loginByUsername(String username,String password) {
        final UserBean user = new UserBean();
        //此处替换为你的用户名
        user.setUsername(username);
        //此处替换为你的密码
        user.setPassword(password);
        user.login(new SaveListener<UserBean>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void done(UserBean bmobUser, BmobException e) {
                if (e == null) {
                    //登录成功
                    MySharePreference.setCurrentLoginState(Constant.STATE_NORMAL_LOGIN_SUC);
                    FragmentMine.initState();
                    finish();
                } else {
                    //登录失败
                    shake(usernameMobileEdit);
                    shake(passEdit);
                    ToasUtils.showToastMessage("用户名或密码错误");
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
                    ToasUtils.showToastMessage("登陆成功");
                    MySharePreference.setCurrentLoginState(Constant.STATE_NORMAL_LOGIN_SUC);
                    FragmentMine.initState();
                    finish();
                } else {
                    //登录失败
                    shake(usernameMobileEdit);
                    shake(passEdit);
                    ToasUtils.showToastMessage("手机号或密码错误");
                }
            }
        });
    }
    private class LoginListener implements IUiListener {
        //登陆成功返回
        @Override
        public void onComplete(Object o){
            try {
                JSONObject jsonObject = new JSONObject(o.toString());
                String access_token = jsonObject.getString("access_token");
                String openid = jsonObject.getString("openid");
                String expires = jsonObject.getString("expires_in");
                //qq登陆授权成功后，初始化mTencent实例的成员信息
                mTencent.setAccessToken(access_token, expires);
                mTencent.setOpenId(openid);
                //获取用户信息
                userInfo = new UserInfo(UserLoginActivity.this, mTencent.getQQToken()); //获取用户信息
                userInfo.getUserInfo(userInfoListener);//回掉UserInfoListener监听器里的方法
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onError(UiError e){
            Log.v("onError:", "code:" + e.errorCode + ", msg:"
                    + e.errorMessage + ", detail:" + e.errorDetail);
        }
        @Override
        public void onCancel(){
            Log.v("onCancel", "");
        }
    }

    private class UserInfoListener implements IUiListener {
        //回掉成功
        @Override
        public void onComplete(Object o) {
            try {
                JSONObject jsonObject = new JSONObject(o.toString());
                OpenPlatform openPlatform = new OpenPlatform();
                openPlatform.avatar = jsonObject.getString("figureurl_qq");
                openPlatform.nickname = jsonObject.getString("nickname");
                openPlatform.gender = jsonObject.getString("gender");
                openPlatform.openid = mTencent.getOpenId();
                openPlatform.plat_type = "qq";
                //class to json string
                String json = GsonUtils.getInstance().toJson(openPlatform);
                bind_mobile_register_or_login(json,openPlatform.openid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {

        }

        @Override
        public void onCancel() {

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100 && resultCode==200){
            finish();
        }
        Tencent.onActivityResultData(requestCode,resultCode,data,listener);
    }

}
