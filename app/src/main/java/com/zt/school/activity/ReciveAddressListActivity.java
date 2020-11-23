package com.zt.school.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zt.school.R;
import com.zt.school.adapter.AddressListAdapter;
import com.zt.school.base.BaseActivity;
import com.zt.school.bmob.AddressBean;
import com.zt.school.bmob.UserBean;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Author: 九七
 */

public class ReciveAddressListActivity extends BaseActivity {
    @BindView(R.id.listview)
    ListView listView;
    @BindView(R.id.id_base_smartrefreshlayout)
    SmartRefreshLayout smartRefreshLayout;
    private AddressListAdapter adapter;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_address_list;
    }

    @Override
    protected boolean setStatusBarFontColorIsBlack() {
        return true;
    }

    @Override
    protected void initView() {
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                getAddress();
                smartRefreshLayout.finishRefresh();
            }
        });
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.autoRefresh();
    }

    @OnClick(R.id.add_address_tv) void click(View view){
        startActivity(new Intent(this,AddAddressActivity.class));
    }

    private void getAddress(){
        BmobQuery<AddressBean> query = new BmobQuery<>();
        UserBean user = BmobUser.getCurrentUser(UserBean.class);
        query.addWhereEqualTo("userId", user.getObjectId());
        query.findObjects(new FindListener<AddressBean>() {
            @Override
            public void done(List<AddressBean> list, BmobException e) {
                if (e==null && list!=null && list.size()>0){
                    if (adapter==null){
                        listView.setAdapter(adapter = new AddressListAdapter(ReciveAddressListActivity.this,list,R.layout.layout_item_address));
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                AddressBean bean = adapter.getItem(i);
                                Intent intent = new Intent();
                                intent.putExtra("address", bean);
                                setResult(200,intent);
                                finish();
                            }
                        });
                    }else {
                        adapter.refreshAllData(list);
                    }
                }else {
                    if (adapter!=null)adapter.clearAllData();
                }
            }
        });
    }
}
