package com.zt.school.activity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flyco.tablayout.SlidingTabLayout;
import com.zt.school.R;
import com.zt.school.base.BaseActivity;
import com.zt.school.confige.Constant;
import com.zt.school.views.OrderStateTabPage;

import butterknife.BindView;

public class MyOrderStateListActivity extends BaseActivity {
    @BindView(R.id.root_bottomsheetlayout)
    BottomSheetLayout bottomSheetLayout;
    @BindView(R.id.id_base_title_tv)
    TextView mTitleTv;
    @BindView(R.id.slidingTablayout)
    SlidingTabLayout slidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private String[] titles = {"全部","待付款","待使用","待评价","退款/售后"};
    int allState = -1;//全部

    @Override
    protected int setContentViewId() {
        return R.layout.activity_myorder_state_list;
    }

    @Override
    protected boolean setStatusBarFontColorIsBlack() {
        return true;
    }

    private boolean isFirst = true;
    @Override
    protected void initView() {
        mTitleTv.setText("我的订单");
        final TabPageAdapter adapter;
        viewPager.setAdapter(adapter = new TabPageAdapter());
        slidingTabLayout.setViewPager(viewPager);
        slidingTabLayout.onPageSelected(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position==0 && isFirst){
                    isFirst = false;
                    //默认加载第0页数据
                    OrderStateTabPage page = adapter.pages[0];
                    page.initData(allState);
                }
            }
            @Override
            public void onPageSelected(int position) {
                OrderStateTabPage page =  adapter.pages[position];
                switch (position){
                    case 0://全部
                        page.initData(allState);
                        break;
                    case 1://待付款
                        page.initData(Constant.ORDER_STATE_WAIT_PAY);
                        break;
                    case 2://待使用
                        page.initData(Constant.ORDER_STATE_SHOP_HAVE_RECEIPT);
                        break;
                    case 3://待评价
                        page.initData(Constant.ORDER_STATE_WAIT_COMMENT);
                        break;
                    case 4://退款/售后
                        page.initData(Constant.ORDER_STATE_APPLY_REFUND);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    class TabPageAdapter extends PagerAdapter {
        public OrderStateTabPage[] pages;
        public TabPageAdapter(){
            pages = new OrderStateTabPage[titles.length];
        }
        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view==object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            OrderStateTabPage page = pages[position];
            if(page==null){
                page = new OrderStateTabPage(MyOrderStateListActivity.this);
                pages[position] = page;
            }
            container.addView(page);
            return page;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
