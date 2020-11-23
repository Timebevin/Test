package com.zt.school.fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.flyco.tablayout.SlidingTabLayout;
import com.zt.school.R;
import com.zt.school.base.BaseFragment;
import com.zt.school.confige.Constant;
import com.zt.school.utils.UIUtils;
import com.zt.school.views.OrderStateTabPage;
import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * Author: 九七
 */
public class FragmentOrder extends BaseFragment {
    @BindView(R.id.root_ll)
    LinearLayout rootLinearlayout;
    @BindView(R.id.slidingTablayout)
    SlidingTabLayout slidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private String[] titles = {"全部","待付款","待使用","待评价","退款/售后"};
    private Context mContext;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }
    private boolean isFirst = true;
    private void init(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            rootLinearlayout.setPadding(0, UIUtils.getStateBarHeight(),0,0);
        }
        mContext = getActivity();
        final FragmentOrder.TabPageAdapter adapter;
        viewPager.setAdapter(adapter = new FragmentOrder.TabPageAdapter());
        slidingTabLayout.setViewPager(viewPager);
        slidingTabLayout.onPageSelected(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position==0 && isFirst){
                    isFirst = false;
                    //默认加载第0页数据
                    OrderStateTabPage page =  adapter.pages[0];
                    page.initData(-1);
                }
            }
            @Override
            public void onPageSelected(int position) {
                OrderStateTabPage page =  adapter.pages[position];
                switch (position){
                    case 0://全部
                        page.initData(-1);
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
                page = new OrderStateTabPage(mContext);
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
