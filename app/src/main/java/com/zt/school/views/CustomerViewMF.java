package com.zt.school.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gongwen.marqueen.MarqueeFactory;
import com.zt.school.R;
import com.zt.school.bmob.NewsBean;

/**
 * 自定义MarqueeFactory来定制任意类型跑马灯ItemView
 */

public class CustomerViewMF extends MarqueeFactory<LinearLayout,NewsBean> {
    private LayoutInflater inflater;
    public CustomerViewMF(Context mContext) {
        super(mContext);
        inflater = LayoutInflater.from(mContext);
    }
    @Override
    protected LinearLayout generateMarqueeItemView(NewsBean data) {
        LinearLayout itemView = (LinearLayout) inflater.inflate(R.layout.pmd_item, null);
        TextView titleTv = itemView.findViewById(R.id.id_pmd_item_title);
        TextView desTv = itemView.findViewById(R.id.id_pmd_item_des);
        titleTv.setText(data.title);
        desTv.setText(data.desc);
        return itemView;
    }


}
