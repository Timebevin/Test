package com.zt.school.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.zt.school.R;
import com.zt.school.adapter.OrderStateListAdapter;
import com.zt.school.base.BaseActivity;
import com.zt.school.bmob.AddressBean;
import com.zt.school.bmob.OrderBean;
import com.zt.school.bmob.ShopBean;
import com.zt.school.bmob.UserBean;
import com.zt.school.confige.Constant;
import com.zt.school.utils.CallPhoneUtils;
import com.zt.school.utils.CustomListView;
import com.zt.school.utils.GlideUtils;
import com.zt.school.utils.NetworkUtil;
import com.zt.school.utils.ToasUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * Author: 九七
 */
public class ShopReceiveOrderDetailActivity extends BaseActivity {
    @BindView(R.id.id_base_title_tv)
    TextView mTitleTv;
    @BindView(R.id.id_pay_state_tv)
    TextView orderStateTv;
    @BindView(R.id.id_time_tv)
    TextView createTimeTv;
    @BindView(R.id.id_top_tv)
    RoundTextView optionButton;
    @BindView(R.id.order_detail_top_rl)
    RelativeLayout relativeLayout;

    @BindView(R.id.id_cus_listview)
    CustomListView customListView;
    @BindView(R.id.id_address_tv)
    TextView addressTv;
    @BindView(R.id.id_order_number_tv)
    TextView orderNumberTv;
    @BindView(R.id.id_order_time_tv)
    TextView orderTimeTV;
    @BindView(R.id.id_remarks_tv)
    TextView remarksTv;
    @BindView(R.id.id_pay_type)
    TextView payTypeTv;

    @BindView(R.id.id_user_logo_iv)
    ImageView userLogoIv;
    @BindView(R.id.id_user_name_tv)
    TextView userNameTv;
    @BindView(R.id.id_user_mobile_tv)
    TextView userMobileTv;
    @BindView(R.id.id_user_motto_tv)
    TextView userMottoTv;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_shop_receive_order_detail;
    }
    @Override
    protected void initView() {
        mTitleTv.setText("订单详情");
    }

    @Override
    protected boolean setStatusBarFontColorIsBlack() {
        return true;
    }

    private OrderBean mOrderBean;
    private ShopBean mShopBean;
    private UserBean mUserBean;
    @Override
    protected void initData(){
        loadData();
    }
    private void loadData(){
        BmobQuery<OrderBean> query = new BmobQuery<>();
        String objectId = getIntent().getStringExtra(Constant.OBJECTID);
        query.getObject(objectId, new QueryListener<OrderBean>() {
            @Override
            public void done(OrderBean orderBean, BmobException e) {
                if (e==null){
                    mOrderBean = orderBean;
                    init(orderBean);
                    findShop(orderBean.shopId);
                    findUser(orderBean.userId);
                }else {
                    if (!NetworkUtil.checkedNetWork(ShopReceiveOrderDetailActivity.this)){
                        ToasUtils.showToastMessage("网络链接异常");
                    }
                }
            }
        });
    }
    //查询商家信息
    private void findShop(String objectId){
        BmobQuery<ShopBean> query = new BmobQuery<>();
        query.getObject(objectId, new QueryListener<ShopBean>() {
            @Override
            public void done(ShopBean shopBean, BmobException e) {
                if (e==null){
                    mShopBean = shopBean;
                }
            }
        });
    }
    //查询顾客基本信息
    public void findUser(String objectId){
        BmobQuery<UserBean> query = new BmobQuery<>();
        query.getObject(objectId, new QueryListener<UserBean>() {
            @Override
            public void done(UserBean userBean, BmobException e) {
                if (e==null){
                    mUserBean = userBean;
                    GlideUtils.load(userBean.logo,userLogoIv);
                    userNameTv.setText(userBean.nickname!=null?userBean.nickname:userBean.getUsername());
                    userMobileTv.setText(userBean.getMobilePhoneNumber()!=null?userBean.getMobilePhoneNumber():"");
                    userMottoTv.setText(userBean.motto != null ? userBean.motto : getResources().getString(R.string.default_motto));
                }
            }
        });
    }
    private void init(OrderBean orderBean){
        String msg = "";
        String noti = "";
        int bgColor = 0;
        switch (orderBean.state){
            case Constant.ORDER_STATE_WAIT_PAY:
                msg = "订单状态 : 待付款";
                noti = "立即付款";
                bgColor = R.color.tab_shop_color;
                break;
            case Constant.ORDER_STATE_PAY_SUCCESS:
                msg = "订单状态 : 顾客付款成功，等待商家接单";
                bgColor = R.color.tab_area_color;
                optionButton.setVisibility(View.GONE);
                break;
            case Constant.ORDER_STATE_SHOP_HAVE_RECEIPT:
                msg = "订单状态 : 商家已接单/餐品制作完成/骑手正在送货/等待顾客确认收货";
                noti = "确认收货";
                bgColor = R.color.tab_area_color;
                break;
            case Constant.ORDER_STATE_SHOP_REFUSE_RECEIPT:
                msg = "订单状态 : 抱歉！商家由于特殊原因取消了该订单-系统将会为您自动退款";//哈哈这部待实现哈
                bgColor = R.color.normal_select_color;
                optionButton.setVisibility(View.GONE);
                break;
            case Constant.ORDER_STATE_WAIT_COMMENT:
                msg = "订单状态 : 订单交易完成，顾客未评价";
                noti = "去评价";
                bgColor = R.color.pay_succ_color;
                break;
            case Constant.ORDER_STATE_COMPLETE:
                msg = "订单状态 : 订单交易完成，顾客已评价";
                bgColor = R.color.tab_area_color;
                optionButton.setVisibility(View.GONE);
                break;
            case Constant.ORDER_STATE_APPLY_REFUND:
                msg = "订单状态 : 申请退款中";
                bgColor = R.color.tint;
                optionButton.setVisibility(View.GONE);
                break;
            case Constant.ORDER_STATE_REFUND_SUCCESS:
                msg = "订单状态 : 退款成功";
                bgColor = R.color.tab_area_color;
                optionButton.setVisibility(View.GONE);
                break;
            case Constant.ORDER_STATE_REFUND_FAIL:
                msg = "订单状态 : 退款失败";
                bgColor = R.color.normal_select_color;
                optionButton.setVisibility(View.GONE);
                break;
            case Constant.ORDER_STATE_USER_CANCEL:
                msg = "订单状态 : 订单已取消";
                bgColor = R.color.normal_select_color;
                optionButton.setVisibility(View.GONE);
                break;
        }
        orderStateTv.setText(msg);
        createTimeTv.setText("下单时间 : "+orderBean.getCreatedAt());
        relativeLayout.setBackgroundColor(getResources().getColor(bgColor));
        optionButton.setText(noti);

        List<OrderBean> list = new ArrayList<>();
        list.add(orderBean);
        customListView.setFocusable(false);
        customListView.setAdapter(new OrderStateListAdapter(this,list,R.layout.layout_item_order_sate,true));

        //配送地址
        BmobQuery<AddressBean> addressBeanBmobQuery = new BmobQuery<>();
        addressBeanBmobQuery.getObject(orderBean.addressId, new QueryListener<AddressBean>() {
            @Override
            public void done(AddressBean addressBean, BmobException e) {
                if (e==null){
                    addressTv.setText(addressBean.province+"-"+addressBean.city+"-"+addressBean.area+"-"+addressBean.detailAddress);
                }
            }
        });
        //订单信息
        orderNumberTv.setText(orderBean.orderNumber);
        orderTimeTV.setText(orderBean.getCreatedAt());
        remarksTv.setText(orderBean.remarks+"");
        payTypeTv.setText("在线支付("+orderBean.payType+")");
        number = orderBean.orderNumber;

        //按钮点击事件(此按钮已在xml文件隐藏，若有需要可自行打开，设计相关点击事件逻辑。
        // 此处相关的逻辑点击事件已在上一个列表界面实现（OrderShopStateListAdapter.java），故此处不做实现
        optionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @OnClick({R.id.id_copy_tv,R.id.id_call_iv}) void click(View view){
        switch (view.getId()){
            case R.id.id_copy_tv:
                copy();
                break;
            case R.id.id_call_iv:
                if (mUserBean!=null){
                    String title = mUserBean.nickname != null ? mUserBean.nickname : mUserBean.getUsername();
                    CallPhoneUtils.callPhone(this,title,mUserBean.getMobilePhoneNumber());
                }
                break;
        }
    }
    String number="";
    private void copy(){
        ClipboardManager copy = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        copy.setText(number);
        ToasUtils.showToastMessage("复制成功");
    }
}
