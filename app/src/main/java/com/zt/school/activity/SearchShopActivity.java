package com.zt.school.activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zt.school.R;
import com.zt.school.adapter.ShopListAdapter;
import com.zt.school.base.BaseActivity;
import com.zt.school.bmob.ShopBean;
import com.zt.school.confige.Constant;
import com.zt.school.utils.ToasUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SearchShopActivity extends BaseActivity {

    @BindView(R.id.id_search_tv)
    TextView searchTv;
    @BindView(R.id.listview)
    ListView listView;
    @BindView(R.id.id_base_smartrefreshlayout)
    SmartRefreshLayout smartRefreshLayout;
    private ShopListAdapter adapter;

    @Override
    protected int setContentViewId(){
        return R.layout.activity_search_shop;
    }

    @Override
    protected boolean setStatusBarFontColorIsBlack(){
        return true;
    }

    @Override
    protected void initView(){
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener(){
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                searchShopByName();
                smartRefreshLayout.finishRefresh();
            }
        });
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.autoRefresh();
    }

    @OnClick(R.id.id_search_linear) void click(View view){
        Intent intent = new Intent(this, HistorySearchActivity.class);
        startActivityForResult(intent,100);
        overridePendingTransition(R.anim.anim_open_search,R.anim.translate_none);
    }

    private String keyword;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && resultCode==200){
            keyword = data.getStringExtra(Constant.KEYWORD);
            if(TextUtils.isEmpty(keyword)){
                searchTv.setText(getResources().getString(R.string.search_shop_msg));
                searchTv.setTextColor(getResources().getColor(R.color.tv_gray_color));
            }else {
                searchTv.setText(keyword);
                searchTv.setTextColor(getResources().getColor(R.color.tv_b_gray_color));
            }
            smartRefreshLayout.autoRefresh();
        }
    }
    private void searchShopByName(){
        BmobQuery<ShopBean> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("isPass", true);

        BmobQuery<ShopBean> query2 = new BmobQuery<>();
        if (keyword!=null) query2.addWhereEqualTo("name", keyword);
        //if (keyword!=null) query2.addWhereContains("name", keyword);模糊查询【注:模糊查询只对付费用户开放，付费后可直接使用。】

        List<BmobQuery<ShopBean>> queryList = new ArrayList<>();
        queryList.add(query1);
        queryList.add(query2);

        //复合查询-and
        BmobQuery<ShopBean> mainQuery = new BmobQuery<>();
        mainQuery.and(queryList);
        mainQuery.findObjects(new FindListener<ShopBean>(){
            @Override
            public void done(List<ShopBean> list, BmobException e){
                if (e==null && list!=null && list.size()>0){
                    if (adapter==null){
                        listView.setAdapter(adapter = new ShopListAdapter(SearchShopActivity.this,list,R.layout.item_list_shop));
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                ShopBean bean = adapter.getItem(i);
                                Intent intent = new Intent(SearchShopActivity.this, ShopFoodActivity.class);
                                intent.putExtra("shop", bean);
                                startActivity(intent);
                            }
                        });
                    }else {
                        adapter.refreshAllData(list);
                    }
                }else {
                    if (adapter!=null)adapter.clearAllData();
                    ToasUtils.showToastMessage("没有此商家哦");
                }
            }
        });
    }
}
