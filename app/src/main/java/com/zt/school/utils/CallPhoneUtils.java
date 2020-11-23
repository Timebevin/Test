package com.zt.school.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import com.kongzue.dialog.v2.SelectDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 九七
 */

public class CallPhoneUtils {
    //打电话
    private static void call(Context context, String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        context.startActivity(intent);
    }
    public static void callPhone(final Activity activity, final String number){
        SelectDialog.show(activity, "拨打", number, "呼叫", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    call(activity,number);
            }
        }, "取消", null).setCanCancel(true);
    }
    public static void callPhone(final Activity activity, String title,final String number){
        SelectDialog.show(activity, title, number, "呼叫", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                call(activity,number);
            }
        }, "取消", null).setCanCancel(true);
    }
    /**
     * 判断电话号码是否符合格式.
     *
     * @param inputText the input text
     * @return true, if is phone
     */
    public static boolean isPhone(String inputText) {
        Pattern p = Pattern.compile("^((14[0-9])|(13[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$");
        Matcher m = p.matcher(inputText);
        return m.matches();
    }

}
