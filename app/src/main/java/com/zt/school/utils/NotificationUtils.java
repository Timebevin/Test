package com.zt.school.utils;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;

import com.kongzue.dialog.v2.SelectDialog;

import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 极光SDK version:3.1.5 通知渠道(渠道名：Notification 渠道ID:JPush)检测（android8.0系统 极光通知默认关闭）是否打开
 */
public class NotificationUtils {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void checkNotificationIsOpen(Context context){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            return;
        }
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        List<NotificationChannel> list = manager.getNotificationChannels();
        for (NotificationChannel channel  : list){
            String channelId = channel.getId();
            if("JPush".equals(channelId)){
                if(channel.getImportance()== NotificationManager.IMPORTANCE_NONE){ //极光 通知关闭
                    show(context,channelId);
                    return;
                }
            }else if("Download".equals(channelId)){
                if(channel.getImportance()== NotificationManager.IMPORTANCE_NONE){ //apk/文件下载 通知关闭
                    show(context,channelId);
                    return;
                }
            }
        }
    }

    private static void show(final Context context, final String channelId){
        SelectDialog.show(context, "提示", "请允许或打开通知否则查看不到通知栏信息", "去打开", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, "com.zt.mt");
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId);
                context.startActivity(intent);
            }
        }, "下次再说", null);
    }

    /**
     * Android 8.0 接收到通知但没显示ui bug处理
     * 极光sdk3.1.5 在收到service端发送来的通知后会创建一个channelId 为JPush channelName为Notification的通知渠道
     * 在Android8.0系列的手机在极光发送通知，app端广播接收者接收到通知后，Jpush SDK才会创建该渠道，故在此先创建一个channelId相同的渠道，让用户手动授予后，当service端推送一条通知
     * 即可显示通知ui,此时channelName就变为Notification，将我们之前设置的覆盖
     */
    public static void registerNotificationConfig(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "JPush";
            String channelName = "打开推送通知";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);

            String channelId2 = "Download";
            String channelName2 = "打开文件下载通知";
            createNotificationChannel(channelId2, channelName2, importance);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.setShowBadge(true);
        NotificationManager notificationManager = (NotificationManager) UIUtils.getContext().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }
}
