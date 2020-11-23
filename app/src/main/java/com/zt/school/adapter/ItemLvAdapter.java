package com.zt.school.adapter;

import android.content.Context;

import com.zt.school.R;
import com.zt.school.base.ItemBean;
import com.zt.school.common.CommonAdapter;
import com.zt.school.common.CommonViewHolder;

import java.util.List;

/**
 * Author: 九七
 */

public class ItemLvAdapter extends CommonAdapter<ItemBean> {

    public ItemLvAdapter(Context context, List<ItemBean> datas, int layoutId){
        super(context, datas, layoutId);
    }

    @Override
    public void convert(CommonViewHolder holder, ItemBean itemBean) {
        holder.setImageResource(R.id.id_iv, itemBean.imgRes)
                .setTvText(R.id.id_title, itemBean.title);
    }
}
