package com.zt.school.fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zt.school.R;
import com.zt.school.base.BaseFragment;
import com.zt.school.confige.Constant;
import com.zt.school.confige.MySharePreference;
import com.zt.school.views.ShopPage;
import com.zt.school.views.UserPage;

/**
 * Created by 九七
 */
public class FragmentMine extends BaseFragment {
    private static FrameLayout mFrameLayout;
    private View rootView;
    private static UserPage userPage;
    private static ShopPage shopPage;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mine, container, false);
        init();
        return rootView;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void init() {
        mFrameLayout = rootView.findViewById(R.id.mine_fragment_framelayout);
        mFrameLayout.addView(userPage = new UserPage(getActivity()));
        mFrameLayout.addView(shopPage = new ShopPage(getActivity()));
        initState();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void initState(){
        //根据登陆状态显示相应的界面
        int state = MySharePreference.getCurrentLoginState();
        if(state== Constant.STATE_UN_LOING || state==Constant.STATE_NORMAL_LOGIN_SUC){
            showNormalView();
            if(userPage!=null){
                userPage.updateUi();
            }
        }else if (state == Constant.STATE_BUSINESS_LOGIN_SUC){
            showShopView();
            if(shopPage!=null){
                shopPage.updateUi();
            }
        }else if(state == Constant.STATE_UN_LOING){
            showShopView();
            if (userPage!=null)userPage.updateUi();
        }
    }
    private static void showNormalView(){
        if(mFrameLayout!=null){
            mFrameLayout.getChildAt(0).setVisibility(View.VISIBLE);
            mFrameLayout.getChildAt(1).setVisibility(View.GONE);
        }
    }
    private static void showShopView() {
        if(mFrameLayout!=null){
            mFrameLayout.getChildAt(0).setVisibility(View.GONE);
            mFrameLayout.getChildAt(1).setVisibility(View.VISIBLE);
        }
    }
}
