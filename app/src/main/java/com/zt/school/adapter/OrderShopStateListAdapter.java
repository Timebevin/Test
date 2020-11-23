package com.zt.school.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import com.flyco.roundview.RoundTextView;
import com.google.gson.Gson;
import com.kongzue.dialog.v2.SelectDialog;
import com.zt.school.R;
import com.zt.school.activity.UserCommentListActivity;
import com.zt.school.bmob.FoodBean;
import com.zt.school.bmob.OrderBean;
import com.zt.school.common.CommonAdapter;
import com.zt.school.common.CommonViewHolder;
import com.zt.school.confige.Constant;
import com.zt.school.utils.CustomListView;
import com.zt.school.utils.GlideUtils;
import com.zt.school.utils.ToasUtils;
import com.zt.school.views.OrderShopStateTabPage;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Author: 九七
 */

public class OrderShopStateListAdapter extends CommonAdapter<OrderBean> {
    Gson gson = new Gson();
    boolean hideBotton;
    OrderShopStateTabPage orderShopStateTabPage;

    public OrderShopStateListAdapter(Context context, List<OrderBean> datas, int layoutId, boolean hideBotton) {
        super(context, datas, layoutId);
        this.hideBotton = hideBotton;
    }

    public OrderShopStateListAdapter(Context context, List<OrderBean> datas, int layoutId, OrderShopStateTabPage orderShopStateTabPage) {
        super(context, datas, layoutId);
        this.orderShopStateTabPage = orderShopStateTabPage;
    }

    @Override
    public void convert(CommonViewHolder helper, OrderBean item) {
        GlideUtils.load(item.userLogo, helper.getView(R.id.logo));
        helper.setTvText(R.id.shop_name_tv,item.userName!=null?item.userName:"顾客")
                .setTvText(R.id.id_count_tv, "共计" + item.total+ "件商品合计:")
                .setTvText(R.id.id_cost_pri, "￥" + item.totalPrice);
        RoundTextView button1 = helper.getView(R.id.id_tv1);
        RoundTextView button2 = helper.getView(R.id.id_tv2);
        TextView stateTv = helper.getView(R.id.id_order_state_tv);
        switch (item.state){
            case Constant.ORDER_STATE_PAY_SUCCESS://支付成功
                stateTv.setText("顾客付款成功-等待商家接单");
                button1.setVisibility(View.VISIBLE);
                button2.setVisibility(View.VISIBLE);
                button1.setText("取消订单");
                button2.setText("接单");
                break;
            case Constant.ORDER_STATE_SHOP_HAVE_RECEIPT:
                stateTv.setText("餐品制作中-骑手送货-待顾客确认");
                button1.setVisibility(View.GONE);
                button2.setVisibility(View.VISIBLE);
                button2.setText("关闭");
                break;
            case Constant.ORDER_STATE_WAIT_COMMENT://待评价
                //完成
                stateTv.setText("顾客已确认收货-交易完成");
                button1.setVisibility(View.GONE);
                button2.setVisibility(View.VISIBLE);
                button2.setText("关闭");
                break;
            case Constant.ORDER_STATE_COMPLETE://订单交易完成/评价完成
                stateTv.setText("顾客已确认收货-交易完成");
                button1.setVisibility(View.VISIBLE);
                button2.setVisibility(View.VISIBLE);
                button1.setText("关闭");
                button2.setText("查看评价");
                break;
            case Constant.ORDER_STATE_APPLY_REFUND://售后/退款
                stateTv.setText("顾客申请售后/退款");
                button1.setVisibility(View.GONE);
                button2.setVisibility(View.GONE);
                break;
        }
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                switch (item.state){
                    case Constant.ORDER_STATE_PAY_SUCCESS:
                        //取消/拒绝订单
                        cancelOrder(item.getObjectId());
                        break;
                    case Constant.ORDER_STATE_COMPLETE:
                        //关闭订单
                        closeOrder(item.getObjectId(),helper.getmPosition());
                        break;
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (item.state){
                    case Constant.ORDER_STATE_PAY_SUCCESS:
                        //接单
                        receiptOrder(item.getObjectId());
                        break;
                    case Constant.ORDER_STATE_SHOP_HAVE_RECEIPT:
                        //关闭
                        closeOrder(item.getObjectId(),helper.getmPosition());
                        break;
                    case Constant.ORDER_STATE_WAIT_COMMENT:
                        //关闭订单
                        closeOrder(item.getObjectId(),helper.getmPosition());
                        break;
                    case Constant.ORDER_STATE_COMPLETE:
                        //查看评价
                        Intent intent = new Intent(mContext, UserCommentListActivity.class);
                        intent.putExtra("shopId", item.shopId);
                        intent.putExtra("orderId", item.getObjectId());
                        intent.putExtra(Constant.TYPE, 2);
                        mContext.startActivity(intent);
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
        SelectDialog.show(mContext, "提示", "确定要取消/拒绝该订单吗?", "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                OrderBean orderBean = new OrderBean();
                orderBean.state = Constant.ORDER_STATE_SHOP_REFUSE_RECEIPT;
                orderBean.update(orderId, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            //列表重试请求数据，刷新ui
                            orderShopStateTabPage.refreshListData();
                        }
                    }
                });
            }
        }).setCanCancel(true);
    }

    //关闭订单
    private void closeOrder(String orderId,int position){
        SelectDialog.show(mContext, "提示", "关闭订单表示该订单不显示在此界面并不是真正的删除,可到bmob后台打开\n确定要关闭该订单吗?", "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                OrderBean orderBean = new OrderBean();
                orderBean.closeOrder = true;
                orderBean.update(orderId, new UpdateListener(){
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            //列表移除该item
                            OrderShopStateListAdapter.this.mDatas.remove(position);
                            OrderShopStateListAdapter.this.notifyDataSetChanged();
                        }
                    }
                });
            }
        }).setCanCancel(true);
    }

    //接单
    private void receiptOrder(String orderId){
        SelectDialog.show(mContext, "提示", "接下此单并开始做餐?", "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                OrderBean orderBean = new OrderBean();
                orderBean.state = Constant.ORDER_STATE_SHOP_HAVE_RECEIPT;
                orderBean.update(orderId, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e==null){
                            orderShopStateTabPage.refreshListData();
                            ToasUtils.showToastMessage("接单成功");
                        }
                    }
                });
            }
        }).setCanCancel(true);
    }
}
