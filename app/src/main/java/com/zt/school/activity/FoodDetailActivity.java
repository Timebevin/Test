package com.zt.school.activity;

import android.content.Intent;
import android.widget.TextView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.zt.school.R;
import com.zt.school.base.BaseActivity;
import com.zt.school.bmob.FoodBean;
import com.zt.school.confige.Constant;
import com.zt.school.utils.GlideImageLoader;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import butterknife.BindView;

public class FoodDetailActivity extends BaseActivity {
    @BindView(R.id.id_base_title_tv)
    TextView mTitleTv;
    @BindView(R.id.banner)
    Banner mBanner;
    @BindView(R.id.tv)
    TextView foodInfoTv;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_food_detail;
    }


    @Override
    protected boolean setStatusBarFontColorIsBlack() {
        return true;
    }

    @Override
    protected void initData(){
        FoodBean food = (FoodBean) getIntent().getSerializableExtra("food");
        mTitleTv.setText(food.name);
        try {
            JSONArray jsonArray = new JSONArray(food.imgs);
            final ArrayList<String> imgs = new ArrayList<>();
            for (int i=0;i<jsonArray.length();i++){
                String img = jsonArray.getString(i);
                imgs.add(img);
            }
            //update
            mBanner.setImageLoader(new GlideImageLoader());
            mBanner.setImages(imgs);
            mBanner.setDelayTime(3000);
            mBanner.setBannerAnimation(Transformer.Default);
            mBanner.setIndicatorGravity(BannerConfig.CENTER);
            mBanner.start();
            mBanner.setOnBannerListener(new OnBannerListener(){
                @Override
                public void OnBannerClick(int position){
                    Intent intent = new Intent(FoodDetailActivity.this, ImageDetailActivity.class);
                    intent.putStringArrayListExtra(Constant.IMAGES, imgs);
                    intent.putExtra(Constant.POSITION, position);
                    startActivity(intent);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        foodInfoTv.setText("菜品名称："+food.name+"\n"+"菜品价格："+food.price+"元/份"+"\n"+"菜品分类："+food.cateName);

    }
}
