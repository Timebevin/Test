package com.zt.school.update;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.zt.school.R;
import com.zt.school.confige.MySharePreference;
import com.zt.school.utils.NetworkUtil;
import com.zt.school.utils.ToasUtils;
import com.zt.school.utils.UIUtils;

import java.io.File;

/**
 * Created by 九七
 * app版本更新
 */

public class DownLoadService extends Service{

    private NotificationCompat.Builder builder;
    private NotificationManager manager;
    private static int NOTI_ID = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.logo);
        builder.setContentTitle(getResources().getString(R.string.app_name));
        builder.setTicker(getResources().getString(R.string.app_name)+"正在更新中...");
        builder.setAutoCancel(true);
        builder.setOngoing(true);
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //适配8.0系统
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            builder.setChannelId("Download");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        String apkUrl = intent.getStringExtra("apkUrl");
        download(apkUrl);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        manager.cancel(NOTI_ID);
        stopSelf();
    }
    //使用该方法前6.0上的系统需要动态申请的写sd卡权限
    private void download(final String apkUrl){
        MySharePreference.setUpdateAppStatus(true);
        final String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
        final String apkName = "mt.apk";
        OkGo.<File>get(apkUrl)
                .execute(new FileCallback(path,apkName){
                    @Override
                    public void onSuccess(Response<File> response){
                        manager.cancel(NOTI_ID);
                        installApk(DownLoadService.this,path+apkName);
                        DownLoadService.this.stopSelf();
                    }
                    @Override
                    public void onError(Response<File> response){
                        if (!NetworkUtil.checkedNetWork(UIUtils.getContext())){
                            ToasUtils.showToastMessage("下载失败,请检查你的网络连接");
                        }
                    }
                    @Override
                    public void downloadProgress(Progress progress){
                        int ps = (int) (progress.fraction * 100);
                        showNotiFication(progress);
                    }
                });
    }
    private void showNotiFication(Progress progress){
        int ps = (int) (progress.fraction * 100);
        builder.setProgress(100, ps, false);
        builder.setContentText("当前下载进度:" + ps+"%");
        manager.notify(NOTI_ID,builder.build());
    }
    /**
     * 1.下载成功后，跳转到系统的APP安装界面进行安装
     * 2.修改代码适配Android N
     */
    private void installApk(Context context, String apkSdPath){
        MySharePreference.setUpdateAppStatus(false);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判断是否是Android N以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(apkSdPath));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(apkSdPath)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }
}
