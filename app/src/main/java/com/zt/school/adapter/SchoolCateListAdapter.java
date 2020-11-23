package com.zt.school.adapter;

import android.content.Context;

import com.zt.school.R;
import com.zt.school.bmob.ShopCateBean;
import com.zt.school.common.CommonAdapter;
import com.zt.school.common.CommonViewHolder;

import java.util.List;

public class SchoolCateListAdapter extends CommonAdapter<ShopCateBean> {

    public SchoolCateListAdapter(Context context, List<ShopCateBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(CommonViewHolder holder, ShopCateBean categoryBean) {
        holder.setTvText(R.id.id_cate_tv, categoryBean.name);
    }
}
