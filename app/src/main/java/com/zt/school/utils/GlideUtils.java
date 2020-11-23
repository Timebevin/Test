package com.zt.school.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zt.school.R;

/**
 * Created by 九七
 * 统一图片加载
 */
public class GlideUtils {
    public static void load(String url, ImageView imageView){
        Glide.with(UIUtils.getContext())
                .load(url)
                .error(R.mipmap.logo)
                .into(imageView);
    }
}
