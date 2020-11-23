package com.zt.school.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.bigkoo.pickerview.OptionsPickerView;
import com.kongzue.dialog.v2.MessageDialog;
import com.kongzue.dialog.v2.SelectDialog;
import com.zt.school.R;
import com.zt.school.adapter.ImageUpLoadGridAdapter;
import com.zt.school.base.BaseActivity;
import com.zt.school.bmob.FoodBean;
import com.zt.school.bmob.FoodCateBean;
import com.zt.school.confige.MySharePreference;
import com.zt.school.utils.NetworkUtil;
import com.zt.school.utils.ToasUtils;
import com.zt.school.views.MyGridView;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class PublishFoodActivity extends BaseActivity{
    @BindView(R.id.id_base_title_tv)
    TextView mTitleTv;
    @BindView(R.id.id_food_cate_tv)
    TextView foodCateTv;
    @BindView(R.id.id_gridview)
    MyGridView myGridView;
    @BindView(R.id.id_food_name_edit)
    EditText foodNameEdit;
    @BindView(R.id.id_food_price_edit)
    EditText foodPriceEdit;

    private OptionsPickerView options;
    private ImageUpLoadGridAdapter adapter;
    private String seleceFoodCateObjectId;
    private String selectFoodCateName;

    @Override
    protected int setContentViewId(){
        return R.layout.activity_publish_food;
    }

    @Override
    protected void initView(){
        mTitleTv.setText("发布菜品");
    }

    @Override
    protected boolean setStatusBarFontColorIsBlack() {
        return true;
    }

    @Override
    protected void initData(){
        myGridView.setAdapter(adapter = new ImageUpLoadGridAdapter(this));
    }

    @OnClick({R.id.id_food_cate_tv,R.id.add_cate_iv,R.id.id_commit_tv}) void click(View view){
        switch (view.getId()){
            case R.id.id_food_cate_tv:
                getFoodCate();
                break;
            case R.id.add_cate_iv:
                startActivity(new Intent(PublishFoodActivity.this,AddFoodCateActivity.class));
                break;
            case R.id.id_commit_tv:
                commit();
                break;
        }
    }
    private void commit(){
        if (seleceFoodCateObjectId==null){
            ToasUtils.showToastMessage("请选择菜品分类!");
            return;
        }
        String foodName = foodNameEdit.getText().toString().trim();
        if(TextUtils.isEmpty(foodName)){
            ToasUtils.showToastMessage("请输入菜品名称!");
            return;
        }
        String price = foodPriceEdit.getText().toString().trim();
        if(TextUtils.isEmpty(price)){
            ToasUtils.showToastMessage("请输入菜品售价!");
            return;
        }
        if(adapter.images.size()<2){
            ToasUtils.showToastMessage("请上传菜品图片!");
            return;
        }
        if (!NetworkUtil.checkedNetWork(this)){
            ToasUtils.showToastMessage("网络链接异常，请链接成功后再试!");
            return;
        }
        FoodBean foodBean = new FoodBean(MySharePreference.getShopBean().getObjectId(),seleceFoodCateObjectId,selectFoodCateName, foodName, price, adapter.getUploadImgUrlArrays());
        foodBean.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){
                    MessageDialog.show(PublishFoodActivity.this, "提升", "菜品发布成功", "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                }
            }
        });
    }
    private void getFoodCate(){
        BmobQuery<FoodCateBean> query = new BmobQuery<>();
        query.addWhereEqualTo("shopId", MySharePreference.getShopBean().getObjectId());
        query.findObjects(new FindListener<FoodCateBean>(){
            @Override
            public void done(List<FoodCateBean> list, BmobException e){
                if(list!=null && list.size()>0){
                    initPick(list);
                    if (options!=null)options.show();
                }else {
                    showSelect();
                }
            }
        });
    }
    private void showSelect(){
        SelectDialog.show(this, "提示", "暂未添加菜品分类，去添加?", "添加", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(PublishFoodActivity.this,AddFoodCateActivity.class));
            }
        });
    }
    private void initPick(final List<FoodCateBean> foodCateBeanList){
        options = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener(){
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                foodCateTv.setText(foodCateBeanList.get(options1).name);
                seleceFoodCateObjectId = foodCateBeanList.get(options1).getObjectId();
                selectFoodCateName = foodCateBeanList.get(options1).name;
            }
        }).setTitleText("分类选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK)
                .setContentTextSize(15)
                .build();
        List<String> names = new ArrayList<>();
        for (FoodCateBean bean:foodCateBeanList) {
            names.add(bean.name);
        }
        options.setPicker(names);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //拍照or相册
        if(adapter!=null){
            adapter.activityResult(requestCode,resultCode,data);
        }
    }
}
