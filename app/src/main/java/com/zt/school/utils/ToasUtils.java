package com.zt.school.utils;

import android.widget.Toast;

/**
 * Created by 九七
 */

public class ToasUtils {
    private static Toast mToast;
    private ToasUtils(){}
    public static void showToastMessage(String message){
        if(mToast==null){
            synchronized (ToasUtils.class){
                mToast = Toast.makeText(UIUtils.getContext(), message, Toast.LENGTH_SHORT);
            }
        }
        mToast.setText(message);
        mToast.show();
    }
}
