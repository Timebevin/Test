package com.zt.school.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.kongzue.dialog.listener.OnMenuItemClickListener;
import com.kongzue.dialog.v2.BottomMenu;
import com.kongzue.dialog.v2.MessageDialog;
import com.zt.school.R;
import com.zt.school.activity.ShopFoodActivity;
import com.zt.school.bmob.ShopBean;
import com.zt.school.confige.Constant;
import com.zt.school.confige.MySharePreference;
import com.zt.school.utils.GaoDeMapUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 九七
 */

public class CusPopDialog extends Dialog {
    private AppCompatActivity activity;
    public CusPopDialog(@NonNull Context context) {
        super(context, R.style.LocatonDialogStyle);
        this.activity = (AppCompatActivity) context;
        setContentView(R.layout.cus_pop_dialog);
        setCancelable(true);
    }
    public void showDialog(final ShopBean bean) {
        Window window = getWindow();
        //设置弹窗动画
        window.setWindowAnimations(R.style.DialogScaleAnim);
        //设置Dialog背景色(透明)
        window.setBackgroundDrawableResource(R.color.transparent);
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置弹窗位置
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        TextView cateTv = findViewById(R.id.id_cate_tv);
        cateTv.setText("类别:" + bean.cateName);
        TextView nameTv = findViewById(R.id.id_name_tv);
        nameTv.setText(bean.name);
        TextView popularTv = findViewById(R.id.id_popular_tv);
        popularTv.setText("人气:"+ Float.valueOf(bean.hot));
        TextView scoreTv = findViewById(R.id.id_score_tv);
        scoreTv.setText("评分:"+ Float.valueOf(bean.score));

        CircleImageView logoIv = findViewById(R.id.id_logo_iv);
        Glide.with(activity).load(bean.logo).centerCrop().into(logoIv);

        findViewById(R.id.id_ignore_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                popShowDialog(bean);
            }
        });
        findViewById(R.id.id_update_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                Intent intent = new Intent(activity, ShopFoodActivity.class);
                intent.putExtra(Constant.SHOP, bean);
                activity.startActivity(intent);
            }
        });
        show();
    }
    private void popShowDialog(ShopBean shopBean){
        MessageDialog.show(activity, shopBean.name, shopBean.address + "", "导航到该商家", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                daohan(shopBean);
            }
        }).setCanCancel(true);
    }
    private void daohan(ShopBean shopBean){
        List<String> list = new ArrayList<>();
        list.add("高德地图");
        list.add("百度地图");
        BottomMenu.show(activity, list, new OnMenuItemClickListener() {
            @Override
            public void onClick(String text, int index) {
                switch (index){
                    case 0:
                        GaoDeMapUtils.openGaoDeMap(activity, MySharePreference.getCurrentLocationInfo().lat,MySharePreference.getCurrentLocationInfo().lng,MySharePreference.getCurrentLocationInfo().street,
                                shopBean.lat,shopBean.lng,shopBean.name);
                        break;
                    case 1:
                        GaoDeMapUtils.openBaiduMap(activity,MySharePreference.getCurrentLocationInfo().lat,MySharePreference.getCurrentLocationInfo().lng,
                                shopBean.lat,shopBean.lng,shopBean.name);
                        break;
                }
            }
        },true);
    }
}
