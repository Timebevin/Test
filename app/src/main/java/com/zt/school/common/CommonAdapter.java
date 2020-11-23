package com.zt.school.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by 九七
 * 通用ListView和GridView适配器
 */

public abstract class CommonAdapter<T> extends BaseAdapter {
    protected List<T> mDatas;
    protected Context mContext;
    protected LayoutInflater mInflate;
    private int layoutId;
    public CommonAdapter(Context context, List<T> datas, int layoutId){
        this.mContext = context;
        this.mInflate = LayoutInflater.from(context);
        this.mDatas = datas;
        this.layoutId = layoutId;
    }
    @Override
    public int getCount() {
        return mDatas.size();
    }
    public void addData(List<T> datas){
        this.mDatas.addAll(datas);
        notifyDataSetChanged();
    }
    public void refreshData(List<T> datas){
        this.mDatas.clear();
        addData(datas);
    }
    public void clearAllData(){
        mDatas.clear();
        notifyDataSetChanged();
    }
    @Override
    public T getItem(int i) {
        return mDatas.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        CommonViewHolder holder = CommonViewHolder.get(mContext, convertView, parent,layoutId, position);
        T bean = getItem(position);
        convert(holder,bean);
        assistant(holder,bean,position);
        return holder.getConvertView();
    }
    public abstract void convert(CommonViewHolder holder, T t);
    public void assistant(CommonViewHolder holder, T t,int position){};

    //Item背景or文字选中颜色(me add)
    protected int checkItemPosition = -1;
    public void setCheckItem(int position){
        this.checkItemPosition = position;
        notifyDataSetChanged();
    }
}
