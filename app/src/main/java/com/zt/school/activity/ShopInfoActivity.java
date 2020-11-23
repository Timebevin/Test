package com.zt.school.activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kongzue.dialog.listener.OnMenuItemClickListener;
import com.kongzue.dialog.v2.BottomMenu;
import com.kongzue.dialog.v2.MessageDialog;
import com.kongzue.dialog.v2.WaitDialog;
import com.zt.school.R;
import com.zt.school.base.BaseActivity;
import com.zt.school.bmob.ShopBean;
import com.zt.school.confige.MySharePreference;
import com.zt.school.fragment.FragmentMine;
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
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

/**
 * Author: 九七
 */
public class ShopInfoActivity extends BaseActivity {
    @BindView(R.id.id_base_title_tv)
    TextView mTitleTv;
    @BindView(R.id.edit_photo_image)
    CircleImageView circleImageView;
    @BindView(R.id.edit_name_Edit)
    EditText nameEdit;
    private ShopBean shopBean;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_shopinfo;
    }

    @Override
    protected boolean setStatusBarFontColorIsBlack() {
        return true;
    }

    @Override
    protected void initView() {
        mTitleTv.setText("商家信息");
        shopBean = MySharePreference.getShopBean();
        GlideUtils.load(shopBean.logo,circleImageView);
        nameEdit.setText(shopBean.name);
    }
    @OnClick({R.id.edit_photo_relative,R.id.id_save_tv}) void click(View view){
        switch (view.getId()){
            case R.id.edit_photo_relative:
                showAlert();
                break;
            case R.id.id_save_tv:
                update();
                break;
        }
    }
    private void showAlert(){
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
    private void paizhao(){
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
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){ //7.0以下的系统用 Uri.fromFile(file)
            photoURI = Uri.fromFile(file);
        }else {                                            //7.0以上的系统用下面这种方案
            photoURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider",file);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);//将图片文件转化为一个uri传入
        startActivityForResult(intent, 100);
    }
    private void xc(){
        PhotoPicker.builder()
                //设置选择个数
                .setPhotoCount(50)
                //选择界面第一个显示拍照按钮
                .setShowCamera(false)
                //选择时点击图片放大浏览
                .setPreviewEnabled(true)
                .start(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        activityResult(requestCode,resultCode,data);
    }
    public void activityResult(int requestCode, int resultCode, Intent data){
        if(!NetworkUtil.checkedNetWork(this)){
            ToasUtils.showToastMessage("网络异常请连接后重试");
            return;
        }
        //three library 相册选择返回
        if (resultCode == this.RESULT_OK && (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)){
            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            if (photos != null) {
                upload(photos.get(0));
            }
        }
        switch (requestCode){
            case 100: //拍照
                if (resultCode==this.RESULT_OK){
                    if(image_from_sd_paizhao_or_xianche__path!=null){
                        upload(image_from_sd_paizhao_or_xianche__path);
                    }
                }else{
                    ToasUtils.showToastMessage("放弃拍照");
                }
                break;
        }
    }

    private String logoUrl;
    private void upload(String path){
        WaitDialog.show(this, "头像上传中...");
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                WaitDialog.dismiss();
                if(e==null){
                    logoUrl = bmobFile.getFileUrl();
                    GlideUtils.load(logoUrl,circleImageView);
                }
            }
            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }
        });
    }
    private void update(){
        WaitDialog.show(this, "头像上传中...");
        final String name = nameEdit.getText().toString().trim();
        final ShopBean bean = new ShopBean();
        bean.setObjectId(shopBean.getObjectId());
        if (logoUrl!=null) bean.logo = logoUrl;
        if (!TextUtils.isEmpty(name))bean.name = name;
        bean.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                WaitDialog.dismiss();
                if (e == null) {
                    //更新本地sp商家数据
                    if (logoUrl!=null)MySharePreference.editor.putString("logo", logoUrl).commit();
                    if (!TextUtils.isEmpty(name))MySharePreference.editor.putString("name", name).commit();

                    MessageDialog.show(ShopInfoActivity.this, "提示", "信息修改成功", "知道了，关闭", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FragmentMine.initState();
                            finish();
                        }
                    });
                } else {
                    ToasUtils.showToastMessage("更新信息失败：" + e.getMessage());
                }
            }
        });
    }
}
