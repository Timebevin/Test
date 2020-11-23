package com.zt.school.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.base.bj.paysdk.domain.TrPayResult;
import com.base.bj.paysdk.listener.PayResultListener;
import com.base.bj.paysdk.utils.TrPay;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.jaeger.library.StatusBarUtil;
import com.zt.school.R;
import com.zt.school.adapter.OrderListAdapter;
import com.zt.school.base.BaseActivity;
import com.zt.school.bmob.AddressBean;
import com.zt.school.bmob.AppKeyBean;
import com.zt.school.bmob.FoodBean;
import com.zt.school.bmob.OrderBean;
import com.zt.school.bmob.ShopBean;
import com.zt.school.bmob.UserBean;
import com.zt.school.confige.Constant;
import com.zt.school.utils.CustomListView;
import com.zt.school.utils.GlideUtils;
import com.zt.school.utils.ToasUtils;
import com.zt.school.utils.UIUtils;
import com.zt.school.views.CircleImageView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Author: 九七
 */

public class CommitOrderActivity extends BaseActivity {
    @BindView(R.id.id_base_title_tv)
    TextView mTitleTv;
    @BindView(R.id.id_order_commit_shop_logo)
    CircleImageView circleImageView;
    @BindView(R.id.id_order_commit_shop_name_tv)
    TextView shopNameTv;
    @BindView(R.id.listview)
    CustomListView customListView;
    @BindView(R.id.id_cost_tv2)
    TextView totalPriceTv;

    @BindView(R.id.id_edit_address_rl)
    RelativeLayout notiAddressView;
    @BindView(R.id.id_address_rl)
    RelativeLayout addressRelativeView;

    @BindView(R.id.lxr_tv)
    TextView lxrMobileTv;
    @BindView(R.id.address_tv)
    TextView addressTv;
    @BindView(R.id.id_wx_iv)
    ImageView wxIv;
    @BindView(R.id.id_zfb_iv)
    ImageView zfbIv;
    @BindView(R.id.id_beizu_edit)
    EditText beizuEdit;
    private String addressId;//收货地址id
    private ArrayList<FoodBean> foods;
    private ShopBean shopBean;
    float totalPrice ;
    int total;
    private int payType = Constant.PAY_WX;
    private boolean orderIsCreateSuccess;//订单是否已经创建成功
    private String orderId;//订单id
    @Override
    protected int setContentViewId(){
        return R.layout.activity_commit_pay;
    }
    @Override
    protected boolean setStatusBarFontColorIsBlack(){
        return true;
    }
    @Override
    protected void statueBar() {
        StatusBarUtil.setColor(this,getResources().getColor(R.color.white),0);
    }
    @Override
    protected void initView(){
        //初始化PaySdk(Context请传入当前Activity对象)
        TrPay.getInstance(this).initPaySdk(getIntent().getStringExtra("appkey"),"360");
        mTitleTv.setText("提交订单");
        foods = (ArrayList<FoodBean>) getIntent().getSerializableExtra("list");
        shopBean = (ShopBean) getIntent().getSerializableExtra("shop");
        GlideUtils.load(shopBean.logo,circleImageView);
        shopNameTv.setText(shopBean.name);
        customListView.setAdapter(new OrderListAdapter(this,foods,R.layout.item_cart_list));
        for (FoodBean foodBean : foods){
            totalPrice = totalPrice + Float.valueOf(foodBean.price) * foodBean.total;
            total = total + foodBean.total;
        }
        totalPriceTv.setText("合计:"+ Constant.PRICE_MARK+totalPrice+"元");
    }

    @Override
    protected void initData(){
        BmobQuery<AddressBean> query = new BmobQuery<>();
        UserBean user = BmobUser.getCurrentUser(UserBean.class);
        query.addWhereEqualTo("userId", user.getObjectId());
        query.findObjects(new FindListener<AddressBean>(){
            @Override
            public void done(List<AddressBean> list, BmobException e){
                if (e==null && list!=null && list.size()>0){
                    notiAddressView.setVisibility(View.INVISIBLE);
                    addressRelativeView.setVisibility(View.VISIBLE);
                    //获取第一个地址作为默认的收货地址
                    AddressBean address = list.get(0);
                    lxrMobileTv.setText(address.name + "  " + address.mobile);
                    String detailAddress = address.province + " " + address.city + " " + address.area + " " + address.detailAddress;
                    addressTv.setText(detailAddress);
                    addressId = address.getObjectId();
                }else {
                    notiAddressView.setVisibility(View.VISIBLE);
                    addressRelativeView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
    @OnClick({R.id.id_edit_address_rl,R.id.id_address_rl,R.id.id_show_food_yddc_tv,R.id.pay_wx,R.id.pay_zfb}) void click(View view){
        switch (view.getId()){
            case R.id.id_edit_address_rl:
                Intent intent = new Intent(this, ReciveAddressListActivity.class);
                startActivityForResult(intent,100);
                break;
            case R.id.id_address_rl:
                intent = new Intent(this, ReciveAddressListActivity.class);
                startActivityForResult(intent,100);
                break;
            case R.id.id_show_food_yddc_tv:
                if (addressId==null){
                    ToasUtils.showToastMessage("请添加收货地址");
                    return;
                }
                if (orderIsCreateSuccess){
                    pay();
                }else {
                    createOrder();
                }
                break;
            case R.id.pay_wx:
                payType = Constant.PAY_WX;
                wxIv.setImageResource(R.mipmap.radio_icon_check);
                zfbIv.setImageResource(R.mipmap.radio_icon_normal);
                break;
            case R.id.pay_zfb:
                payType = Constant.PAY_ZFB;
                zfbIv.setImageResource(R.mipmap.radio_icon_check);
                wxIv.setImageResource(R.mipmap.radio_icon_normal);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==100 && resultCode==200){
            notiAddressView.setVisibility(View.INVISIBLE);
            addressRelativeView.setVisibility(View.VISIBLE);

            AddressBean address = (AddressBean) data.getSerializableExtra("address");
            lxrMobileTv.setText(address.name + "  " + address.mobile);
            String detailAddress = address.province + " " + address.city + " " + address.area + " " + address.detailAddress;
            addressTv.setText(detailAddress);
            addressId = address.getObjectId();
        }else if (requestCode ==100 && resultCode==201){
            setResult(201);
            finish();
        }else if (requestCode ==100 && resultCode==202){
            finish();
        }
    }

    private String orderNumber,mTotalPrice,orderCreateTime;
    //创建订单
    private void createOrder(){
        //foods 集合转化为json数组
        Gson gson = new Gson();
        JsonArray jsonArray = gson.toJsonTree(foods, new TypeToken<List<FoodBean>>() {}.getType()).getAsJsonArray();
        OrderBean orderBean = new OrderBean();
        UserBean user = BmobUser.getCurrentUser(UserBean.class);
        orderBean.shopId = shopBean.getObjectId();
        orderBean.userId = user.getObjectId();
        orderBean.orderArray = jsonArray.toString();
        orderBean.state = Constant.ORDER_STATE_WAIT_PAY;
        orderBean.shopLogo = shopBean.logo;
        orderBean.shopName = shopBean.name;
        orderBean.orderNumber = "no" + user.getObjectId() +  UIUtils.getCurrentSysTimeStamp();
        orderNumber = orderBean.orderNumber;
        orderBean.totalPrice = String.valueOf(totalPrice);
        mTotalPrice = orderBean.totalPrice;
        orderBean.total = total;
        orderBean.remarks = beizuEdit.getText().toString().trim();
        orderBean.addressId = addressId;
        orderBean.userName = user.nickname != null ? user.nickname : user.getUsername();
        orderBean.userLogo = user.logo;
        if (payType==Constant.PAY_WX){
            orderBean.payType = "微信";
        }else {
            orderBean.payType = "支付宝";
        }
        orderBean.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){
                    orderIsCreateSuccess = true;
                    orderId = orderBean.getObjectId();
                    orderCreateTime = orderBean.getCreatedAt();
                    pay();
                }else {
                    orderIsCreateSuccess = false;
                    ToasUtils.showToastMessage("订单提交失败,请重试 "+ e.getMessage());
                }
            }
        });
    }
    private void pay(){
        //发起支付所需参数
        String userid = shopBean.getObjectId();//商户系统用户ID(如：trpay@52yszd.com，商户系统内唯一)
        String outtradeno = UUID.randomUUID() + "";//商户系统订单号(为便于演示，此处利用UUID生成模拟订单号，商户系统内唯一)
        String tradename = shopBean.name;//商品名称
        String backparams = "name=2&age=22";//商户系统回调参数
        String notifyurl = "http://101.200.13.92/notify/alipayTestNotify";//商户系统回调地址
        if (TextUtils.isEmpty(tradename)){
            Toast.makeText(CommitOrderActivity.this, "请输入商品名称！", Toast.LENGTH_SHORT).show();
            return;
        }
        String ss = changeBranch(String.valueOf(totalPrice));
        long amount = Long.valueOf(ss);//商品价格（单位：分/角/毛。如1.5元传150）
        if (amount < 1) {
            Toast.makeText(CommitOrderActivity.this, "金额不能小于1分！", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(CommitOrderActivity.this, "订单创建成功，等待支付", Toast.LENGTH_LONG).show();
                        orderDetial();
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
                        Toast.makeText(CommitOrderActivity.this, "订单创建成功，等待支付", Toast.LENGTH_LONG).show();
                        orderDetial();
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
    //支付成功改变订单为支付成功状态
    private void orderPaySuccess(){
        OrderBean bean = new OrderBean();
        if (payType==Constant.PAY_WX){
            bean.payType = "微信";
        }else {
            bean.payType = "支付宝";
        }
        bean.remarks = beizuEdit.getText().toString().trim();
        bean.addressId = addressId;
        bean.state = Constant.ORDER_STATE_PAY_SUCCESS;
        bean.update(orderId, new UpdateListener(){
            @Override
            public void done(BmobException e) {
                if(e==null){
                    //订单支付成功显示界面
                    Intent intent = new Intent(CommitOrderActivity.this, PaySuccessActivity.class);
                    intent.putExtra("order_number", orderNumber);
                    intent.putExtra("pay_type", payType);
                    intent.putExtra("pay_price", mTotalPrice);
                    intent.putExtra("pay_time", orderCreateTime);
                    intent.putExtra(Constant.OBJECTID, orderId);
                    CommitOrderActivity.this.startActivityForResult(intent,100);
                }else{
                    ToasUtils.showToastMessage("更新失败：" + e.getMessage());
                }
            }
        });
    }
    //订单已创建，引导用户到订单详情页进行支付
    private void orderDetial(){
        //查询AppKeyBean表,获取支付所需的appkey
        BmobQuery<AppKeyBean> query = new BmobQuery<>();
        query.findObjects(new FindListener<AppKeyBean>(){
            @Override
            public void done(List<AppKeyBean> list, BmobException e) {
                Intent intent = new Intent(CommitOrderActivity.this, OrderDetailActivity.class);
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
