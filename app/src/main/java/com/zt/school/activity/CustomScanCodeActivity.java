package com.zt.school.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zt.school.R;
import com.zt.school.utils.CustomCodeUtils;
import com.zt.school.utils.ImageUtil;
import com.zt.school.utils.ToasUtils;
import com.zt.school.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created 九七
 * 自定义二维码扫描界面，进入此界面即可进行扫码
 * 此Activity可作为扫码工具类，他日有此需求直接copy使用即可
 */

public class CustomScanCodeActivity extends FragmentActivity {
    @BindView(R.id.id_custom_top_view)
    RelativeLayout topRelativeLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_scann_code_layout);
        ButterKnife.bind(this);
        statueBar();
        customScanCodeView();
    }
    private void customScanCodeView() {
        /**
         * 执行扫面Fragment的初始化操作
         */
        CaptureFragment captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面, R.layout.my_camera必须添加
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        /**
         * 替换我们的扫描控件
         */
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();
    }
    /**
     * 打开此界面直接扫码进行二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            CustomScanCodeActivity.this.setResult(RESULT_OK, resultIntent);
            CustomScanCodeActivity.this.finish();
        }
        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            CustomScanCodeActivity.this.setResult(RESULT_OK, resultIntent);
            CustomScanCodeActivity.this.finish();
        }
    };
    /**
     *相册扫描回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CustomCodeUtils.REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri!=null) {
                    try {
                        CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(this, uri), new CodeUtils.AnalyzeCallback() {
                            @Override
                            public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                                //手机震动
                                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                vibrator.vibrate(200);
                                //关闭当前activity,将二维码解析结果传递回前一activity/fragment
                                Intent intent = new Intent();
                                intent.putExtra(CodeUtils.RESULT_STRING, result);
                                CustomScanCodeActivity.this.setResult(CustomCodeUtils.TYPE_XC_RETURN_CODE,intent);
                                CustomScanCodeActivity.this.finish();
                            }
                            @Override
                            public void onAnalyzeFailed() {
                                ToasUtils.showToastMessage("请选择二维码/条形码");
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    /**
     * 打开相册选择额我们图片进行扫码,读SD卡权限在上一个activity已经申请
     */
    private void openXC(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, CustomCodeUtils.REQUEST_IMAGE);
    }
    public static boolean isOpen = false;
    @OnClick({R.id.custom_light_ll, R.id.custom_xc_ll,R.id.back_colse_view}) void bottomItemClick(View view){
        switch (view.getId()){
            case R.id.custom_light_ll:
                if (!isOpen) {
                    CodeUtils.isLightEnable(true);
                    isOpen = true;
                } else {
                    CodeUtils.isLightEnable(false);
                    isOpen = false;
                }
                break;
            case R.id.custom_xc_ll:
                openXC();
                break;
            case R.id.back_colse_view:
                finish();
                break;
        }
    }
    private void statueBar() {
        //4.4 全透明状态栏（有的机子是过渡形式的透明）
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        //5.0 全透明实现
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);// calculateStatusColor(Color.WHITE, (int) alphaValue)
        }
        initStateBar();
    }
    private void initStateBar(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT){
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) topRelativeLayout.getLayoutParams();
            lp.topMargin = UIUtils.getStateBarHeight();
            topRelativeLayout.setLayoutParams(lp);
        }
    }
}
