package com.zt.school.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.zt.school.R;
import com.zt.school.base.BaseActivity;
import com.zt.school.bmob.AppKeyBean;
import com.zt.school.confige.Constant;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Author: 九七
 * 订单支付成功界面
 */

public class PaySuccessActivity extends BaseActivity {
    @BindView(R.id.id_base_title_tv)
    TextView mTitleTv;
    @BindView(R.id.back_view)
    View backView;
    @BindView(R.id.pay_number_tv)
    TextView payNumberTv;
    @BindView(R.id.pay_type_tv)
    TextView payTypeTv;
    @BindView(R.id.pay_price_tv)
    TextView payPriceTv;
    @BindView(R.id.pay_time_tv)
    TextView payTimeTv;
    @Override
    protected int setContentViewId() {
        return R.layout.activity_pay_success;
    }

    @Override
    protected int setStatueBarColor() {
        return R.color.pay_succ_color;
    }

    @Override
    protected void initView() {
        mTitleTv.setText("支付详情");
        backView.setBackgroundColor(getResources().getColor(R.color.pay_succ_color));
        String number = getIntent().getStringExtra("order_number");
        int payType = getIntent().getIntExtra("pay_type",Constant.PAY_WX);
        String price = getIntent().getStringExtra("pay_price");
        String time = getIntent().getStringExtra("pay_time");
        String type;
        if (payType==Constant.PAY_WX){
            type = "微信支付";
        }else {
            type = "支付宝支付";
        }
        payNumberTv.setText("订单编号：" + number);
        payTypeTv.setText("支付方式：" + type);
        payPriceTv.setText("支付金额：" + Constant.PRICE_MARK+""+price);
        payTimeTv.setText("支付时间：" + time);
    }

    @OnClick({R.id.go_home_tv,R.id.go_detail_tv}) void getHome(View view){
        switch (view.getId()){
            case R.id.go_home_tv:
                setResult(201);
                finish();
                break;
            case R.id.go_detail_tv:
                enteryOrderDetail(getIntent().getStringExtra(Constant.OBJECTID));
                break;
        }
    }

    //重写左上角退出图标点击事件
    @Override
    protected void doBackClick(View view) {
        setResult(202);
        finish();
    }
    //重写back按键返回事件
    @Override
    public void onBackPressed() {
        setResult(202);
        super.onBackPressed();
    }

    //禁用侧滑
    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }
    private void enteryOrderDetail(String orderId){
        //查询AppKeyBean表,获取支付所需的appkey
        BmobQuery<AppKeyBean> query = new BmobQuery<>();
        query.findObjects(new FindListener<AppKeyBean>(){
            @Override
            public void done(List<AppKeyBean> list, BmobException e) {
                Intent intent = new Intent(PaySuccessActivity.this, OrderDetailActivity.class);
                if (e==null && list!=null && list.size()>0){
                    intent.putExtra("appkey", list.get(0).appkey);//使用第一条记录的appkey
                }else {
                    intent.putExtra("appkey","appkey");
                }
                intent.putExtra(Constant.OBJECTID, orderId);
                startActivity(intent);
            }
        });
    }
}
