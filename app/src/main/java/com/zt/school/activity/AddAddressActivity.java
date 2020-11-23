package com.zt.school.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.kongzue.dialog.v2.MessageDialog;
import com.zt.school.R;
import com.zt.school.base.BaseActivity;
import com.zt.school.bmob.AddressBean;
import com.zt.school.bmob.UserBean;
import com.zt.school.utils.CallPhoneUtils;
import com.zt.school.utils.ProviceCityObtainUtils;
import com.zt.school.utils.ToasUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Author: 九七
 */
public class AddAddressActivity extends BaseActivity {
    @BindView(R.id.id_base_title_tv)
    TextView titleTv;
    @BindView(R.id.id_name_ed)
    EditText nameEdit;
    @BindView(R.id.id_mobile_ed)
    EditText mobileEdit;
    @BindView(R.id.id_address_tv)
    TextView addressTv;
    @BindView(R.id.id_detail_address_tv)
    EditText detailAddressEdit;
    @BindView(R.id.id_cimmit)
    TextView saveTv;
    private OptionsPickerView pvOptions;
    private boolean isEdit;
    @Override
    protected int setContentViewId() { return R.layout.activity_add_recrving_address;
    }
    @Override
    protected boolean setStatusBarFontColorIsBlack() {
        return true;
    }
    @Override
    protected void initView() {
        titleTv.setText("添加收货地址");
        showAreaChooseView();
    }
    @OnClick({R.id.id_address_tv, R.id.id_cimmit})
    void click(View view) {
        closeSoftware();
        switch (view.getId()) {
            case R.id.id_address_tv:
                if (pvOptions != null) {
                    pvOptions.show();
                }
                break;
            case R.id.id_cimmit:
                commit();
                break;
        }
    }
    private void commit() {
        final String name = nameEdit.getText().toString().trim();
        final String mobile = mobileEdit.getText().toString().trim();
        final String detailAddress = detailAddressEdit.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToasUtils.showToastMessage("请输入收货人姓名");
            return;
        }
        if (!CallPhoneUtils.isPhone(mobile)) {
            ToasUtils.showToastMessage("请输入正确的手机号码");
            return;
        }
        if (!isChooseCity) {
            ToasUtils.showToastMessage("请选择地区");
            return;
        }
        if (TextUtils.isEmpty(detailAddress)) {
            ToasUtils.showToastMessage("请输入详细地址");
            return;
        }
        AddressBean addressBean = new AddressBean();
        UserBean user = BmobUser.getCurrentUser(UserBean.class);
        addressBean.userId = user.getObjectId();
        addressBean.name = name;
        addressBean.mobile = mobile;
        addressBean.province = provinceName;
        addressBean.city = cityName;
        addressBean.area = areaName;
        addressBean.detailAddress = detailAddress;
        addressBean.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){
                    MessageDialog.show(AddAddressActivity.this, "提示", "收货地址添加成功", "关闭", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AddAddressActivity.this.finish();
                        }
                    }).setCanCancel(false);
                }
            }
        });
    }

    private String provinceName, cityName, areaName;
    private boolean isChooseCity;

    private void showAreaChooseView() {
        pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                provinceName = ProviceCityObtainUtils.provinces_names.get(options1);
                cityName = ProviceCityObtainUtils.provin_citys_names.get(options1).get(options2);
                areaName = ProviceCityObtainUtils.provin_citys_areas_names.get(options1).get(options2).get(options3);
                isChooseCity = true;
                addressTv.setText(provinceName + "  " + cityName + "  " + areaName);
            }
        })
                .setTitleText("地区选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(13)
                .setTitleSize(13)
                .build();
        if (ProviceCityObtainUtils.provinces_names == null || ProviceCityObtainUtils.provin_citys_names == null || ProviceCityObtainUtils.provin_citys_areas_names == null) {
            ProviceCityObtainUtils.loadProvinceCityAreaData();
        }
        pvOptions.setPicker(ProviceCityObtainUtils.provinces_names, ProviceCityObtainUtils.provin_citys_names, ProviceCityObtainUtils.provin_citys_areas_names);//三级选择器
    }
}
