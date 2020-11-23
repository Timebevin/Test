package com.zt.school.activity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import com.zt.school.R;
import com.zt.school.db.DatabaseUtil;
import com.zt.school.utils.UIUtils;
import permissions.dispatcher.BuildConfig;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
/**
 * Created 九七
 */
@RuntimePermissions
public class SplashActivity extends Activity {
    private boolean isShowAd = true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashActivityPermissionsDispatcher.initWithPermissionCheck(this);
        DatabaseUtil.packDataBase(this);
    }

    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE})
    protected  void init(){
        UIUtils.getHandler().postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent intent;
                if(isShowAd){
                     intent = new Intent(SplashActivity.this, AdActivity.class);
                     startActivity(intent);
                }else {
                     intent = new Intent(SplashActivity.this, MainActivity.class);
                     startActivity(intent);
                     overridePendingTransition(R.anim.anim_scale,R.anim.translate_none);
                }
                finish();
            }
        },1000);
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE})
    protected  void permissionDenied(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("权限申请")
                .setMessage("定位权限,存储权限,电话权限，相机权限，手机状态权限为必选权限全部授予才可以正常使用APP")
                .setCancelable(false)
                .setPositiveButton("取消", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("授予", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SplashActivityPermissionsDispatcher.initWithPermissionCheck(SplashActivity.this);
                    }
                })
                .show();
    }
    @OnNeverAskAgain({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE})
    protected void nerverAskAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("权限申请")
                .setMessage("定位权限,存储权限,电话权限，相机权限，手机状态权限为必选权限，全部授予才可以正常使用APP，请到设置中开启\n\n"+"设置路径:系统设置->天润易购->权限")
                .setCancelable(false)
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gotoMiuiPermission();
                        finish();
                    }
                })
                .show();
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SplashActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    /**
     * 跳转到miui的权限管理页面
     */
    private void gotoMiuiPermission() {
        Intent i = new Intent("miui.intent.action.APP_PERM_EDITOR");
        ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        i.setComponent(componentName);
        i.putExtra("extra_pkgname", BuildConfig.APPLICATION_ID);
        try {
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
            gotoMeizuPermission();
        }
    }
    /**
     * 跳转到魅族的权限管理系统
     */
    private void gotoMeizuPermission() {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            gotoHuaweiPermission();
        }
    }
    /**
     * 华为的权限管理页面
     */
    private void gotoHuaweiPermission() {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            startActivity(getAppDetailSettingIntent());
        }
    }
    /**
     * 获取应用详情页面intent
     *
     * @return
     */
    private Intent getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", BuildConfig.APPLICATION_ID);
        }
        return localIntent;
    }
    /**
     *防止在启动页时点击back返回键退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

}
