package com.zt.school.activity;

import android.widget.ListView;
import android.widget.TextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zt.school.R;
import com.zt.school.adapter.OrderCommentsAdapter;
import com.zt.school.base.BaseActivity;
import com.zt.school.bmob.CommentBean;
import com.zt.school.confige.Constant;
import com.zt.school.utils.ToasUtils;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
/**
 * 用户评价
 */
public class UserCommentListActivity extends BaseActivity {
    @BindView(R.id.id_base_title_tv)
    TextView mTitleTv;
    @BindView(R.id.id_base_smartrefreshlayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.listview)
    ListView listView;
    private OrderCommentsAdapter adapter;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_shop_comment_list;
    }

    @Override
    protected boolean setStatusBarFontColorIsBlack() {
        return true;
    }

    @Override
    protected void initView() {
        mTitleTv.setText("用户评价");
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener(){
            @Override
            public void onRefresh(RefreshLayout refreshLayout){
                int type = getIntent().getIntExtra(Constant.TYPE, 1);
                if (type==1){
                    //ShopFoodActivity进入
                    loadData1();
                }else if (type==2){
                    //ShopFoodActivity -> OrderShopStateListAdapter 进入
                    loadData2();
                }
                smartRefreshLayout.finishRefresh();
            }
        });
        smartRefreshLayout.autoRefresh();
    }

    //复合查询-与查询
    private void loadData1(){
        String shopId = getIntent().getStringExtra("shopId");
        BmobQuery<CommentBean> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("shopId", shopId);
        BmobQuery<CommentBean> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("isPass", true);

        List<BmobQuery<CommentBean>> list = new ArrayList<>();
        list.add(query1);
        list.add(query2);

        BmobQuery<CommentBean> mainQuery = new BmobQuery<CommentBean>();
        mainQuery.and(list);
        mainQuery.order("-createdAt");//时间降序查询
        mainQuery.findObjects(new FindListener<CommentBean>(){
            @Override
            public void done(List<CommentBean> list, BmobException e){
                if (e==null && list!=null && list.size()>0){
                    if (adapter==null){
                        listView.setAdapter(adapter = new OrderCommentsAdapter(UserCommentListActivity.this, list, R.layout.shop_user_comment_item));
                    }else {
                        adapter.refreshData(list);
                    }
                }else {
                    if (adapter!=null){
                        adapter.clearAllData();
                    }else {
                        ToasUtils.showToastMessage("暂无用户评价");
                    }
                }
            }
        });
    }
    //复合查询-与查询
    private void loadData2(){
        String shopId = getIntent().getStringExtra("shopId");
        String orderId = getIntent().getStringExtra("orderId");
        BmobQuery<CommentBean> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("shopId", shopId);
        BmobQuery<CommentBean> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("isPass", true);
        BmobQuery<CommentBean> query3 = new BmobQuery<>();
        query3.addWhereEqualTo("orderId", orderId);

        List<BmobQuery<CommentBean>> list = new ArrayList<>();
        list.add(query1);
        list.add(query2);
        list.add(query3);

        BmobQuery<CommentBean> mainQuery = new BmobQuery<CommentBean>();
        mainQuery.and(list);
        mainQuery.order("-createdAt");//时间降序查询
        mainQuery.findObjects(new FindListener<CommentBean>(){
            @Override
            public void done(List<CommentBean> list, BmobException e){
                if (e==null && list!=null && list.size()>0){
                    if (adapter==null){
                        listView.setAdapter(adapter = new OrderCommentsAdapter(UserCommentListActivity.this, list, R.layout.shop_user_comment_item));
                    }else {
                        adapter.refreshData(list);
                    }
                }else {
                    if (adapter!=null){
                        adapter.clearAllData();
                    }else {
                        ToasUtils.showToastMessage("暂无用户评价");
                    }
                }
            }
        });
    }
}
