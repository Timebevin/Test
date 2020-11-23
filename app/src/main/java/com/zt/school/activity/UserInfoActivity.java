package com.zt.school.activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
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
import com.zt.school.bmob.UserBean;
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
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

/**
 * Author: 九七
 */
public class UserInfoActivity extends BaseActivity {
    @BindView(R.id.id_base_title_tv)
    TextView mTitleTv;
    @BindView(R.id.edit_photo_image)
    CircleImageView circleImageView;
    @BindView(R.id.edit_motto_Edit)
    EditText mottoEdit;
    @BindView(R.id.edit_nickname_Edit)
    EditText nickNameEdit;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_userinfo;
    }

    @Override
    protected boolean setStatusBarFontColorIsBlack() {
        return true;
    }

    @Override
    protected void initView() {
        mTitleTv.setText("我的信息");
        UserBean user = BmobUser.getCurrentUser(UserBean.class);
        GlideUtils.load(user.logo,circleImageView);
        mottoEdit.setText(user.motto);
        if (user.nickname==null){
            nickNameEdit.setText(user.getUsername());
        }else {
            nickNameEdit.setText(user.nickname);
            nickNameEdit.setSelection(user.nickname.length());
        }
    }
    @OnClick({R.id.edit_photo_relative,R.id.id_save_tv}) void click(View view){
        switch (view.getId()){
            case R.id.edit_photo_relative:
                showAlert();
                break;
            case R.id.id_save_tv:
                updateUser();
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
    /**
     * 更新用户操作并同步更新本地的用户信息
     */
    private void updateUser(){
        String nickname = nickNameEdit.getText().toString().trim();
        String motto = mottoEdit.getText().toString().trim();
        if (TextUtils.isEmpty(nickname)){
            ToasUtils.showToastMessage("请输入您的昵称");
            return;
        }
        if (TextUtils.isEmpty(motto)){
            ToasUtils.showToastMessage("请输入您的座右铭");
            return;
        }
        final UserBean user = BmobUser.getCurrentUser(UserBean.class);
        if (logoUrl!=null) user.logo = logoUrl;
        user.motto = motto;
        user.nickname = nickname;
        user.update(user.getObjectId(),new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    MessageDialog.show(UserInfoActivity.this, "提示", "用户信息修改成功", "知道了，关闭", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FragmentMine.initState();
                            finish();
                        }
                    });
                } else {
                   ToasUtils.showToastMessage("更新用户信息失败：" + e.getMessage());
                }
            }
        });
    }
}
