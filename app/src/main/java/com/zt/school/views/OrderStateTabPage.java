package com.zt.school.views;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zt.school.R;
import com.zt.school.activity.OrderDetailActivity;
import com.zt.school.adapter.OrderStateListAdapter;
import com.zt.school.bmob.AppKeyBean;
import com.zt.school.bmob.OrderBean;
import com.zt.school.bmob.UserBean;
import com.zt.school.confige.Constant;
import com.zt.school.utils.UIUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class OrderStateTabPage extends FrameLayout {
    @BindView(R.id.id_base_smartrefreshlayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.listview)
    ListView listView;
    Context context;
    private OrderStateListAdapter adapter;

    public OrderStateTabPage(@NonNull Context context){
        super(context);
        this.context = context;
        View view = UIUtils.inflater(R.layout.layout_refresh_listview);
        this.addView(view);
        ButterKnife.bind(this);
        init();
    }

    private void init(){
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                initData(state);
                smartRefreshLayout.finishRefresh();
            }
        });
    }

    public int state;
    public void initData(int state){
        this.state = state;
        if (BmobUser.isLogin()){  //全部
            BmobQuery<OrderBean> query = new BmobQuery<>();
            query.order("-createdAt");
            if (state!=-1)query.addWhereEqualTo("state", state);//-1代表全部
            query.addWhereEqualTo("userId", BmobUser.getCurrentUser(UserBean.class).getObjectId());
            query.findObjects(new FindListener<OrderBean>(){
                @Override
                public void done(List<OrderBean> list, BmobException e) {
                    if (e==null && list!=null && list.size()>0){
                        if (adapter==null){
                            listView.setAdapter(adapter = new OrderStateListAdapter(context,list,R.layout.layout_item_order_sate,OrderStateTabPage.this));
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    OrderBean orderBaen = adapter.getItem(i);
                                    enteryOrderDetail(orderBaen.getObjectId());
                                }
                            });
                        }else {
                          adapter.refreshData(list);
                        }
                    }else {
                        if (adapter!=null)adapter.clear();
                    }
                }
            });
        }else {
            if (adapter!=null)adapter.clear();
        }
    }
    public void refreshListData(){
        initData(state);
    }
    private void enteryOrderDetail(String orderId){
        //查询AppKeyBean表,获取支付所需的appkey
        BmobQuery<AppKeyBean> query = new BmobQuery<>();
        query.findObjects(new FindListener<AppKeyBean>(){
            @Override
            public void done(List<AppKeyBean> list, BmobException e) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                if (e==null && list!=null && list.size()>0){
                    intent.putExtra("appkey", list.get(0).appkey);//使用第一条记录的appkey
                }else {
                    intent.putExtra("appkey","appkey");
                }
                intent.putExtra(Constant.OBJECTID, orderId);
                context.startActivity(intent);
            }
        });
    }
}
