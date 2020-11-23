package com.zt.school.activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.kongzue.dialog.v2.MessageDialog;
import com.zt.school.R;
import com.zt.school.adapter.ImageUpLoadGridAdapter;
import com.zt.school.adapter.OrderStateListAdapter;
import com.zt.school.base.BaseActivity;
import com.zt.school.bmob.CommentBean;
import com.zt.school.bmob.OrderBean;
import com.zt.school.bmob.UserBean;
import com.zt.school.confige.Constant;
import com.zt.school.utils.CustomListView;
import com.zt.school.utils.ToasUtils;
import com.zt.school.views.CustomGridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Author: 九七
 * 订单评价界面
 */
public class CommentActivity extends BaseActivity {
    @BindView(R.id.id_base_title_tv)
    TextView mTitleTv;
    @BindView(R.id.listview)
    CustomListView customListView;
    @BindView(R.id.id_nm_iv)
    ImageView nimingIv;
    @BindView(R.id.id_gridview)
    CustomGridView customGridView;
    @BindView(R.id.id_user_commen_ratingbar)
    MaterialRatingBar materialRatingBar;
    @BindView(R.id.id_comment_edit)
    EditText commentEdit;
    @BindView(R.id.id_msg_fl)
    FrameLayout notiFrameLayout;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    boolean isNmComment;
    private ImageUpLoadGridAdapter adapter;

    @Override
    protected int setContentViewId(){
        return R.layout.activity_order_comment;
    }
    @Override
    protected void statueBar() {
        StatusBarUtil.setColor(this,getResources().getColor(R.color.white),0);
    }
    @Override
    protected void initView(){
        super.initView();
        mTitleTv.setText("订单评价");
        customGridView.setAdapter(adapter = new ImageUpLoadGridAdapter(this));
    }

    @Override
    protected boolean setStatusBarFontColorIsBlack(){
        return true;
    }

    OrderBean mOrderBean;
    @Override
    protected void initData() {
        String orderId = getIntent().getStringExtra(Constant.OBJECTID);
        BmobQuery<OrderBean> query = new BmobQuery<>();
        query.getObject(orderId, new QueryListener<OrderBean>() {
            @Override
            public void done(OrderBean orderBean, BmobException e) {
                if (e==null){
                    //判断订单是否已经评价过
                    if (orderBean.state==Constant.ORDER_STATE_COMPLETE){
                        notiFrameLayout.setVisibility(View.VISIBLE);
                    }else {
                        scrollView.setVisibility(View.VISIBLE);
                        List<OrderBean> list = new ArrayList<>();
                        list.add(orderBean);
                        mOrderBean = orderBean;
                        customListView.setAdapter(new OrderStateListAdapter(CommentActivity.this, list, R.layout.layout_item_order_sate, true));
                    }
                }
            }
        });
    }
    @OnClick({R.id.id_nmpl_ll,R.id.id_commit_comment_tv}) void click(View view){
        switch (view.getId()){
            case R.id.id_nmpl_ll:
                if (isNmComment){
                    nimingIv.setImageResource(R.mipmap.radio_icon_normal);
                } else {
                    nimingIv.setImageResource(R.mipmap.radio_icon_check);
                }
                isNmComment = !isNmComment;
                break;
            case R.id.id_commit_comment_tv:
                if (BmobUser.isLogin()){
                    commitComment();
                }else {
                    ToasUtils.showToastMessage("登陆后方可评论");
                }
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        //拍照or相册
        if(adapter!=null)adapter.activityResult(requestCode,resultCode,data);
    }
    private void commitComment(){
        String content = commentEdit.getText().toString().trim();
        if (content.length()<8){
            ToasUtils.showToastMessage("评价至少输入8个字");
            return;
        }
        float rat = materialRatingBar.getRating();
        if (rat <= 0){
            ToasUtils.showToastMessage("给个评分呗！");
            return;
        }
        CommentBean commentBean = new CommentBean();
        commentBean.content = content;
        commentBean.imgs = adapter.getUploadImgUrlArrays();
        commentBean.score = String.valueOf(rat);
        commentBean.isInonymous = isNmComment;//是否匿名评论
        commentBean.shopId = mOrderBean.shopId;
        commentBean.orderId = getIntent().getStringExtra(Constant.OBJECTID);
        commentBean.userId = mOrderBean.userId;
        commentBean.isPass = true;
        commentBean.userPortrait = BmobUser.getCurrentUser(UserBean.class).logo;
        String nickname = BmobUser.getCurrentUser(UserBean.class).nickname;//昵称
        String username = BmobUser.getCurrentUser(UserBean.class).getUsername();//登陆用户名
        commentBean.userName = nickname != null ? nickname : username;
        commentBean.save(new SaveListener<String>(){
            @Override
            public void done(String s, BmobException e) {
                if (e==null) changeOrder();
            }
        });
    }
    //改变订单状态为已完成
    private void changeOrder(){
        OrderBean orderBean = new OrderBean();
        orderBean.state = Constant.ORDER_STATE_COMPLETE;
        orderBean.update(getIntent().getStringExtra(Constant.OBJECTID), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e==null){
                    MessageDialog.show(CommentActivity.this, "提示", "恭喜你，评论成功！", "知道啦,关闭", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                }
            }
        });
    }
}
