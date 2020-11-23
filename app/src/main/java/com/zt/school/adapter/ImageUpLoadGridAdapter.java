package com.zt.school.adapter;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.kongzue.dialog.listener.OnMenuItemClickListener;
import com.kongzue.dialog.v2.BottomMenu;
import com.kongzue.dialog.v2.WaitDialog;
import com.zt.school.R;
import com.zt.school.activity.ImageDetailActivity;
import com.zt.school.confige.Constant;
import com.zt.school.utils.GlideUtils;
import com.zt.school.utils.NetworkUtil;
import com.zt.school.utils.ToasUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UploadBatchListener;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

/**
 * Created by 九七
 * 通用网格图片上传适配器
 */

public class ImageUpLoadGridAdapter extends BaseAdapter {
    public List<String> images;
    private AppCompatActivity activity;
    private LayoutInflater inflater;
    private BottomMenu bottomMenu;
    private Gson gson;
    public ImageUpLoadGridAdapter(AppCompatActivity activity){
        this.images = new ArrayList<>();
        images.add("占位图");
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.gson = new Gson();
    }
    private void showAlert(){
        List<String> list = new ArrayList<>();
        list.add("拍照");
        list.add("相册");
        bottomMenu = BottomMenu.show(activity, list, new OnMenuItemClickListener() {
            @Override
            public void onClick(String text, int index) {
                switch (index){
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
    @Override
    public int getCount() {
        return images.size();
    }
    //添加多张图片
    public void addGridData(List<String> list){
        this.images.addAll(this.images.size()-1, list);//将集合，添加在占位图前
        this.notifyDataSetChanged();
    }
    //添加单张图片
    public void addGridItemImg(String url){
        this.images.add(this.images.size()-1,url);
        this.notifyDataSetChanged();
    }

    @Override
    public String getItem(int i) {
        return images.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    //获取图片url json数组字符串
    public String getUploadImgUrlArrays(){
        List<String> urls = new ArrayList<>();
        for (int i=0;i<images.size()-1;i++){
            urls.add(images.get(i));
        }
        //list集合转化为json数组
        JsonArray jsonArray = gson.toJsonTree(urls, new TypeToken<List<String>>() {}.getType()).getAsJsonArray();
        return jsonArray.toString();
    }

    @Override
    public View getView(int position, View contentView, ViewGroup viewGroup) {
        ViewHolder vh;
        if(contentView==null){
            contentView = inflater.inflate(R.layout.item_img_upload, null);
            vh = new ViewHolder();
            vh.uploadImg = contentView.findViewById(R.id.id_item_f_s_upload_iv);
            vh.closeImg = contentView.findViewById(R.id.id_item_f_s_colse_iv);
            contentView.setTag(vh);
        }else {
            vh = (ViewHolder) contentView.getTag();
        }
        vh.bindData(position);
        return contentView;
    }
    class ViewHolder{
        ImageView uploadImg,closeImg;
        public void bindData(final int position){
            if(position==images.size()-1){
                Glide.with(activity).load(images.get(position)).error(R.mipmap.icon_add).into(uploadImg);
                closeImg.setVisibility(View.GONE);
                uploadImg.setScaleType(ImageView.ScaleType.FIT_XY);
            }else {
                GlideUtils.load(images.get(position),uploadImg);
                closeImg.setVisibility(View.VISIBLE);
                uploadImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            //移除此张图片
            closeImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    images.remove(position);
                    notifyDataSetChanged();
                }
            });
            //拍照vs相册
            uploadImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(position==images.size()-1){
                        showAlert();
                    }else {
                        ArrayList<String> imgUrls = new ArrayList<>();
                        for (int i=0;i<images.size()-1;i++){
                            imgUrls.add(images.get(i));
                        }
                        Intent intent = new Intent(activity, ImageDetailActivity.class);
                        intent.putExtra(Constant.POSITION, position);
                        intent.putStringArrayListExtra(Constant.IMAGES,imgUrls);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.translate_down_to_between,R.anim.translate_none);
                    }
                }
            });
        }
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
            photoURI = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider",file);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);//将图片文件转化为一个uri传入
        activity.startActivityForResult(intent, 100);
    }
    private void xc(){
        PhotoPicker.builder()
                //设置选择个数
                .setPhotoCount(50)
                //选择界面第一个显示拍照按钮
                .setShowCamera(false)
                //选择时点击图片放大浏览
                .setPreviewEnabled(true)
                .start(activity);
    }
    public void activityResult(int requestCode, int resultCode, Intent data){
        if(!NetworkUtil.checkedNetWork(activity)){
            ToasUtils.showToastMessage("网络异常请连接后重试");
            return;
        }
        //three library 相册选择返回
        if (resultCode == activity.RESULT_OK && (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)){
            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            if (photos != null) {
                upload(photos);
            }
        }
        switch (requestCode){
            case 100: //拍照
                if (resultCode==activity.RESULT_OK){
                    ArrayList<String> list = new ArrayList<>();
                    if(image_from_sd_paizhao_or_xianche__path!=null){
                        list.add(image_from_sd_paizhao_or_xianche__path);
                        upload(list);
                    }
                }else{
                    //ToasUtils.showToastMessage("放弃拍照");
                }
                break;
        }
    }

    /**
     * *************递归方式上传图片*************
     */
    //相册选择vs拍照后直接上传图片
    private void upload(List<String> imgs){
        if(imgs!=null && imgs.size()>0){
            WaitDialog.show(activity, "图片上传中...");
            uploadFileAll(imgs);
        }
    }

    //批量上传文件
    public void uploadFileAll(List<String> imgList){
        //详细示例可查看BmobExample工程中BmobFileActivity类
        final String[] filePaths = new String[imgList.size()];
        for (int i=0;i<imgList.size();i++){
            filePaths[i] = imgList.get(i);
        }
        BmobFile.uploadBatch(filePaths, new UploadBatchListener(){
            @Override
            public void onSuccess(List<BmobFile> files, List<String> urls) {
                //1、files-上传完成后的BmobFile集合，是为了方便大家对其上传后的数据进行操作，例如你可以将该文件保存到表中
                //2、urls-上传文件的完整url地址
                if(urls.size()==filePaths.length){//如果数量相等，则代表文件全部上传完成
                    //do something
                    WaitDialog.dismiss();
                    addGridData(urls);
                }
            }
            @Override
            public void onError(int statuscode, String errormsg) {
                WaitDialog.dismiss();
                ToasUtils.showToastMessage("错误码"+statuscode +",错误描述："+errormsg);
            }
            @Override
            public void onProgress(int curIndex, int curPercent, int total,int totalPercent){
                //1、curIndex--表示当前第几个文件正在上传
                //2、curPercent--表示当前上传文件的进度值（百分比）
                //3、total--表示总的上传文件数
                //4、totalPercent--表示总的上传进度（百分比）
            }
        });
    }
}
