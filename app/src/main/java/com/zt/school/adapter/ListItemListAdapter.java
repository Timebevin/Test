package com.zt.school.adapter;

import android.content.Context;

import com.makeramen.roundedimageview.RoundedImageView;
import com.zt.school.R;
import com.zt.school.bmob.FoodBean;
import com.zt.school.common.CommonAdapter;
import com.zt.school.common.CommonViewHolder;
import com.zt.school.utils.GlideUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * Created 九七
 */

public class ListItemListAdapter extends CommonAdapter<FoodBean>{
    private String createTime;
    public ListItemListAdapter(Context context, List<FoodBean> datas, int layoutId,String createTime) {
        super(context, datas, layoutId);
        this.createTime = createTime;
    }

    @Override
    public void convert(CommonViewHolder helper, FoodBean foodBean) {
        RoundedImageView iv = helper.getView(R.id.logo);
        try {
            JSONArray jsonArray = new JSONArray(foodBean.imgs);
            GlideUtils.load(jsonArray.get(0).toString(),iv);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        helper.setTvText(R.id.name_tv,foodBean.name)
                .setTvText(R.id.price, "￥ " + foodBean.price)
                .setTvText(R.id.desc,"下单时间 : " + createTime)
                .setTvText(R.id.count_tv, "x " + foodBean.total);
    }
}
