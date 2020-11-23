package com.zt.school.activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.kongzue.dialog.listener.OnMenuItemClickListener;
import com.kongzue.dialog.v2.BottomMenu;
import com.kongzue.dialog.v2.MessageDialog;
import com.kongzue.dialog.v2.WaitDialog;
import com.zt.school.R;
import com.zt.school.base.BaseActivity;
import com.zt.school.bean.SelectAddressBean;
import com.zt.school.bmob.ShopBean;
import com.zt.school.bmob.ShopCateBean;
import com.zt.school.utils.CallPhoneUtils;
import com.zt.school.utils.GlideUtils;
import com.zt.school.utils.NetworkUtil;
import com.zt.school.utils.ToasUtils;
import com.zt.school.views.CircleImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

/**
 * Author: 九七
 */
public class ShopRegisterActivity extends BaseActivity {
    @BindView(R.id.id_base_title_tv)
    TextView mTitleTv;
    @BindView(R.id.name)
    EditText nameEdit;
    @BindView(R.id.tel)
    EditText phoneEdit;
    @BindView(R.id.pass)
    EditText passEdit;
    @BindView(R.id.repass)
    EditText repasdEdit;
    @BindView(R.id.zg_name)
    EditText userNameEdit;
    @BindView(R.id.id_logo_iv)
    CircleImageView logoIv;
    @BindView(R.id.address)
    TextView addressTv;
    @BindView(R.id.id_shop_cate_tv)
    TextView shopCateTv;
    private OptionsPickerView options;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_shop_register;
    }

    @Override
    protected void initView() {
        mTitleTv.setText("商家注册");
    }

    @Override
    protected boolean setStatusBarFontColorIsBlack() {
        return true;
    }

    @OnClick({R.id.register, R.id.id_logo_iv, R.id.id_shop_cate_tv,R.id.address})
    void click(View view) {
        switch (view.getId()) {
            case R.id.register:
                register();
                break;
            case R.id.id_logo_iv:
                showAlert();
                break;
            case R.id.id_shop_cate_tv:
                showShopCatePick();
                break;
            case R.id.address:
                Intent intent = new Intent(this, ShopMapAddressSelectActivity.class);
                startActivityForResult(intent,101);
                break;
        }
    }

    private void register() {
        String name = nameEdit.getText().toString().trim();
        String username = userNameEdit.getText().toString().trim();
        String phone = phoneEdit.getText().toString().trim();
        String pass = passEdit.getText().toString().trim();
        String repass = repasdEdit.getText().toString().trim();
        if (logo == null) {
            ToasUtils.showToastMessage("请上传商家头像!");
            return;
        }
        if (TextUtils.isEmpty(name)) {
            ToasUtils.showToastMessage("请输入商家名称");
            return;
        }
        if (seleceCateId==null){
            ToasUtils.showToastMessage("请输入商家分类");
            return;
        }
        if (TextUtils.isEmpty(username)) {
            ToasUtils.showToastMessage("请输入掌柜姓名");
            return;
        }
        if (!CallPhoneUtils.isPhone(phone)) {
            ToasUtils.showToastMessage("请输入正确的手机号码");
            return;
        }
        if (addressBean==null) {
            ToasUtils.showToastMessage("请选择商家地址");
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            ToasUtils.showToastMessage("请输入密码");
            return;
        }
        if (TextUtils.isEmpty(repass)) {
            ToasUtils.showToastMessage("请输入重复密码");
            return;
        }
        if (!pass.equals(repass)) {
            ToasUtils.showToastMessage("两次密码输入不一致");
            return;
        }
        ShopBean shopBean = new ShopBean();
        shopBean.name = name;
        shopBean.tel = phone;
        shopBean.address = addressBean.address;
        shopBean.logo = logo;
        shopBean.score = String.valueOf(4);
        shopBean.distance = String.valueOf(500);
        shopBean.username = username;
        shopBean.pass = pass;
        shopBean.cateName = selectCateName;
        shopBean.cateId = seleceCateId;
        shopBean.hot = "1000";//默认给100人气值
        //注:默认经纬度为成都
        shopBean.lat = addressBean.lat;
        shopBean.lng = addressBean.lng;

        shopBean.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    MessageDialog.show(ShopRegisterActivity.this, "提示", "商家注册成功", "知道了,去登陆", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                }
            }
        });
    }

    private void showAlert() {
        List<String> list = new ArrayList<>();
        list.add("拍照");
        list.add("相册");
        BottomMenu bottomMenu = BottomMenu.show(this, list, new OnMenuItemClickListener() {
            @Override
            public void onClick(String text, int index) {
                switch (index) {
                    case 0:
                        paizhao();
                        break;
                    case 1:
                        xc();
                        break;
                }
            }
        }, true).setTitle("图片选择方式");
    }

    private String path, image_from_sd_paizhao_or_xianche__path;

    private void paizhao() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        path = Environment.getExternalStorageDirectory().getPath() + "/";
        //将当前的拍照时间作为图片的文件名称
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String filename = simpleDateFormat.format(new Date()) + ".jpg";
        image_from_sd_paizhao_or_xianche__path = path + filename;
        File file = new File(image_from_sd_paizhao_or_xianche__path);
        //******************************************************************
        Uri photoURI;
        //解决三星7.x或其他7.x系列的手机拍照失败或应用崩溃的bug.解决4.2.2(oppo)/4.x系统拍照执行不到设置显示图片的bug
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) { //7.0以下的系统用 Uri.fromFile(file)
            photoURI = Uri.fromFile(file);
        } else {                                            //7.0以上的系统用下面这种方案
            photoURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);//将图片文件转化为一个uri传入
        startActivityForResult(intent, 100);
    }

    private void xc() {
        PhotoPicker.builder()
                //设置选择个数
                .setPhotoCount(1)
                //选择界面第一个显示拍照按钮
                .setShowCamera(false)
                //选择时点击图片放大浏览
                .setPreviewEnabled(true)
                .start(this);
    }

    private SelectAddressBean addressBean;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!NetworkUtil.checkedNetWork(this)) {
            ToasUtils.showToastMessage("网络异常请连接后重试");
            return;
        }
        //three library 相册选择返回
        if (resultCode == this.RESULT_OK && (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {
            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            if (photos != null) {
                upload(photos.get(0));
            }
        }
        switch (requestCode) {
            case 100: //拍照
                if (resultCode == this.RESULT_OK) {
                    if (image_from_sd_paizhao_or_xianche__path != null) {
                        upload(image_from_sd_paizhao_or_xianche__path);
                    }
                }
                break;
            case 101://地址选择返回
                if (resultCode==200){
                    addressBean = (SelectAddressBean) data.getSerializableExtra("address");
                    addressTv.setText(addressBean.address);
                }
                break;
        }
    }

    private String logo;

    private void upload(String path) {
        WaitDialog.show(this, "头像上传中...");
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                WaitDialog.dismiss();
                if (e == null) {
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    logo = bmobFile.getFileUrl();
                    GlideUtils.load(logo, logoIv);
                } else {
                    ToasUtils.showToastMessage("上传文件失败：" + e.getMessage());
                }
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }
        });
    }
    private List<ShopCateBean> shopCateList;
    private List<String> cateNames;
    private void showShopCatePick(){
        BmobQuery<ShopCateBean> query = new BmobQuery<>();
        query.findObjects(new FindListener<ShopCateBean>(){
            @Override
            public void done(List<ShopCateBean> list, BmobException e) {
                if (e == null && list != null && list.size() > 0) {
                    shopCateList = new ArrayList();
                    cateNames = new ArrayList<>();
                    shopCateList = list;
                    for (ShopCateBean bean:list){
                        cateNames.add(bean.name);
                    }
                    show();
                }
            }
        });
    }
    private String selectCateName, seleceCateId;
    private void show(){
        options = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                shopCateTv.setText(cateNames.get(options1));
                selectCateName = cateNames.get(options1);
                seleceCateId = shopCateList.get(options1).getObjectId();
            }
        }).setTitleText("分类选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK)
                .setContentTextSize(15)
                .build();
        options.setPicker(cateNames);
        options.show();
    }
}
