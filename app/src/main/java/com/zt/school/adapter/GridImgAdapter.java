package com.zt.school.adapter;

import android.content.Context;

import com.zt.school.R;
import com.zt.school.common.CommonAdapter;
import com.zt.school.common.CommonViewHolder;
import com.zt.school.utils.GlideUtils;

import java.util.List;

public class GridImgAdapter extends CommonAdapter<String> {

    public GridImgAdapter(Context context, List<String> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(CommonViewHolder holder, String s) {
        GlideUtils.load(s,holder.getView(R.id.id_img));
    }
}
