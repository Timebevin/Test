package com.zt.school.views;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by 九七
 * 1.有时候应用中需要ScrollView嵌套ListView,但是往往listView会获取焦点占满屏幕，以致于ScrollView的其他控件无法显示，解决的办法
 * 2.自定义ScrollView:实现滑动距离监听器的方法
 */

public class CustomeScrollView extends ScrollView {
    public CustomeScrollView(Context context) {
        super(context);
    }

    public CustomeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomeScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //重写ScrollView,解决其包裹lv/gv时焦点被抢占问题
    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        return 0;
    }


    // 滑动距离监听器
    public interface IonScrollListener{
        /**
         * 在滑动的时候调用，scrollY为已滑动的距离
         */
        void onScroll(int scrollY);
    }

    private IonScrollListener listener;
    public void setIonScrollListener(IonScrollListener ionScrollListener){
        this.listener = ionScrollListener;
    }
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(listener != null){
            listener.onScroll(t);
        }
    }
}
