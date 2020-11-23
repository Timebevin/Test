package com.zt.school.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zt.school.R;
import com.zt.school.activity.FoodDetailActivity;
import com.zt.school.activity.ShopFoodActivity;
import com.zt.school.bean.StickBean;
import com.zt.school.bmob.FoodBean;
import com.zt.school.utils.CustomListView;

import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Author: 九七
 * 带头部标题的listview适配器
 */

public class StickRightListAdapter extends BaseAdapter implements StickyListHeadersAdapter{
    private List<StickBean> stickBeans;
    private ShopFoodActivity activity;
    private LayoutInflater inflater;
    public StickRightListAdapter(ShopFoodActivity context, List<StickBean> list){
        this.activity = context;
        this.stickBeans = list;
        this.inflater = LayoutInflater.from(context);
    }
    public List<StickBean> getDatas(){
        return stickBeans;
    }
    @Override
    public int getCount() {
        return stickBeans.size();
    }

    @Override
    public StickBean getItem(int i) {
        return stickBeans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup){
        ViewHolder vh;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.layout_listview,null);
            vh = new ViewHolder();
            vh.customListView = convertView.findViewById(R.id.listview);
            convertView.setTag(vh);
        }else {
            vh = (ViewHolder) convertView.getTag();
        }
        StickBean stickBean = stickBeans.get(i);
        RightListAdapter adapter;
        if(stickBean.foodBeanList.size()>0){
            vh.customListView.setVisibility(View.VISIBLE);//解决某一分类没有菜品数据时，任显示出其他菜品数据的bug
            vh.customListView.setAdapter(adapter = new RightListAdapter(stickBean.foodBeanList,activity));
            final RightListAdapter finalAdapter = adapter;
            vh.customListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                    FoodBean bean = finalAdapter.getItem(i);
                    Intent intent = new Intent(activity, FoodDetailActivity.class);
                    intent.putExtra("food", bean);
                    activity.startActivity(intent);
                }
            });
        }else {
            vh.customListView.setVisibility(View.GONE);//解决某一分类没有菜品数据时，任显示出其他菜品数据的bug
        }
        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder vh = null;
        if (vh == null){
            convertView = inflater.inflate(R.layout.item_stick_list_heade, null);
            TextView titleTv = convertView.findViewById(R.id.id_stick_header_title);
            vh = new HeaderViewHolder();
            vh.titleTv = titleTv;
            convertView.setTag(vh);
        }else {
            vh = (HeaderViewHolder) convertView.getTag();
        }
        vh.titleTv.setText(stickBeans.get(position).headerName);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return stickBeans.get(position).headerId;
    }
    class HeaderViewHolder{
        public TextView titleTv;
    }
    class ViewHolder{
        public CustomListView customListView;
    }
}
