package com.zt.school.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.kongzue.dialog.v2.SelectDialog;
import com.zt.school.R;
import com.zt.school.bmob.AddressBean;
import com.zt.school.common.CommonAdapter;
import com.zt.school.common.CommonViewHolder;
import com.zt.school.utils.ToasUtils;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Author: 九七
 */

public class AddressListAdapter extends CommonAdapter<AddressBean> {
    public AddressListAdapter(Context context, List<AddressBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(CommonViewHolder holder, final AddressBean addressBean) {
        final String address = addressBean.province + " " + addressBean.city + " " + addressBean.area + " " + addressBean.detailAddress;
        holder.setTvText(R.id.name_tv, addressBean.name)
                .setTvText(R.id.mobile_tv, addressBean.mobile)
                .setTvText(R.id.address_tv, address);
        holder.getView(R.id.id_delete_tv)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SelectDialog.show(mContext, "提示", "确定要删除该收货地址吗?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i==-1){ //确定
                                    AddressBean p2 = new AddressBean();
                                    p2.setObjectId(addressBean.getObjectId());
                                    p2.delete(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if(e==null){
                                                deleteAddress(addressBean);
                                            }else {
                                                ToasUtils.showToastMessage("删除失败 " + e.getMessage());
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
    }

    public void refreshAllData(List<AddressBean> list) {
        mDatas.clear();
        mDatas.addAll(list);
        notifyDataSetChanged();
    }
    public void clearAllData(){
        mDatas.clear();
        notifyDataSetChanged();
    }
    private void deleteAddress(AddressBean addressBean){
        ToasUtils.showToastMessage("删除成功");
        mDatas.remove(addressBean);
        notifyDataSetChanged();
    }
}
