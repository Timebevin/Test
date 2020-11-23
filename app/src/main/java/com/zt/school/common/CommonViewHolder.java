package com.zt.school.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zt.school.utils.GlideUtils;


/**
 * Created by 九七
 * 通用ViewHolder
 */

public class CommonViewHolder {
    private SparseArray<View> mViews;
    private int mPosition;
    private Context mContext;
    private View mConvertView;
    public CommonViewHolder(Context context, ViewGroup parent, int layoutId, int position){
        this.mPosition = position;
        this.mContext = context;
        mViews = new SparseArray<>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }
    public static CommonViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position){
        if(convertView==null){
            return new CommonViewHolder(context, parent, layoutId, position);
        }else {
            CommonViewHolder holder = (CommonViewHolder) convertView.getTag();
            holder.mPosition = position;
            return holder;
        }
    }

    public View getConvertView() {
        return mConvertView;
    }

    public int getmPosition() {
        return mPosition;
    }

    /**
     * 通过viewId获取控件
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T getView(int viewId){
        View view = mViews.get(viewId);
        if(view==null){
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId,view);
        }
        return (T) view;
    }

    /**
     *为TextView赋值
     */
    public CommonViewHolder setTvText(int viewId, String text){
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }
    public CommonViewHolder setImageResource(int viewId, int resId){
        ImageView iv = getView(viewId);
        iv.setImageResource(resId);
        return this;
    }
    public CommonViewHolder setImageBitmap(int viewId, Bitmap bitmap){
        ImageView iv = getView(viewId);
        iv.setImageBitmap(bitmap);
        return this;
    }
    public CommonViewHolder setImageUrl(int viewId, String url){
        ImageView iv = getView(viewId);
        GlideUtils.load(url,iv);
        return this;
    }
}
