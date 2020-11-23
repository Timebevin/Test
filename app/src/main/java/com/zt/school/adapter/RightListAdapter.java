package com.zt.school.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kongzue.dialog.v2.MessageDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.zt.school.R;
import com.zt.school.activity.ShopFoodActivity;
import com.zt.school.activity.UserLoginActivity;
import com.zt.school.bmob.FoodBean;
import com.zt.school.confige.Constant;
import com.zt.school.confige.MySharePreference;
import com.zt.school.utils.GlideUtils;
import com.zt.school.utils.NetworkUtil;
import com.zt.school.utils.ToasUtils;
import com.zt.school.utils.UIUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 九七
 * 点餐界面右侧菜品列表适配器
 */

public class RightListAdapter extends BaseAdapter {
    private List<FoodBean> list;
    private ShopFoodActivity activity;
    private LayoutInflater inflater;
    public RightListAdapter(List<FoodBean> list, ShopFoodActivity context){
        this.list = list;
        this.activity = context;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list.size();
    }
    @Override
    public FoodBean getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh;
        if(view == null){
            view = inflater.inflate(R.layout.item_right_lv,null);
            vh = new ViewHolder(view);
            view.setTag(vh);
        }else {
            vh = (ViewHolder) view.getTag();
        }
        FoodBean goodsData = list.get(i);
        vh.bindData(goodsData);
        return view;
    }
    class ViewHolder implements View.OnClickListener{
        RoundedImageView goodLogo;
        TextView foodNameTv;
        TextView lable1Tv;
        TextView priceTv;
        TextView minusTv,countTv,addTv;
        FoodBean itemData;
        public ViewHolder(View view){
            goodLogo = view.findViewById(R.id.id_r_i_food_logo);
            foodNameTv = view.findViewById(R.id.id_r_i_food_name_tv);
            priceTv = view.findViewById(R.id.id_r_i_food_price_tv);
            minusTv = view.findViewById(R.id.id_r_i_food_jin_tv);
            countTv = view.findViewById(R.id.id_r_i_food_count_tv);
            addTv = view.findViewById(R.id.id_r_i_food_add_tv);
            lable1Tv = view.findViewById(R.id.type1);
            minusTv.setOnClickListener(this);
            addTv.setOnClickListener(this);
        }
        public void bindData(FoodBean goodsData){
            try {
                JSONArray jsonArray = new JSONArray(goodsData.imgs);
                String img = jsonArray.getString(0);
                GlideUtils.load(img,goodLogo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            foodNameTv.setText(goodsData.name);
            priceTv.setText("￥"+goodsData.price);
            int status = goodsData.status;
            //标签
            initLables(goodsData);
            if(status==0){   //下架
                minusTv.setVisibility(View.INVISIBLE);
                addTv.setVisibility(View.INVISIBLE);
                countTv.setVisibility(View.VISIBLE);
                countTv.setText("该商品未上架");
                countTv.setTextColor(Color.GRAY);
                countTv.setTextSize(UIUtils.dp2px(3));
            }else if(status==1){  //正常
                int total = goodsData.total;
                if(total==0){
                    minusTv.setVisibility(View.INVISIBLE);
                    countTv.setVisibility(View.INVISIBLE);
                    addTv.setVisibility(View.VISIBLE);
                }else {
                    minusTv.setVisibility(View.VISIBLE);
                    countTv.setVisibility(View.VISIBLE);
                    addTv.setVisibility(View.VISIBLE);
                    countTv.setText(String.valueOf(total));
                    countTv.setTextColor(Color.BLACK);
                    countTv.setTextSize(UIUtils.dp2px(5));
                }
            }
        }
        private void initLables(FoodBean goodsData){
            itemData = goodsData;
            List<String> lables = new ArrayList<>();
            lables.add(goodsData.cateName);
            if(lables!=null && lables.size()>0){
                int size = lables.size();
                switch (size){
                    case 1:
                        String lable = lables.get(0);
                        if(lable==null){
                            lable1Tv.setVisibility(View.INVISIBLE);
                        }else {
                            lable1Tv.setVisibility(View.VISIBLE);
                            lable1Tv.setText(lables.get(0));
                        }
                        break;
                    case 2:
                        lable1Tv.setVisibility(View.VISIBLE);
                        lable1Tv.setText(lables.get(0));
                        break;
                    default:
                        lable1Tv.setVisibility(View.VISIBLE);
                        lable1Tv.setText(lables.get(0));
                        break;
                }
            }
        }
        @Override
        public void onClick(final View view) {
             switch (view.getId()){
                 case R.id.id_r_i_food_jin_tv:
                     if(!NetworkUtil.checkedNetWork(activity)){
                         ToasUtils.showToastMessage("网络异常请重试!");
                         return;
                     }
                     minus();
                     break;
                 case R.id.id_r_i_food_add_tv:
                     //判断是否登陆了会员端
                     int loginState = MySharePreference.getCurrentLoginState();
                     if(loginState== Constant.STATE_NORMAL_LOGIN_SUC){
                         add(view);
                     }else {
                         MessageDialog.show(activity, "提示", "登录会员端后方可点餐", "去登录", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialogInterface, int i) {
                                 Intent intent = new Intent(activity, UserLoginActivity.class);
                                 activity.startActivity(intent);
                                 activity.overridePendingTransition(R.anim.translate_down_to_between, R.anim.translate_none);
                             }
                         }).setCanCancel(true);
                     }
                     break;
             }
        }
        private void minus(){
            int total = itemData.total;
            if(total==1){
                minusTv.startAnimation(getHiddenAnimation());
                countTv.startAnimation(getHiddenAnimation());
                minusTv.setVisibility(View.INVISIBLE);
                countTv.setVisibility(View.INVISIBLE);
            }
            itemData.total--;
            countTv.setText(String.valueOf( itemData.total));

            activity.remove(itemData);
        }
        private void add(View view){
            int total = itemData.total;
            if(total==0){
                minusTv.startAnimation(getShowAnimation());
                countTv.startAnimation(getShowAnimation());
                minusTv.setVisibility(View.VISIBLE);
                countTv.setVisibility(View.VISIBLE);
            }
            itemData.total++;
            countTv.setText(String.valueOf(itemData.total));
            countTv.setTextColor(Color.BLACK);
            countTv.setTextSize(UIUtils.dp2px(5));

            //小球跳转动画
            int[] loc = new int[2];
            view.getLocationInWindow(loc);
            activity.playAnimation(loc);

            activity.add(itemData);
        }
    }
    private Animation getShowAnimation(){
        AnimationSet set = new AnimationSet(true);
        RotateAnimation rotate = new RotateAnimation(0,720, RotateAnimation.RELATIVE_TO_SELF,0.5f, RotateAnimation.RELATIVE_TO_SELF,0.5f);
        set.addAnimation(rotate);
        TranslateAnimation translate = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF,2f
                , TranslateAnimation.RELATIVE_TO_SELF,0
                , TranslateAnimation.RELATIVE_TO_SELF,0
                , TranslateAnimation.RELATIVE_TO_SELF,0);
        set.addAnimation(translate);
        AlphaAnimation alpha = new AlphaAnimation(0,1);
        set.addAnimation(alpha);
        set.setDuration(500);
        return set;
    }

    private Animation getHiddenAnimation(){
        AnimationSet set = new AnimationSet(true);
        RotateAnimation rotate = new RotateAnimation(0,720, RotateAnimation.RELATIVE_TO_SELF,0.5f, RotateAnimation.RELATIVE_TO_SELF,0.5f);
        set.addAnimation(rotate);
        TranslateAnimation translate = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF,0
                , TranslateAnimation.RELATIVE_TO_SELF,2f
                , TranslateAnimation.RELATIVE_TO_SELF,0
                , TranslateAnimation.RELATIVE_TO_SELF,0);
        set.addAnimation(translate);
        AlphaAnimation alpha = new AlphaAnimation(1,0);
        set.addAnimation(alpha);
        set.setDuration(500);
        return set;
    }
}
