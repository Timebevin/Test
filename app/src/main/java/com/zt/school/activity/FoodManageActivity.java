package com.zt.school.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.kongzue.dialog.v2.MessageDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zt.school.R;
import com.zt.school.base.BaseActivity;
import com.zt.school.bmob.FoodBean;
import com.zt.school.common.CommonAdapter;
import com.zt.school.common.CommonViewHolder;
import com.zt.school.confige.Constant;
import com.zt.school.confige.MySharePreference;
import com.zt.school.utils.GlideUtils;
import com.zt.school.utils.ToasUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 菜品管理
 */
public class FoodManageActivity extends BaseActivity {
    @BindView(R.id.listview)
    ListView listView;

    @BindView(R.id.id_base_smartrefreshlayout)
    SmartRefreshLayout smartRefreshLayout;
    private FoodListAdapter adapter;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_food_manage;
    }

    @Override
    protected boolean setStatusBarFontColorIsBlack() {
        return true;
    }

    @OnClick(R.id.add_food_iv) void click(View view){
        Intent intent = new Intent(this, PublishFoodActivity.class);
        startActivity(intent);
    }

    @Override
    protected void initView() {
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                getFoodList();
                smartRefreshLayout.finishRefresh();
            }
        });
        smartRefreshLayout.autoRefresh();
        smartRefreshLayout.setEnableLoadMore(false);
    }
    private void getFoodList(){
        BmobQuery<FoodBean> query = new BmobQuery<>();
        query.addWhereEqualTo("shopId", MySharePreference.getShopBean().getObjectId());
        query.findObjects(new FindListener<FoodBean>(){
            @Override
            public void done(List<FoodBean> list, BmobException e){
                if(list!=null && list.size()>0){
                    if (adapter==null){
                        listView.setAdapter(adapter = new FoodListAdapter(FoodManageActivity.this,list,R.layout.item_list_food));
                        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                                FoodBean food = adapter.getItem(position);
                                MessageDialog.show(FoodManageActivity.this, "提示", "确定要删除【"+food.name+"】该菜品吗?", "确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface,  int i){
                                        FoodBean bean = adapter.getItem(position);
                                        FoodBean foodBean = new FoodBean();
                                        foodBean.setObjectId(bean.getObjectId());
                                        foodBean.delete(new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if (e==null){
                                                    adapter.delete(position);
                                                }
                                            }
                                        });
                                    }
                                }).setCanCancel(true);
                                return false;
                            }
                        });
                    }else {
                        adapter.refreshData(list);
                    }
                }
            }
        });
    }
    class FoodListAdapter extends CommonAdapter<FoodBean>{

        public FoodListAdapter(Context context, List<FoodBean> datas, int layoutId) {
            super(context, datas, layoutId);
        }

        @Override
        public void convert(CommonViewHolder holder, FoodBean foodBean) {
            ImageView logoIv = holder.getView(R.id.logo);
            try {
                JSONArray jsonArray = new JSONArray(foodBean.imgs);
                String img = jsonArray.getString(0);
                GlideUtils.load(img,logoIv);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            holder.setTvText(R.id.name, foodBean.name)
                    .setTvText(R.id.cate, "【"+foodBean.cateName+"】")
                    .setTvText(R.id.price, Constant.PRICE_MARK + " " + foodBean.price + " 元/份");
        }

        public void refreshData(List<FoodBean> list) {
            this.mDatas.clear();
            this.mDatas.addAll(list);
            notifyDataSetChanged();
        }
        public void delete(int position){
            this.mDatas.remove(position);
            notifyDataSetChanged();
            ToasUtils.showToastMessage("删除成功!");
        }
    }
}
