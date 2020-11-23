package com.zt.school.activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.flyco.tablayout.SlidingTabLayout;
import com.zt.school.R;
import com.zt.school.base.BaseActivity;
import com.zt.school.confige.Constant;
import com.zt.school.views.OrderShopStateTabPage;
import butterknife.BindView;
/**
 * Author: 九七
 * 商家接收到顾客提交的订单activity
 */
public class ShopOrderActivity extends BaseActivity{
    @BindView(R.id.id_base_title_tv)
    TextView mTitleTv;
    @BindView(R.id.top_view)
    View topView;
    @BindView(R.id.slidingTablayout)
    SlidingTabLayout slidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private String[] titles = {"待接单","进行中","已完成","退款/售后"};
    private VpPageAdapter adapter;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_shop_order;
    }
    private boolean isFirst = true;
    @Override
    protected void initView(){
        mTitleTv.setText("我的订单");
        topView.setBackgroundColor(getResources().getColor(R.color.shop_top_back_color));
        viewPager.setAdapter(adapter = new VpPageAdapter());
        slidingTabLayout.setViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){
                if(position==0 && isFirst){
                    isFirst = false;
                    //默认加载第0页数据
                    OrderShopStateTabPage page =  adapter.pages[0];
                    page.initData(Constant.ORDER_STATE_PAY_SUCCESS);
                }
            }
            @Override
            public void onPageSelected(int position) {
                OrderShopStateTabPage page =  adapter.pages[position];
                switch (position){
                    case 0:
                        //待接单-用户已经支付成功
                        page.initData(Constant.ORDER_STATE_PAY_SUCCESS);
                        break;
                    case 1:
                        //进行中-为商家已确认接单
                        page.initData(Constant.ORDER_STATE_SHOP_HAVE_RECEIPT);
                        break;
                    case 2:
                        //已完成-订单评价完成
                        page.initData(666);//此处666仅为一个标记值，用于传递到方法里做状态标记使用
                        break;
                    case 3:
                        //退款/售后
                        page.initData(Constant.ORDER_STATE_APPLY_REFUND);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    @Override
    protected int setStatueBarColor() {
        return R.color.shop_top_back_color;
    }
    class  VpPageAdapter extends PagerAdapter{
        private OrderShopStateTabPage[] pages;
        public VpPageAdapter(){
            pages = new OrderShopStateTabPage[titles.length];
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
            OrderShopStateTabPage page = pages[position];
            if (page==null){
                page = new OrderShopStateTabPage(ShopOrderActivity.this);
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
