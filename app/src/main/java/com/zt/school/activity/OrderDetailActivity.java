package com.zt.school.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.base.bj.paysdk.domain.TrPayResult;
import com.base.bj.paysdk.listener.PayResultListener;
import com.base.bj.paysdk.utils.TrPay;
import com.flyco.roundview.RoundTextView;
import com.kongzue.dialog.listener.OnMenuItemClickListener;
import com.kongzue.dialog.v2.BottomMenu;
import com.kongzue.dialog.v2.SelectDialog;
import com.zt.school.R;
import com.zt.school.adapter.OrderStateListAdapter;
import com.zt.school.base.BaseActivity;
import com.zt.school.bmob.AddressBean;
import com.zt.school.bmob.OrderBean;
import com.zt.school.bmob.ShopBean;
import com.zt.school.confige.Constant;
import com.zt.school.utils.CustomListView;
import com.zt.school.utils.NetworkUtil;
import com.zt.school.utils.ToasUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Author: 九七
 * 订单详情页
 */
public class OrderDetailActivity extends BaseActivity {
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

    @Override
    protected int setContentViewId() {
        return R.layout.activity_order_detail;
    }
    @Override
    protected void initView() {
        mTitleTv.setText("订单详情");
        TrPay.getInstance(this).initPaySdk(getIntent().getStringExtra("appkey"),"360");
    }

    @Override
    protected boolean setStatusBarFontColorIsBlack() {
        return true;
    }

    private OrderBean mOrderBean;
    private ShopBean mShopBean;
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
                }else {
                    if (!NetworkUtil.checkedNetWork(OrderDetailActivity.this)){
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
                msg = "订单状态 : 付款成功，等待商家接单";
                bgColor = R.color.tab_area_color;
                optionButton.setVisibility(View.GONE);
                break;
            case Constant.ORDER_STATE_SHOP_HAVE_RECEIPT:
                msg = "订单状态 : 骑手正在赶来，待使用";
                noti = "确认收货";
                bgColor = R.color.tab_area_color;
                break;
            case Constant.ORDER_STATE_SHOP_REFUSE_RECEIPT:
                msg = "订单状态 : 抱歉！商家由于特殊原因取消了该订单-系统将会为您自动退款";//这步待实现
                bgColor = R.color.normal_select_color;
                optionButton.setVisibility(View.GONE);
                break;
            case Constant.ORDER_STATE_WAIT_COMMENT:
                msg = "订单状态 : 待评价";
                noti = "去评价";
                bgColor = R.color.pay_succ_color;
                break;
            case Constant.ORDER_STATE_COMPLETE:
                msg = "订单状态 : 已完成";
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

        //按钮点击事件
        optionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (orderBean.state){
                    case Constant.ORDER_STATE_WAIT_PAY:
                        //立即付款
                        shopPayBottomDailog();
                        break;
                    case Constant.ORDER_STATE_SHOP_HAVE_RECEIPT:
                        //确认收货
                        confirmOrder();
                        break;
                    case Constant.ORDER_STATE_WAIT_COMMENT:
                        //去评论
                        Intent intent = new Intent(OrderDetailActivity.this, CommentActivity.class);
                        intent.putExtra(Constant.OBJECTID, getIntent().getStringExtra(Constant.OBJECTID));
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    @OnClick({R.id.id_copy_tv}) void click(View view){
        switch (view.getId()){
            case R.id.id_copy_tv:
                copy();
                break;
        }
    }
    String number="";
    private void copy(){
        ClipboardManager copy = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        copy.setText(number);
        ToasUtils.showToastMessage("复制成功");
    }
    private void pay(){
        //发起支付所需参数
        String userid = mShopBean.getObjectId();//商户系统用户ID(如：trpay@52yszd.com，商户系统内唯一)
        String outtradeno = UUID.randomUUID() + "";//商户系统订单号(为便于演示，此处利用UUID生成模拟订单号，商户系统内唯一)
        String tradename = mShopBean.name;//商品名称
        String backparams = "name=2&age=22";//商户系统回调参数
        String notifyurl = "http://101.200.13.92/notify/alipayTestNotify";//商户系统回调地址
        if (TextUtils.isEmpty(tradename)){
            Toast.makeText(OrderDetailActivity.this, "请输入商品名称！", Toast.LENGTH_SHORT).show();
            return;
        }
        String ss = changeBranch(mOrderBean.totalPrice);
        long amount = Long.valueOf(ss);//商品价格（单位：分/角/毛。如1.5元传150）
        if (amount < 1) {
            Toast.makeText(OrderDetailActivity.this, "金额不能小于1分！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (payType == Constant.PAY_ZFB) {
            /**
             * 发起支付宝支付调用
             */
            TrPay.getInstance(this).callAlipay(tradename, outtradeno, amount, backparams, notifyurl, userid, new PayResultListener() {
                /**
                 * 支付完成回调
                 * @param context      上下文
                 * @param outtradeno   商户系统订单号
                 * @param resultCode   支付状态(RESULT_CODE_SUCC：支付成功、RESULT_CODE_FAIL：支付失败)
                 * @param resultString 支付结果
                 * @param payType      支付类型（1：支付宝 2：微信）
                 * @param amount       支付金额
                 * @param tradename    商品名称
                 */
                @Override
                public void onPayFinish(Context context, String outtradeno, int resultCode, String resultString, int payType, Long amount, String tradename) {
                    if (resultCode == TrPayResult.RESULT_CODE_SUCC.getId()) {//1：支付成功回调
//                        TrPay.getInstance((Activity) context).closePayView();//关闭快捷支付页面
                        //支付成功逻辑处理
                        orderPaySuccess();
                    } else if (resultCode == TrPayResult.RESULT_CODE_FAIL.getId()) {//2：支付失败回调
                        //支付失败逻辑处理
                        Toast.makeText(OrderDetailActivity.this, "支付取消", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else if (payType == Constant.PAY_WX) {
            /**
             * 发起微信支付调用
             */
            TrPay.getInstance(this).callWxPay(tradename, outtradeno, amount, backparams, notifyurl, userid, new PayResultListener() {
                /**
                 * 支付完成回调
                 * @param context      上下文
                 * @param outtradeno   商户系统订单号
                 * @param resultCode   支付状态(RESULT_CODE_SUCC：支付成功、RESULT_CODE_FAIL：支付失败)
                 * @param resultString 支付结果
                 * @param payType      支付类型（1：支付宝 2：微信）
                 * @param amount       支付金额
                 * @param tradename    商品名称
                 */
                @Override
                public void onPayFinish(Context context, String outtradeno, int resultCode, String resultString, int payType, Long amount, String tradename) {
                    if (resultCode == TrPayResult.RESULT_CODE_SUCC.getId()) {//1：支付成功回调
//                        TrPay.getInstance((Activity) context).closePayView();//关闭快捷支付页面//支付成功逻辑处理
                        orderPaySuccess();
                    } else if (resultCode == TrPayResult.RESULT_CODE_FAIL.getId()) {//2：支付失败回调
                        //支付失败逻辑处理
                        Toast.makeText(OrderDetailActivity.this, "支付取消", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    /**
     * 将元为单位的转换为分 （乘100）
     */
    public static String changeBranch(Long amount) {
        return BigDecimal.valueOf(amount).multiply(new BigDecimal(100)).toString();
    }

    /**
     * 将元为单位的转换为分 替换小数点，支持以逗号区分的金额
     */
    public static String changeBranch(String amount) {
        String currency = amount.replaceAll("\\$|\\￥|\\,", ""); // 处理包含, ￥
        // 或者$的金额
        int index = currency.indexOf(".");
        int length = currency.length();
        Long amLong;
        if (index == -1) {
            amLong = Long.valueOf(currency + "00");
        } else if (length - index >= 3) {
            amLong = Long.valueOf((currency.substring(0, index + 3)).replace(".", ""));
        } else if (length - index == 2) {
            amLong = Long.valueOf((currency.substring(0, index + 2)).replace(".", "") + 0);
        } else {
            amLong = Long.valueOf((currency.substring(0, index + 1)).replace(".", "") + "00");
        }
        return amLong.toString();
    }
    private int payType = Constant.PAY_WX;
    private void shopPayBottomDailog(){
        List<String> list = new ArrayList<>();
        list.add("微信");
        list.add("支付宝");
        BottomMenu.show(this, list, new OnMenuItemClickListener() {
            @Override
            public void onClick(String text, int index) {
                switch (index) {
                    case 0:
                        payType = Constant.PAY_WX;
                        break;
                    case 1:
                        payType = Constant.PAY_ZFB;
                        break;
                }
                pay();
            }
        }, true).setTitle("请选择支付方式");
    }
    //支付成功改变订单为支付成功状态
    private void orderPaySuccess(){
        OrderBean bean = new OrderBean();
        if (payType==Constant.PAY_WX){
            bean.payType = "微信";
        }else {
            bean.payType = "支付宝";
        }
        bean.state = Constant.ORDER_STATE_PAY_SUCCESS;
        bean.update(getIntent().getStringExtra(Constant.OBJECTID), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    //重试请求数据，刷新各ui数据和状态
                    loadData();
                }
            }
        });
    }
    //确认收货
    private void confirmOrder(){
        SelectDialog.show(this, "提示", "确认已收到该餐品?", "确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                OrderBean orderBean = new OrderBean();
                orderBean.state = Constant.ORDER_STATE_WAIT_COMMENT;
                orderBean.update(getIntent().getStringExtra(Constant.OBJECTID), new UpdateListener(){
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            //重试请求数据，刷新各ui数据和状态
                            loadData();
                        }
                    }
                });
            }
        }).setCanCancel(true);
    }
}
