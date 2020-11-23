package com.zt.school.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.google.gson.Gson;
import com.kongzue.dialog.v2.SelectDialog;
import com.zt.school.R;
import com.zt.school.activity.CommentActivity;
import com.zt.school.activity.OrderDetailActivity;
import com.zt.school.activity.ShopFoodActivity;
import com.zt.school.bmob.AppKeyBean;
import com.zt.school.bmob.FoodBean;
import com.zt.school.bmob.OrderBean;
import com.zt.school.bmob.ShopBean;
import com.zt.school.common.CommonAdapter;
import com.zt.school.common.CommonViewHolder;
import com.zt.school.confige.Constant;
import com.zt.school.utils.CallPhoneUtils;
import com.zt.school.utils.CustomListView;
import com.zt.school.utils.GlideUtils;
import com.zt.school.utils.ToasUtils;
import com.zt.school.views.OrderStateTabPage;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Author: 九七
 */

public class OrderStateListAdapter extends CommonAdapter<OrderBean> {
    Gson gson = new Gson();
    boolean hideBotton;
    OrderStateTabPage orderStateTabPage;
    public OrderStateListAdapter(Context context, List<OrderBean> datas, int layoutId,boolean hideBotton) {
        super(context, datas, layoutId);
        this.hideBotton = hideBotton;
    }

    public OrderStateListAdapter(Context context, List<OrderBean> datas, int layoutId,OrderStateTabPage orderStateTabPage) {
        super(context, datas, layoutId);
        this.orderStateTabPage = orderStateTabPage;
    }

    @Override
    public void convert(CommonViewHolder helper, OrderBean item) {
        GlideUtils.load(item.shopLogo, helper.getView(R.id.logo));
        helper.setTvText(R.id.shop_name_tv, item.shopName)
                .setTvText(R.id.id_count_tv, "共计" + item.total+ "件商品合计:")
                .setTvText(R.id.id_cost_pri, "￥" + item.totalPrice);
        RoundTextView button1 = helper.getView(R.id.id_tv1);
        RoundTextView button2 = helper.getView(R.id.id_tv2);
        TextView stateTv = helper.getView(R.id.id_order_state_tv);
        switch (item.state){
            case Constant.ORDER_STATE_WAIT_PAY:
                stateTv.setText("待付款");
                button1.setVisibility(View.VISIBLE);
                button2.setVisibility(View.VISIBLE);
                button1.setText("取消订单");
                button2.setText("付款");
                break;
            case Constant.ORDER_STATE_PAY_SUCCESS:
                stateTv.setText("付款成功，等待商家接单");
                button1.setVisibility(View.GONE);
                button2.setVisibility(View.GONE);
                break;
            case Constant.ORDER_STATE_SHOP_HAVE_RECEIPT://
                stateTv.setText("待使用");
                button1.setVisibility(View.GONE);
                button2.setVisibility(View.VISIBLE);
                button2.setText("确认收货");
                break;
            case Constant.ORDER_STATE_SHOP_REFUSE_RECEIPT://
                stateTv.setText("商家取消了订单");
                button1.setVisibility(View.VISIBLE);
                button2.setVisibility(View.VISIBLE);
                button1.setText("联系商家");
                button2.setText("删除");
                break;
            case Constant.ORDER_STATE_WAIT_COMMENT:
                stateTv.setText("待评价");
                button1.setVisibility(View.VISIBLE);
                button2.setVisibility(View.VISIBLE);
                button1.setText("删除");
                button2.setText("评价");
                break;
            case Constant.ORDER_STATE_COMPLETE:
                stateTv.setText("已完成");
                button1.setVisibility(View.VISIBLE);
                button2.setVisibility(View.VISIBLE);
                button1.setText("再来一单");
                button2.setText("删除");
                break;
            case Constant.ORDER_STATE_APPLY_REFUND:
                stateTv.setText("申请退款/售后");
                button1.setVisibility(View.GONE);
                button2.setVisibility(View.VISIBLE);
                button2.setText("申请退款/售后");
                break;
            case Constant.ORDER_STATE_REFUND_SUCCESS:
                stateTv.setText("退款成功");
                button1.setVisibility(View.GONE);
                button2.setVisibility(View.GONE);
                break;
            case Constant.ORDER_STATE_REFUND_FAIL:
                stateTv.setText("退款失败");
                button1.setVisibility(View.GONE);
                button2.setVisibility(View.GONE);
                break;
            case Constant.ORDER_STATE_USER_CANCEL:
                stateTv.setText("已取消");
                button1.setVisibility(View.GONE);
                button2.setVisibility(View.VISIBLE);
                button2.setText("删除");
                break;
        }
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (item.state){
                    case Constant.ORDER_STATE_WAIT_PAY:
                        //取消订单
                        cancelOrder(item.getObjectId());
                        break;
                    case Constant.ORDER_STATE_WAIT_COMMENT:
                        //删除
                        deleteOrder(item.getObjectId(),helper.getmPosition());
                        break;
                    case Constant.ORDER_STATE_COMPLETE:
                        //再来一单
                        againOrder(item.shopId);
                        break;
                    case Constant.ORDER_STATE_SHOP_REFUSE_RECEIPT:
                        //联系商家
                        callShopMobile(item.shopId);
                        break;
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (item.state){
                    case Constant.ORDER_STATE_WAIT_PAY:
                        //付款
                        pay(item.getObjectId());
                        break;
                    case Constant.ORDER_STATE_SHOP_HAVE_RECEIPT:
                        //确认收货
                        confirmOrder(item.getObjectId());
                        break;
                    case Constant.ORDER_STATE_SHOP_REFUSE_RECEIPT:
                        //删除
                        deleteOrder(item.getObjectId(),helper.getmPosition());
                        break;
                    case Constant.ORDER_STATE_WAIT_COMMENT:
                        //评价
                        commentOrder(item.getObjectId());
                        break;
                    case Constant.ORDER_STATE_COMPLETE:
                        //删除
                        deleteOrder(item.getObjectId(),helper.getmPosition());
                        break;
                    case Constant.ORDER_STATE_APPLY_REFUND:
                        //申请退款/售后
                        ToasUtils.showToastMessage("该功能待实现");
                        break;
                    case Constant.ORDER_STATE_USER_CANCEL:
                        //删除
                        deleteOrder(item.getObjectId(),helper.getmPosition());
                        break;
                }
            }
        });

        if (hideBotton)helper.getView(R.id.id_bottom_view).setVisibility(View.GONE);

        CustomListView itemRecycleView = helper.getView(R.id.id_list);
        //https://www.cnblogs.com/LT5505/p/5972999.html
        itemRecycleView.setFocusable(false);
        itemRecycleView.setClickable(false);
        itemRecycleView.setPressed(false);
        itemRecycleView.setEnabled(false);
        String orderArray = item.orderArray;
        try {
            JSONArray jsonArray = new JSONArray(orderArray);
            List<FoodBean> foodBeanList = new ArrayList<>();
            for (int i=0;i<jsonArray.length();i++){
                String obj = jsonArray.getString(i);
                FoodBean foodBean = gson.fromJson(obj, FoodBean.class);
                foodBeanList.add(foodBean);
            }
            //update lv
            ListItemListAdapter adapter;
            itemRecycleView.setAdapter(adapter = new ListItemListAdapter(mContext,foodBeanList,R.layout.layout_item_list_reycle,item.getCreatedAt()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void clear(){
        mDatas.clear();
        notifyDataSetChanged();
    }
    //取消订单
    private void cancelOrder(String orderId){
        SelectDialog.show(mContext, "提示", "确定要取消该订单吗?", "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                OrderBean orderBean = new OrderBean();
                orderBean.state = Constant.ORDER_STATE_USER_CANCEL;
                orderBean.update(orderId, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            //列表重试请求数据，刷新ui
                            orderStateTabPage.refreshListData();
                        }
                    }
                });
            }
        }).setCanCancel(true);
    }
    //确认收货
    private void confirmOrder(String orderId){
        SelectDialog.show(mContext, "提示", "确认已收到该餐品?", "确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                OrderBean orderBean = new OrderBean();
                orderBean.state = Constant.ORDER_STATE_WAIT_COMMENT;
                orderBean.update(orderId, new UpdateListener(){
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            //列表重试请求数据，刷新ui
                            orderStateTabPage.refreshListData();
                        }
                    }
                });
            }
        }).setCanCancel(true);
    }
    //删除
    private void deleteOrder(String orderId,int position){
        SelectDialog.show(mContext, "提示", "确定要删除该订单吗?", "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                OrderBean orderBean = new OrderBean();
                orderBean.delete(orderId, new UpdateListener(){
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            //列表移除该item
                            OrderStateListAdapter.this.mDatas.remove(position);
                            OrderStateListAdapter.this.notifyDataSetChanged();
                        }
                    }
                });
            }
        }).setCanCancel(true);
    }
    //再来一单
    private void againOrder(String shopId){
        BmobQuery<ShopBean> query = new BmobQuery<>();
        query.getObject(shopId, new QueryListener<ShopBean>() {
            @Override
            public void done(ShopBean shopBean, BmobException e) {
                if (e==null){
                    //此处直接跳转到商家点餐界面
                    Intent intent = new Intent(mContext, ShopFoodActivity.class);
                    intent.putExtra("shop", shopBean);
                    mContext.startActivity(intent);
                }
            }
        });
    }
    //付款(跳转到订单详情页进行付款)
    private void pay(String orderId){
        //查询AppKeyBean表,获取支付所需的appkey
        BmobQuery<AppKeyBean> query = new BmobQuery<>();
        query.findObjects(new FindListener<AppKeyBean>(){
            @Override
            public void done(List<AppKeyBean> list, BmobException e) {
                Intent intent = new Intent(mContext, OrderDetailActivity.class);
                if (e==null && list!=null && list.size()>0){
                    intent.putExtra("appkey", list.get(0).appkey);//使用第一条记录的appkey
                }else {
                    intent.putExtra("appkey","appkey");
                }
                intent.putExtra(Constant.OBJECTID, orderId);
                mContext.startActivity(intent);
            }
        });
    }
    //评价
    private void commentOrder(String orderId){
        Intent intent = new Intent(mContext, CommentActivity.class);
        intent.putExtra(Constant.OBJECTID, orderId);
        mContext.startActivity(intent);
    }
    //联系商家
    private void callShopMobile(String shopId){
        BmobQuery<ShopBean> query = new BmobQuery<>();
        query.getObject(shopId, new QueryListener<ShopBean>(){
            @Override
            public void done(ShopBean shopBean, BmobException e){
                if (e==null) {
                    SelectDialog.show(mContext, "提示", "确定要联系【" + shopBean.name + "】掌柜吗?", "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Activity activity = (Activity) mContext;
                            CallPhoneUtils.callPhone(activity, shopBean.tel);
                        }
                    }).setCanCancel(true);
                }
            }
        });
    }
}
