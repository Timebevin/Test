package com.zt.school.activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.kongzue.dialog.listener.InputDialogOkButtonClickListener;
import com.kongzue.dialog.v2.InputDialog;
import com.kongzue.dialog.v2.MessageDialog;
import com.zt.school.R;
import com.zt.school.base.BaseActivity;
import com.zt.school.bmob.FoodCateBean;
import com.zt.school.common.CommonAdapter;
import com.zt.school.common.CommonViewHolder;
import com.zt.school.confige.MySharePreference;
import com.zt.school.utils.ToasUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class AddFoodCateActivity extends BaseActivity {
    @BindView(R.id.listview)
    ListView listView;
    private ListAdapter adapter;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_add_food_cate;
    }

    @Override
    protected boolean setStatusBarFontColorIsBlack() {
        return true;
    }

    @Override
    protected void initData() {
        BmobQuery<FoodCateBean> query = new BmobQuery<>();
        query.addWhereEqualTo("shopId", MySharePreference.getShopBean().getObjectId());
        query.findObjects(new FindListener<FoodCateBean>(){
            @Override
            public void done(List<FoodCateBean> list, BmobException e){
                if(list!=null && list.size()>0){
                    listView.setAdapter(adapter = new ListAdapter(AddFoodCateActivity.this,list,R.layout.item_list_cate));
                    listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                            FoodCateBean bean = adapter.getItem(i);
                            longClick(bean,i);
                            return false;
                        }
                    });
                }
            }
        });
    }

    private void longClick(final FoodCateBean bean,final int position){
        MessageDialog.show(this, "提示", "确定要删除【" + bean.name + "】分类吗?", "确定", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FoodCateBean foodCateBean = new FoodCateBean();
                foodCateBean.setObjectId(bean.getObjectId());
                foodCateBean.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            adapter.delete(position);
                        }
                    }
                });
            }
        }).setCanCancel(true);
    }

    @OnClick({R.id.add_cate_iv}) void click(View view){
        InputDialog.show(this, "提示", "分类名称", new InputDialogOkButtonClickListener(){
            @Override
            public void onClick(Dialog dialog, String inputText){
                if(TextUtils.isEmpty(inputText)){
                    ToasUtils.showToastMessage("请输入菜品分类名称");
                    return;
                }
                final FoodCateBean foodCateBean = new FoodCateBean(MySharePreference.getShopBean().getObjectId(),inputText);
                foodCateBean.save(new SaveListener<String>(){
                    @Override
                    public void done(String s, BmobException e) {
                        if (e==null){
                            ToasUtils.showToastMessage("添加成功");
                            if (adapter!=null){
                                adapter.addData(foodCateBean);
                            }else {
                                List<FoodCateBean> list = new ArrayList<>();
                                list.add(foodCateBean);
                                listView.setAdapter(adapter = new ListAdapter(AddFoodCateActivity.this,list,R.layout.item_list_cate));
                                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                    @Override
                                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        FoodCateBean bean = adapter.getItem(i);
                                        longClick(bean,i);
                                        return false;
                                    }
                                });
                            }
                        }
                    }
                });
                dialog.dismiss();
            }
        });
    }

    class ListAdapter extends CommonAdapter<FoodCateBean>{

        public ListAdapter(Context context, List<FoodCateBean> datas, int layoutId){
            super(context, datas, layoutId);
        }

        @Override
        public void convert(CommonViewHolder holder, FoodCateBean foodCateBean){
            holder.setTvText(R.id.id_cate_name_tv, foodCateBean.name);
        }
        public void delete(int position){
            this.mDatas.remove(position);
            notifyDataSetChanged();
            ToasUtils.showToastMessage("删除成功");
        }
        public void addData(FoodCateBean foodCateBean){
            this.mDatas.add(foodCateBean);
            notifyDataSetChanged();
        }
    }
}
