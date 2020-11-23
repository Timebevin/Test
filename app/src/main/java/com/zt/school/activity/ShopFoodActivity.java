package com.zt.school.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.kongzue.dialog.listener.OnMenuItemClickListener;
import com.kongzue.dialog.v2.BottomMenu;
import com.kongzue.dialog.v2.MessageDialog;
import com.zt.school.R;
import com.zt.school.adapter.FoodOrderBottomRecycleAdapter;
import com.zt.school.adapter.LeftListAdapter;
import com.zt.school.adapter.StickRightListAdapter;
import com.zt.school.base.BaseActivity;
import com.zt.school.bean.StickBean;
import com.zt.school.bmob.AppKeyBean;
import com.zt.school.bmob.FoodBean;
import com.zt.school.bmob.FoodCateBean;
import com.zt.school.bmob.ShopBean;
import com.zt.school.confige.Constant;
import com.zt.school.confige.MySharePreference;
import com.zt.school.utils.CallPhoneUtils;
import com.zt.school.utils.GaoDeMapUtils;
import com.zt.school.utils.GlideUtils;
import com.zt.school.utils.ToasUtils;
import com.zt.school.views.CircleImageView;
import com.zt.school.views.MyTextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ShopFoodActivity extends BaseActivity {
    @BindView(R.id.profile_image)
    CircleImageView circleImageView;
    @BindView(R.id.id_roder_shop_name)
    TextView shopNameTv;
    @BindView(R.id.id_roder_shop_address)
    TextView shopAddressTv;
    @BindView(R.id.id_left_listview)
    ListView leftLv;
    ShopBean shopBean;

    @BindView(R.id.imgCart)
    FrameLayout imgCart;//购物车
    @BindView(R.id.id_count_tv)
    MyTextView countTv;
    @BindView(R.id.id_price_tv)
    TextView totalPriceTv;
    @BindView(R.id.rootView)
    ViewGroup rootView;
    @BindView(R.id.id_stick_listview)
    StickyListHeadersListView stickyListHeadersListView;

    private LeftListAdapter leftAdapter;
    private Handler mHanlder;
    private StickRightListAdapter rightAdapter;

    private View bottomSheet;

    private float minPrice = 0.1f;//最低起送价格


    @Override
    protected int setContentViewId(){
        return R.layout.activity_food_order_layout;
    }

    @Override
    protected boolean setStatusBarFontColorIsBlack(){
        return true;
    }

    @Override
    protected void initView(){
        shopBean = (ShopBean) getIntent().getSerializableExtra("shop");
        GlideUtils.load(shopBean.logo,circleImageView);
        shopNameTv.setText(shopBean.name);
        shopAddressTv.setText(shopBean.address);
    }
    private void updateShopHot(){
        //每进入此界面 ShopBean的hot值+1
        BmobQuery<ShopBean> query = new BmobQuery<>();
        query.getObject(shopBean.getObjectId(), new QueryListener<ShopBean>(){
            @Override
            public void done(ShopBean shopBean, BmobException e) {
                if (e==null){
                    ShopBean bean = new ShopBean();
                    bean.hot = String.valueOf(Integer.valueOf(shopBean.hot)+1);
                    bean.update(shopBean.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            //hot值+1成功
                        }
                    });
                }
            }
        });
    }
    @Override
    protected void initData(){
        mHanlder = new Handler();
        getAppKey();
        getCateData();
        updateShopHot();
        bottomSheet = createBottomSheetView();
    }
    private String appkey;
    private void getAppKey(){
        BmobQuery<AppKeyBean> query = new BmobQuery<>();
        query.findObjects(new FindListener<AppKeyBean>(){
            @Override
            public void done(List<AppKeyBean> list, BmobException e) {
                    if (e==null && list!=null && list.size()>0){
                        appkey = list.get(0).appkey;
                    }
            }
        });
    }
    @OnClick({R.id.id_order_daohan_tv,R.id.id_order_phone_tv,R.id.id_show_food_yddc_tv,R.id.imgCart,R.id.id_order_comment_tv}) void click(View view){
        switch (view.getId()){
            case R.id.id_order_daohan_tv:
                daohan();
                break;
            case R.id.id_order_phone_tv:
                CallPhoneUtils.callPhone(this,shopBean.tel);
                break;
            case R.id.id_show_food_yddc_tv:
                if (cartFoodsMap.size()==0){
                    MessageDialog.show(this, "提示", "你还没有点餐哦！").setCanCancel(true);
                    return;
                }
                if (totalPrice<minPrice){
                    ToasUtils.showToastMessage(minPrice + "元起送！");
                    return;
                }
                if (appkey==null){
                    getAppKey();
                }
                ArrayList<FoodBean> list = new ArrayList<>();
                Collection<FoodBean> carts = cartFoodsMap.values();
                for (FoodBean foodCateBean:carts){
                    list.add(foodCateBean);
                }
                Intent intent = new Intent(this, CommitOrderActivity.class);
                intent.putExtra("list", list);
                intent.putExtra("shop", shopBean);
                intent.putExtra("appkey", appkey!=null?appkey:"appkey");
                startActivityForResult(intent,100);
                break;
            case R.id.imgCart:
                showCartView();
                break;
            case R.id.id_order_comment_tv:
                intent = new Intent(this, UserCommentListActivity.class);
                intent.putExtra("shopId", shopBean.getObjectId());
                intent.putExtra(Constant.TYPE, 1);
                startActivity(intent);
                break;
        }
    }
    //获取商家菜品分类
    private void getCateData(){
        BmobQuery<FoodCateBean> query = new BmobQuery<>();
        query.addWhereEqualTo("shopId", shopBean.getObjectId());
        query.findObjects(new FindListener<FoodCateBean>(){
            @Override
            public void done(List<FoodCateBean> list, BmobException e) {
                if (e==null && list!=null && list.size()>0){
                    leftLv.setAdapter(leftAdapter = new LeftListAdapter(list,ShopFoodActivity.this));
                    leftLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (rightAdapter!=null)stickyListHeadersListView.setSelection(i);
                            leftAdapter.leftItemSelectStateChange(i);
                        }
                    });
                    //获取所有菜品
                    getFoodData(list);
                }
            }
        });
    }
    //根据商家id和分类id获取所有菜品
    private void getFoodData(final List<FoodCateBean> cates){
        BmobQuery<FoodBean> query = new BmobQuery<>();
        query.addWhereEqualTo("shopId", shopBean.getObjectId());
        query.findObjects(new FindListener<FoodBean>() {
            @Override
            public void done(List<FoodBean> foods, BmobException e) {
                if (e==null && foods!=null && foods.size()>0){
                    List<StickBean> stickBeans = new ArrayList<>();
                    for (int i=0;i<cates.size();i++){
                        FoodCateBean cate = cates.get(i);
                        StickBean stickBean = new StickBean();
                        stickBean.headerId = i;
                        stickBean.headerName = cates.get(i).name;
                        stickBean.foodBeanList = new ArrayList<>();
                        for (FoodBean food : foods){
                            if (food.cateId.equals(cate.getObjectId())){
                                stickBean.foodBeanList.add(food);
                            }
                        }
                        stickBeans.add(stickBean);
                    }
                    //更新右侧列表
                    stickyListHeadersListView.setAdapter(rightAdapter = new StickRightListAdapter(ShopFoodActivity.this,stickBeans));
                    //stickyListHeadersListView头改变监听
                    stickyListHeadersListView.setOnStickyHeaderChangedListener(new StickyListHeadersListView.OnStickyHeaderChangedListener() {
                        @Override
                        public void onStickyHeaderChanged(StickyListHeadersListView l, View header, int itemPosition, long headerId) {
                            if (leftAdapter!=null)leftAdapter.leftItemSelectStateChange((int) headerId);
                        }
                    });
                }
            }
        });
    }
    /**
     * 小球跳转动画....
     */
    public void playAnimation(int[] start_location){
        ImageView img = new ImageView(this);
        img.setImageResource(R.mipmap.add_icon);
        setAnim(img,start_location);
    }
    private void setAnim(final View v, int[] start_location) {
        addViewToAnimLayout(rootView, v, start_location);
        Animation set = createAnim(start_location[0],start_location[1]);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            // 动画的结束
            @Override
            public void onAnimationEnd(final Animation animation) {
                mHanlder.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rootView.removeView(v);
                    }
                },100);
                //ZT:隐藏小球，解决列表滑动时任显示小球的bug
                v.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(set);
    }
    private void addViewToAnimLayout(final ViewGroup vg, final View view, int[] location) {
        int x = location[0];
        int y = location[1];
        int[] loc = new int[2];
        vg.getLocationInWindow(loc);
        view.setX(x);
        view.setY(y-loc[1]);
        vg.addView(view);
    }
    private Animation createAnim(int startX,int startY){
        int[] des = new int[2];
        imgCart.getLocationInWindow(des);

        AnimationSet set = new AnimationSet(false);
        //ZT:+40
        Animation translationX = new TranslateAnimation(0, des[0]-startX+40, 0, 0);
        translationX.setInterpolator(new LinearInterpolator());
        Animation translationY = new TranslateAnimation(0, 0, 0, des[1]-startY);
        translationY.setInterpolator(new AccelerateInterpolator());
        Animation alpha = new AlphaAnimation(1,0.5f);
        set.addAnimation(translationX);
        set.addAnimation(translationY);
        set.addAnimation(alpha);
        set.setDuration(500);
        return set;
    }

    /**
     * HashMap来存储用户加入购物车里的菜品数据
     */
    Map<String, FoodBean> cartFoodsMap = new HashMap<>();
    //菜品加入购物车
    public void add(FoodBean foodBean){
        String key = foodBean.getObjectId();
        if (!cartFoodsMap.containsKey(key)){
            cartFoodsMap.put(key, foodBean);
        }else {
            FoodBean food = cartFoodsMap.get(key);
            food.total = foodBean.total;//更新订餐数量
        }
        updateUi();
    }
    //菜品从购物车减少或移除
    public void remove(FoodBean foodBean){
        String key = foodBean.getObjectId();
        if (cartFoodsMap.containsKey(key)){
            FoodBean food = cartFoodsMap.get(key);
            if (foodBean.total>0){
                food.total = foodBean.total;//更新订餐数量
            }else {
                cartFoodsMap.remove(key);//移除该key
                if (cartFoodsMap.size()==0){
                    totalPrice = 0;
                    if (foodOrderBottomRecycleAdapter!= null)
                        foodOrderBottomRecycleAdapter.list.clear();
                }
            }
        }
        updateUi();
    }

    float totalPrice;
    //更新ui
    private void updateUi(){
        if (cartFoodsMap.size()>0){
            countTv.setVisibility(View.VISIBLE);
            int sum = 0;
            totalPrice = 0;
            Collection<FoodBean> values = cartFoodsMap.values();
            for (FoodBean foodBean : values){
                sum = sum + foodBean.total;
                totalPrice = totalPrice + Float.valueOf(foodBean.price) * foodBean.total;
            }
            countTv.setText(String.valueOf(sum));
            totalPriceTv.setText(Constant.PRICE_MARK+totalPrice+"元");
        }else {
            countTv.setVisibility(View.GONE);
            countTv.setText(String.valueOf(0));
            totalPriceTv.setText(Constant.PRICE_MARK+"0.0"+"元");
        }
        refreshLeftUi();
        requestCartListData();
    }
    private void refreshLeftUi(){
        List<FoodCateBean> cates = leftAdapter.getData();
        if(cates!=null && cates.size()>0){
            List<StickBean> datas = rightAdapter.getDatas();
            if (datas!=null && datas.size()>0){
                for (int i=0;i<cates.size();i++){
                    FoodCateBean cate = cates.get(i);
                    StickBean stickBean = datas.get(i);
                    List<FoodBean> foods = stickBean.foodBeanList;
                    int total = 0;
                    if (foods!=null && foods.size()>0){
                        for (FoodBean foodBean: foods){
                            total = total + foodBean.total;
                        }
                        cate.total = total;
                        //刷新左侧列表
                        if (leftAdapter!=null){
                            leftAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
    }
    /**
     * 底部弹出view
     */
    @BindView(R.id.bottomsheet)
    BottomSheetLayout bottomSheetLayout;
    private void showCartView(){
        if(bottomSheetLayout.isSheetShowing()){
            bottomSheetLayout.dismissSheet();
        }else {
            if(foodOrderBottomRecycleAdapter!=null){
                if(foodOrderBottomRecycleAdapter.list.size()>0){
                    bottomSheetLayout.showWithSheetView(bottomSheet);
                }
            }
        }
    }
    private RecyclerView selectRecyclerView;
    private View createBottomSheetView(){
        View view = LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet,(ViewGroup) getWindow().getDecorView(),false);
        selectRecyclerView = view.findViewById(R.id.selectRecyclerView);
        TextView clear = view.findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                clearCart();
            }
        });
        requestCartListData();
        return view;
    }
    private FoodOrderBottomRecycleAdapter foodOrderBottomRecycleAdapter;
    private void recycleViewInit(List<FoodBean> list){
        if(foodOrderBottomRecycleAdapter==null){
            selectRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            foodOrderBottomRecycleAdapter = new FoodOrderBottomRecycleAdapter(this, list);
            selectRecyclerView.setAdapter(foodOrderBottomRecycleAdapter);
        }else {
            foodOrderBottomRecycleAdapter.refreshRecycleData(list);
        }
    }
    //数据更新
    private void requestCartListData(){
        Collection<FoodBean> carts = cartFoodsMap.values();
        if (carts!=null && carts.size()>0){
            List<FoodBean> list = new ArrayList<>();
            for (FoodBean foodCateBean:carts){
                list.add(foodCateBean);
            }
            recycleViewInit(list);
        }
    }
    private void clearCart(){
        //清空hashMap数据
        cartFoodsMap.clear();
        //清空购物车商品数据
        foodOrderBottomRecycleAdapter.list.clear();
        //将右侧选择的菜品数量至0
        List<StickBean> datas = rightAdapter.getDatas();
        for (StickBean stickBean : datas){
            List<FoodBean> list = stickBean.foodBeanList;
            for (FoodBean foodBean:list){
                foodBean.total = 0;
            }
        }
        //刷新右侧列表
        if (rightAdapter!=null)rightAdapter.notifyDataSetChanged();
        updateUi();
        bottomSheetLayout.dismissSheet();
    }
    private void daohan(){
        List<String> list = new ArrayList<>();
        list.add("高德地图");
        list.add("百度地图");
        BottomMenu.show(this, list, new OnMenuItemClickListener() {
            @Override
            public void onClick(String text, int index) {
                switch (index){
                    case 0:
                            GaoDeMapUtils.openGaoDeMap(ShopFoodActivity.this, MySharePreference.getCurrentLocationInfo().lat,MySharePreference.getCurrentLocationInfo().lng,MySharePreference.getCurrentLocationInfo().street,
                                    shopBean.lat,shopBean.lng,shopBean.name);
                        break;
                    case 1:
                            GaoDeMapUtils.openBaiduMap(ShopFoodActivity.this,MySharePreference.getCurrentLocationInfo().lat,MySharePreference.getCurrentLocationInfo().lng,
                                    shopBean.lat,shopBean.lng,shopBean.name);
                        break;
                }
            }
        },true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100 && resultCode==201)finish();
        Log.v("TAG", requestCode + "     " + resultCode);
    }
}
