package com.zt.school.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zt.school.R;
import com.zt.school.bmob.FoodCateBean;

import java.util.List;

/**
 * Created by Administrator on 2018/3/18.
 * 点餐界面左侧菜品列表适配器
 */

public class LeftListAdapter extends BaseAdapter {
    public int selectPosition;
    private List<FoodCateBean> list;
    private LayoutInflater inflater;
    private Context context;
    public LeftListAdapter(List<FoodCateBean> list, Context context) {
        this.list = list;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }
    public List<FoodCateBean> getData(){
        return list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public FoodCateBean getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh;
        if(view==null){
            view = inflater.inflate(R.layout.item_left_lv_type, null);
            vh = new ViewHolder();
            vh.hotIv = view.findViewById(R.id.id_left_ivIcon);
            vh.typeTv = view.findViewById(R.id.id_left_tvCount);
            vh.countTv = view.findViewById(R.id.id_count_tv);
            view.setTag(vh);
        }else {
            vh = (ViewHolder) view.getTag();
        }
        FoodCateBean goodsCate = list.get(i);
        vh.typeTv.setText(goodsCate.name);
        if(i==selectPosition){
            vh.typeTv.setTextColor(context.getResources().getColor(R.color.red_select_color));
            //设置加粗
            vh.typeTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }else{
            vh.typeTv.setTextColor(context.getResources().getColor(R.color.normal_select_color));
            vh.typeTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
        if (goodsCate.total>0){
            vh.countTv.setText(String.valueOf(goodsCate.total));
            vh.countTv.setVisibility(View.VISIBLE);
        }else {
            vh.countTv.setVisibility(View.INVISIBLE);
        }
        return view;
    }
    class ViewHolder{
        ImageView hotIv;
        TextView typeTv,countTv;
    }
    public void leftItemSelectStateChange(int position){
        selectPosition = position;
        notifyDataSetChanged();
    }
}
