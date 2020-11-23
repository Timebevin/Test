package com.zt.school.adapter;

import android.content.Context;
import com.zt.school.R;
import com.zt.school.bmob.ShopBean;
import com.zt.school.common.CommonAdapter;
import com.zt.school.common.CommonViewHolder;
import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ShopListAdapter  extends CommonAdapter<ShopBean>{

    public ShopListAdapter(Context context, List<ShopBean> datas, int layoutId){
        super(context, datas, layoutId);
    }
    @Override
    public void convert(CommonViewHolder holder, ShopBean shopBean){
        holder.setImageUrl(R.id.logo, shopBean.logo)
                .setTvText(R.id.id_title, shopBean.name)
                .setTvText(R.id.cate,"【"+ shopBean.cateName+"】")
                .setTvText(R.id.id_commen_xin_count,shopBean.score+"分")
                .setTvText(R.id.distance, shopBean.hot);
        MaterialRatingBar ratingbar = holder.getView(R.id.id_ratingbar);
        ratingbar.setRating(Float.valueOf(shopBean.score));
    }
    public void refreshAllData(List<ShopBean> list){
        this.mDatas.clear();
        this.mDatas.addAll(list);
        notifyDataSetChanged();
    }
}
