package com.zt.school.fragment;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gongwen.marqueen.MarqueeFactory;
import com.gongwen.marqueen.MarqueeView;
import com.kongzue.dialog.v2.SelectDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.zt.school.R;
import com.zt.school.activity.CustomScanCodeActivity;
import com.zt.school.activity.MapActivity;
import com.zt.school.activity.RandomActivity;
import com.zt.school.activity.SearchShopActivity;
import com.zt.school.activity.ShopFoodActivity;
import com.zt.school.activity.WebDetailActivity;
import com.zt.school.adapter.ShopListAdapter;
import com.zt.school.base.BaseFragment;
import com.zt.school.bmob.BannerData;
import com.zt.school.bmob.NewsBean;
import com.zt.school.bmob.ShopBean;
import com.zt.school.confige.Constant;
import com.zt.school.confige.MySharePreference;
import com.zt.school.update.UpGradeUtil;
import com.zt.school.utils.CustomCodeUtils;
import com.zt.school.utils.CustomListView;
import com.zt.school.utils.GlideImageLoader;
import com.zt.school.utils.NotificationUtils;
import com.zt.school.utils.ToasUtils;
import com.zt.school.utils.UIUtils;
import com.zt.school.views.CustomeScrollView;
import com.zt.school.views.CustomerViewMF;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by 九七
 */
public class FragmentHome extends BaseFragment {
    @BindView(R.id.banner)
    Banner mBanner;
    @BindView(R.id.listview)
    CustomListView listView;
    @BindView(R.id.martrefreshlayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.scrollView)
    CustomeScrollView customeScrollView;
    @BindView(R.id.id_index_top_search_view)
    FrameLayout searchParentView;
    @BindView(R.id.id_location_tv)
    TextView lovationTv;

    @BindView(R.id.marqueeView)
    MarqueeView marqueeView;

    @BindView(R.id.id_pmd_view)
    View pmdView;

    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;

    private ShopListAdapter adapter;

    Animation animation1;
    Animation animation2;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        init();
        //notification通知授予
        NotificationUtils.checkNotificationIsOpen(getActivity());
        //update app
        UpGradeUtil upGradeUtil = new UpGradeUtil(getActivity());
        upGradeUtil.checkUpdate(false);
        return view;
    }
    private void init(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            searchParentView.setPadding(0, UIUtils.getStateBarHeight(),0,0);
        }
        //scrollview滚动监听
        customeScrollView.setIonScrollListener(new CustomeScrollView.IonScrollListener(){
            @Override
            public void onScroll(int scrollY){
                //top search view
                searchParentView.setBackgroundColor(getResources().getColor(R.color.text_center_tab_selected));
                if(scrollY<=255){
                    searchParentView.getBackground().setAlpha(scrollY);//透明度范围 0-255
                }else{
                    searchParentView.getBackground().setAlpha(255);//透明度范围 0-255
                }
                //fab
                if (scrollY>755){
                    floatingActionButton.setVisibility(View.VISIBLE);
                }
                if (scrollY<=0){
                    floatingActionButton.setVisibility(View.GONE);
                }
            }
        });
        refreshData();
        refreshListtener();
        animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_item_scale1);
        animation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_item_scale2);
        String address = MySharePreference.getCurrentLocationInfo().address;
        if (address!=null)lovationTv.setText("当前位置 ：" + address);
    }
    private void refreshListtener(){
        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener(){
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                smartRefreshLayout.finishLoadMore();
            }
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshData();
                smartRefreshLayout.finishRefresh();
                ToasUtils.showToastMessage("刷新成功");
            }
        });
        smartRefreshLayout.setEnableLoadMore(false);
    }
    private void refreshData(){
        getAllBannerData();
        getAllNews();
        refreshAllShopListData();
    }

    //查询banner数据
    private void getAllBannerData(){
        BmobQuery<BannerData> bmobQuery = new BmobQuery<BannerData>();
        bmobQuery.order("-sort");//根据sort降序查询
        bmobQuery.addWhereEqualTo("isShow", true);
        bmobQuery.findObjects(new FindListener<BannerData>(){
            @Override
            public void done(List<BannerData> list, BmobException e) {
                if(e==null && list!=null && list.size()>0){
                    initBanner(list);
                }
            }
        });
    }

    private void initBanner(final List<BannerData> list){
        List<String> imgs = new ArrayList<>();
        for (BannerData bean : list){
            imgs.add(bean.getImg());
        }
        mBanner.setImageLoader(new GlideImageLoader());
        mBanner.setImages(imgs);
        mBanner.setDelayTime(3000);
        mBanner.setBannerAnimation(Transformer.Default);
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        mBanner.start();
        mBanner.setOnBannerListener(new OnBannerListener(){
            @Override
            public void OnBannerClick(int position){
               ToasUtils.showToastMessage(list.get(position).getUrl());
            }
        });
    }
    //获取商家数据
    private void refreshAllShopListData(){
        BmobQuery<ShopBean> bmobQuery = new BmobQuery<>();
        bmobQuery.order("-hot");//热度降序查询
        bmobQuery.addWhereEqualTo("isPass", true);
        bmobQuery.findObjects(new FindListener<ShopBean>(){
            @Override
            public void done(List<ShopBean> list, BmobException e){
                if(e==null && list!=null && list.size()>0){
                    if (adapter==null){
                        listView.setAdapter(adapter = new ShopListAdapter(getActivity(),list,R.layout.item_list_shop));
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                ShopBean bean = adapter.getItem(i);
                                Intent intent = new Intent(getActivity(), ShopFoodActivity.class);
                                intent.putExtra("shop", bean);
                                startActivity(intent);
                            }
                        });
                    }else {
                        adapter.refreshAllData(list);
                    }
                }else {
                    if (adapter!=null)adapter.clearAllData();
                }
            }
        });
    }
    //获取新闻数据
    private void getAllNews(){
        BmobQuery<NewsBean> bmobQuery = new BmobQuery<>();
        bmobQuery.order("-sort");//根据sort降序查询
        bmobQuery.addWhereEqualTo("isShow", true);
        bmobQuery.findObjects(new FindListener<NewsBean>() {
            @Override
            public void done(List<NewsBean> list, BmobException e) {
                if(e==null && list!=null && list.size()>0) {
                    pmdView.setVisibility(View.VISIBLE);
                    refreshPmd(list);
                }else {
                    marqueeView.removeAllViews();
                }
            }
        });
    }
    //跑马灯
    private void refreshPmd(List<NewsBean> list){
        //下拉刷新时移除跑马灯里的所有子view
        marqueeView.removeAllViews();
        if(getActivity()!=null){
            CustomerViewMF marqueeFactory = new CustomerViewMF(getActivity());//自定义跑马灯itemView
            marqueeFactory.setData(list);
            marqueeView.setMarqueeFactory(marqueeFactory);
            marqueeView.startFlipping();
            marqueeFactory.setOnItemClickListener(new MarqueeFactory.OnItemClickListener<LinearLayout, NewsBean>() {
                @Override
                public void onItemClickListener(MarqueeFactory.ViewHolder<LinearLayout, NewsBean> holder){
                    NewsBean newsBean = holder.data;
                    if (newsBean.type==Constant.TYPE_LOAD_URL){
                        Intent intent = new Intent(getActivity(), WebDetailActivity.class);
                        intent.putExtra(Constant.TITLE, newsBean.title);
                        intent.putExtra(Constant.TYPE, Constant.TYPE_LOAD_URL);
                        intent.putExtra(Constant.URL, newsBean.url);
                        startActivity(intent);
                    }else if (newsBean.type==Constant.TYPE_LOAD_HTML){
                        Intent intent = new Intent(getActivity(), WebDetailActivity.class);
                        intent.putExtra(Constant.TITLE, newsBean.title);
                        intent.putExtra(Constant.TYPE, Constant.TYPE_LOAD_HTML);
                        intent.putExtra(Constant.HTML, newsBean.html);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    @OnClick({R.id.fab,R.id.id_dw_iv,R.id.id_code_iv,R.id.top_search_tv,R.id.id_location_tv,R.id.fun1,R.id.fun2,R.id.fun3,R.id.fun4,R.id.fun5,R.id.fun6,R.id.fun7,R.id.fun8}) void click(final View view){
        if (view.getId()==R.id.fab){
            customeScrollView.post(new Runnable() {
                @Override
                public void run() {
                    customeScrollView.fullScroll(View.FOCUS_UP);
                }
            });
        }else if (view.getId()==R.id.top_search_tv){
            startActivity(new Intent(getActivity(),SearchShopActivity.class));
        }else if(view.getId()==R.id.id_code_iv){
            Intent intent = new Intent(getActivity(), CustomScanCodeActivity.class);
            startActivityForResult(intent, CustomCodeUtils.OPEN_CODE_SCAN);
        }else if (view.getId()==R.id.id_dw_iv || view.getId()==R.id.id_location_tv){
            Intent intent = new Intent(getActivity(),MapActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.translate_up_to_between,R.anim.translate_none);
        }else {
            //缩放动画
            view.startAnimation(animation1);
            animation1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.startAnimation(animation2);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            animation2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    dodo(view);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }
    private void dodo(View view){
        switch (view.getId()){
            case R.id.fun1:
//                Intent intent = new Intent(getActivity(), WebDetailActivity.class);
//                intent.putExtra(Constant.TITLE, "火锅");
//                intent.putExtra(Constant.TYPE, Constant.TYPE_LOAD_URL);
//                intent.putExtra(Constant.URL, "http://km.meituan.com/meishi/c17/");
//                startActivity(intent);
                Intent intent = new Intent(getActivity(), RandomActivity.class);
                intent.putExtra(Constant.TITLE, "万家灯火");
                startActivity(intent);
                break;
            case R.id.fun2:
                intent = new Intent(getActivity(), WebDetailActivity.class);
                intent.putExtra(Constant.TITLE, "饮品甜点");
                intent.putExtra(Constant.TYPE, Constant.TYPE_LOAD_URL);
                intent.putExtra(Constant.URL, "http://km.meituan.com/meishi/c11/");
                startActivity(intent);
                break;
            case R.id.fun3:
                intent = new Intent(getActivity(), WebDetailActivity.class);
                intent.putExtra(Constant.TITLE, "自助餐");
                intent.putExtra(Constant.TYPE, Constant.TYPE_LOAD_URL);
                intent.putExtra(Constant.URL, "http://km.meituan.com/meishi/c40/");
                startActivity(intent);
                break;
            case R.id.fun4:
                intent = new Intent(getActivity(), WebDetailActivity.class);
                intent.putExtra(Constant.TITLE, "小吃快餐");
                intent.putExtra(Constant.TYPE, Constant.TYPE_LOAD_URL);
                intent.putExtra(Constant.URL, "http://km.meituan.com/meishi/c36/");
                startActivity(intent);
                break;
            case R.id.fun5:
                intent = new Intent(getActivity(), WebDetailActivity.class);
                intent.putExtra(Constant.TITLE, "西餐");
                intent.putExtra(Constant.TYPE, Constant.TYPE_LOAD_URL);
                intent.putExtra(Constant.URL, "http://km.meituan.com/meishi/c35/");
                startActivity(intent);
                break;
            case R.id.fun6:
                intent = new Intent(getActivity(), WebDetailActivity.class);
                intent.putExtra(Constant.TITLE, "烧烤烤肉");
                intent.putExtra(Constant.TYPE, Constant.TYPE_LOAD_URL);
                intent.putExtra(Constant.URL, "http://km.meituan.com/meishi/c54/");
                startActivity(intent);
                break;
            case R.id.fun7:
                intent = new Intent(getActivity(), WebDetailActivity.class);
                intent.putExtra(Constant.TITLE, "香锅烤鱼");
                intent.putExtra(Constant.TYPE, Constant.TYPE_LOAD_URL);
                intent.putExtra(Constant.URL, "http://km.meituan.com/meishi/c20004/");
                startActivity(intent);
                break;
            case R.id.fun8:
                intent = new Intent(getActivity(), WebDetailActivity.class);
                intent.putExtra(Constant.TITLE, "海鲜");
                intent.putExtra(Constant.TYPE, Constant.TYPE_LOAD_URL);
                intent.putExtra(Constant.URL, "http://km.meituan.com/meishi/c63/");
                startActivity(intent);
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== CustomCodeUtils.OPEN_CODE_SCAN){
            if(resultCode==CustomCodeUtils.TYPE_XC_RETURN_CODE){
                //相册返回
                String result = data.getStringExtra(CodeUtils.RESULT_STRING);
                showCodeResultDialog(result);
            }else if(resultCode!=CustomCodeUtils.TYPE_XC_RETURN_CODE){
                //直接扫描返回
                if (null != data) {
                    Bundle bundle = data.getExtras();
                    if (bundle == null) {
                        return;
                    }
                    if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                        String result = bundle.getString(CodeUtils.RESULT_STRING);
                        showCodeResultDialog(result);
                    } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                        ToasUtils.showToastMessage("抱歉!请重试");
                    }
                }
            }
        }
    }
    private void showCodeResultDialog(String result){
        if (isHttpUrl(result)){
            urlTypeDialog(result);
        }else {
            textTypeDialog(result);
        }
    }
    private void urlTypeDialog(String result){
        SelectDialog.show(getActivity(), "扫描结果", result, "游览器打开", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse(result);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        }, "复制链接", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                copy(result);
            }
        }).setCanCancel(true);
    }
    private void textTypeDialog(String result){
        SelectDialog.show(getActivity(), "扫描结果", result, "复制文本", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                copy(result);
            }
        }, "关闭弹框", null).setCanCancel(true);
    }
    private void copy(String content){
        ClipboardManager copy = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        copy.setText(content);
        ToasUtils.showToastMessage("复制成功");
    }

    /**
     * 判断字符串是否为URL
     * @param urls 字符串
     * @return true:是URL、false:不是URL
     */
    public static boolean isHttpUrl(String urls){
        boolean isurl = false;
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))" + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//设置正则表达式
        Pattern pat = Pattern.compile(regex.trim());//比对
        Matcher mat = pat.matcher(urls.trim());
        isurl = mat.matches();//判断是否匹配
        if (isurl) {
            isurl = true;
        }
        return isurl;
    }
}
