package com.zt.school.activity;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.kongzue.dialog.v2.MessageDialog;
import com.zt.school.R;
import com.zt.school.base.BaseActivity;
import com.zt.school.bmob.UserBean;
import com.zt.school.utils.CallPhoneUtils;
import com.zt.school.utils.ToasUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class UserRegisterActivity extends BaseActivity {
    @BindView(R.id.id_register_nick_ed)
    EditText usernameEdit;
    @BindView(R.id.id_register_mobile_ed)
    EditText mobileEdit;
    @BindView(R.id.id_register_pass_ed)
    EditText passEdit;
    @BindView(R.id.id_register_repass_ed)
    EditText repassEdit;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_user_register;
    }
    @Override
    protected int setStatueBarColor() {
        return R.color.text_center_tab_selected;
    }

    @OnClick(R.id.id_register_btn) void click(View view){
        register();
    }
    private void register(){
        String username = usernameEdit.getText().toString().trim();
        String mobile = mobileEdit.getText().toString().trim();
        String pass = passEdit.getText().toString().trim();
        String repass = repassEdit.getText().toString().trim();
        if (TextUtils.isEmpty(username)){
            ToasUtils.showToastMessage("用户名不能为空");
            shake(usernameEdit);
            return;
        }
        if (!CallPhoneUtils.isPhone(mobile)){
            shake(mobileEdit);
            ToasUtils.showToastMessage("手机号码不正确");
            return;
        }
        if (TextUtils.isEmpty(pass)){
            shake(passEdit);
            ToasUtils.showToastMessage("密码不能为空");
            return;
        }
        if (pass.length()<6){
            shake(passEdit);
            ToasUtils.showToastMessage("请输入至少6位数密码");
            return;
        }
        if (TextUtils.isEmpty(repass)){
            shake(repassEdit);
            ToasUtils.showToastMessage("确认密码不能为空");
            return;
        }
        if (!repass.equals(pass)){
            shake(repassEdit);
            ToasUtils.showToastMessage("两次密码输入不一致");
            return;
        }
        UserBean userBean = new UserBean();
        userBean.motto = "早睡身体好，睡前别吃太饱";
        userBean.setMobilePhoneNumber(mobile);
        userBean.setUsername(username);
        userBean.setPassword(pass);
        userBean.signUp(new SaveListener<UserBean>(){
            @Override
            public void done(UserBean user, BmobException e){
                if (e == null) {
                    MessageDialog.show(UserRegisterActivity.this, "提示", "恭喜您！注册成功", "OK，去登录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i){
                            finish();
                        }
                    });
                } else {
                    MessageDialog.show(UserRegisterActivity.this, "注册失败", e.getMessage());
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
