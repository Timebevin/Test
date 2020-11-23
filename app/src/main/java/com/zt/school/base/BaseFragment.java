package com.zt.school.base;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.zt.school.R;


/**
 * Created by 九七
 */

public class BaseFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    //设置界面跳转动画
    @Override
    public void startActivity(Intent intent){
        super.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.translate_right_to_between,R.anim.translate_between_to_left);
    }
    /**
     * 关闭软键盘
     */
    protected void closeSoftware(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getActivity().getCurrentFocus() != null){
            if (getActivity().getCurrentFocus().getWindowToken() != null){
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
