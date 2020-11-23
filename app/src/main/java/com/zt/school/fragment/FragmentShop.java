package com.zt.school.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zt.school.R;
import com.zt.school.activity.MainActivity;
import com.zt.school.activity.ShopFoodActivity;
import com.zt.school.adapter.SchoolCateListAdapter;
import com.zt.school.adapter.ShopListAdapter;
import com.zt.school.base.BaseFragment;
import com.zt.school.bmob.ShopBean;
import com.zt.school.bmob.ShopCateBean;
import com.zt.school.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by 九七
 */

public class FragmentShop extends BaseFragment {

    private View rootView;
    @BindView(R.id.listview)
    ListView listView;
    @BindView(R.id.id_all_cate_tv)
    TextView cateTv;
    @BindView(R.id.rootview)
    LinearLayout rootLinearLayoutView;
    @BindView(R.id.id_ll)
    LinearLayout linearLayout;
    @BindView(R.id.id_base_smartrefreshlayout)
    SmartRefreshLayout smartRefreshLayout;

    private String currentCateId;
    private String hot = "hot";
    private String score = "score";
    private String order = hot;
    private ShopListAdapter adapter;

    MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.fragment_shop, container, false);
        ButterKnife.bind(this, rootView);
        init();
        return rootView;
    }
    private void init(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            rootLinearLayoutView.setPadding(0,UIUtils.getStateBarHeight(),0,0);
        }
        getCateData();
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshData();
                smartRefreshLayout.finishRefresh();
            }
        });
        smartRefreshLayout.autoRefresh();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    private void getCateData(){
        BmobQuery<ShopCateBean> query = new BmobQuery<>();
        query.findObjects(new FindListener<ShopCateBean>(){
            @Override
            public void done(List<ShopCateBean> list, BmobException e){
                if (e==null && list!=null && list.size()>0){
                    list.add(0,new ShopCateBean("全部"));
                    initPopCateView(list);
                }
            }
        });
    }

    @OnClick({R.id.id_all_cate_tv,R.id.id_hot_tv,R.id.id_comment_tv}) void click(View view){
        listView.setSelection(0);//列表滑动到最顶部
        switch (view.getId()){
            case R.id.id_all_cate_tv:
                if (popupWindow!=null){
                    popupWindow.showAsDropDown(linearLayout);
                }else {
                    getCateData();
                }
                break;
            case R.id.id_hot_tv:
                if (hot.equals("hot")){
                    hot = "-hot";
                }else {
                    hot = "hot";
                }
                order = hot;
                smartRefreshLayout.autoRefresh();
                break;
            case R.id.id_comment_tv:
                if (score.equals("score")){
                    score = "-score";
                }else {
                    score = "score";
                }
                order = score;
                smartRefreshLayout.autoRefresh();
                break;
        }
    }

    private PopupWindow popupWindow;
    private void initPopCateView(final List<ShopCateBean> datas){
        //计算屏幕宽高
        int width;
        int height;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(dm);
            width = dm.widthPixels;
            height = dm.heightPixels;
        } else {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            width = metrics.widthPixels;
            height = metrics.heightPixels;
        }
        View contentView = UIUtils.inflater(R.layout.layout_category);
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,height/4, true);
        popupWindow.setTouchable(true);
        // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置好参数之后再show
        //popupWindow.showAsDropDown(view);

        ListView listView = contentView.findViewById(R.id.listview);
        listView.setAdapter(new SchoolCateListAdapter(getActivity(),datas,R.layout.item_cate));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                popupWindow.dismiss();
                cateTv.setText(datas.get(position).name);
                cateTv.setTextColor(getResources().getColor(R.color.text_center_tab_selected));
                currentCateId = datas.get(position).getObjectId();

                smartRefreshLayout.autoRefresh();
            }
        });
    }

    /**
     * 多条件查询vs倒序，升序查询
     * // 根据score字段升序显示数据
     * //query.order("score");
     * // 根据score字段降序显示数据
     * //query.order("-score");
     */
    private void refreshData(){
        BmobQuery<ShopBean> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("isPass", true);

        BmobQuery<ShopBean> query2 = new BmobQuery<>();
        if (currentCateId!=null)query2.addWhereEqualTo("cateId", currentCateId);

        List<BmobQuery<ShopBean>> queryList = new ArrayList<>();
        queryList.add(query1);
        queryList.add(query2);

        //复合查询-and
        BmobQuery<ShopBean> mainQuery = new BmobQuery<>();
        mainQuery.and(queryList);
        mainQuery.order(order);

        mainQuery.findObjects(new FindListener<ShopBean>(){
            @Override
            public void done(List<ShopBean> list, BmobException e){
                if(e==null && list!=null && list.size()>0){
                    if (adapter==null){
                        listView.setAdapter(adapter = new ShopListAdapter(getActivity(),list,R.layout.item_list_shop));
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                ShopBean bean = adapter.getItem(i);
                                Intent intent = new Intent(getActivity(), ShopFoodActivity.class);
                                intent.putExtra("shop", bean);
                                startActivity(intent);
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

