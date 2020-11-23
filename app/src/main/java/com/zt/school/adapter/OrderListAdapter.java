package com.zt.school.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.zt.school.R;
import com.zt.school.bmob.FoodBean;
import com.zt.school.common.CommonAdapter;
import com.zt.school.common.CommonViewHolder;
import com.zt.school.confige.Constant;
import com.zt.school.utils.GlideUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * Author: 九七
 */

public class OrderListAdapter extends CommonAdapter<FoodBean> {

    public OrderListAdapter(Context context, List<FoodBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(CommonViewHolder holder, FoodBean foodBean) {
        ImageView iv = holder.getView(R.id.id_cart_item_food_iv);
        try {
            JSONArray jsonArray = new JSONArray(foodBean.imgs);
            GlideUtils.load(jsonArray.get(0).toString(), iv);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.setTvText(R.id.id_cart_item_food_name, foodBean.name)
                .setTvText(R.id.id_cart_item_food_count, "x" + foodBean.total)
                .setTvText(R.id.id_cart_item_food_price, Constant.PRICE_MARK + foodBean.price);
    }
}
