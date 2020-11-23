package com.zt.school.activity;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kongzue.dialog.v2.MessageDialog;
import com.zt.school.R;
import com.zt.school.base.BaseActivity;
import com.zt.school.bmob.OpinionBean;
import com.zt.school.utils.ToasUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by 九七
 */

public class OpinionFeedBackActivity extends BaseActivity {
    @BindView(R.id.id_base_title_tv)
    TextView mTitleTv;
    @BindView(R.id.msg_cntancet)
    EditText lxfsEdit;
    @BindView(R.id.msg_edit)
    EditText contentEdit;
    @Override
    protected int setContentViewId() {
        return R.layout.activity_yjfk;
    }
    @Override
    protected boolean setStatusBarFontColorIsBlack() {
        return true;
    }
    @Override
    protected void initView() {
        mTitleTv.setText("意见反馈");

    }
    @OnClick(R.id.commit_tv) void click(View view){
        closeSoftware();
        String xlfs = lxfsEdit.getText().toString().trim();
        String content = contentEdit.getText().toString().trim();
        if(TextUtils.isEmpty(xlfs)){
            ToasUtils.showToastMessage("请填写你的联系方式");
            return;
        }
        if(TextUtils.isEmpty(content)){
            ToasUtils.showToastMessage("请填写您宝贵的意见");
            return;
        }
        OpinionBean opinionBean = new OpinionBean();
        opinionBean.number = xlfs;
        opinionBean.msg = content;
        opinionBean.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    MessageDialog.show(OpinionFeedBackActivity.this, "提示", "数据提交成功，感谢您的反馈！", "关闭", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                } else {
                    ToasUtils.showToastMessage("数据提交失败：" + e.getMessage());
                }
            }
        });
    }
}
