package com.zt.school.confige;

import android.content.Context;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.kongzue.dialog.util.TextInfo;
import com.kongzue.dialog.v2.DialogSettings;
import com.lzy.okgo.OkGo;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import com.zt.school.bmob.BannerData;
import com.zt.school.bmob.UpdateBean;
import com.zt.school.utils.NetworkUtil;
import com.zt.school.utils.NotificationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobConfig;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;
import okhttp3.OkHttpClient;

/**
 * Created by 九七
 */

public class MyApplication extends MultiDexApplication {
    private static Context context;
    private static Handler handler;
    private static int mainThreadId;
    public static IWXAPI iwxapi;

    @Override
    public void onCreate() {
        super.onCreate();

        // 第一：初始化BmobSDK
        Bmob.initialize(this, "7fc425f6547f8fab788cb96dac8b8520");

        MySharePreference.init(this);
        context = getApplicationContext();
        handler = new Handler();
        mainThreadId = android.os.Process.myTid();//获取当前线程id，主线程

        //微信 注册APPID
        iwxapi = WXAPIFactory.createWXAPI(context, Constant.WX_APPID, false);
        // 将该app注册到微信
        iwxapi.registerApp(Constant.WX_APPID);

        // Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
        // 其中APP_ID是分配给第三方应用的appid，类型为String。
        Tencent mTencent = Tencent.createInstance(Constant.TENCENT_APP_ID, this.getApplicationContext());

        /**
         * 必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回
         * 第一个参数：应用程序上下文
         * 第二个参数：如果发现滑动返回后立即触摸界面时应用崩溃，请把该界面里比较特殊的 View 的 class 添加到该集合中，目前在库中已经添加了 WebView 和 SurfaceView
         */
        BGASwipeBackHelper.init(this, null);
        //network init configer
        netWorkConfiger();

        //ios
        DialogSettings.style = DialogSettings.STYLE_IOS;
        DialogSettings.tip_theme = DialogSettings.THEME_LIGHT;
        DialogSettings.use_blur = true;

        DialogSettings.dialogContentTextInfo = new TextInfo().setBold(false);
        DialogSettings.dialogTitleTextInfo = new TextInfo().setBold(false).setFontSize(15);
        DialogSettings.dialogButtonTextInfo = new TextInfo().setBold(false).setFontSize(15);
        DialogSettings.dialogOkButtonTextInfo = new TextInfo().setBold(false).setFontSize(15);

        //适配8.0通知栏，注册创建Channel
        NotificationUtils.registerNotificationConfig();

        ClassicsFooter.REFRESH_FOOTER_NOTHING = "已经木有内容啦";

        //设置BmobConfig
        BmobConfig config =new BmobConfig.Builder(this)
                //请求超时时间（单位为秒）：默认15s
                .setConnectTimeout(30)
                //文件分片上传时每片的大小（单位字节），默认512*1024
                .setUploadBlockSize(500*1024)
                .build();

        //默认数据初始化
        initData();
    }
    private void netWorkConfiger() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //全局的读取超时时间
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的写入超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的连接超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);

        OkGo.getInstance().init(this)                       //必须调用初始化
                .setOkHttpClient(builder.build())           //建议设置OkHttpClient，不设置将使用默认的
                .setRetryCount(3);                          //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
    }
    public static Context getContext() {
        return context;
    }
    public static Handler getHandler(){
        return handler;
    }
    public static int getMainThreadId(){
        return mainThreadId;
    }
    private void initData(){
        BmobQuery<BannerData> query = new BmobQuery<>();
        query.findObjects(new FindListener<BannerData>() {
            @Override
            public void done(List<BannerData> list, BmobException e) {
                if (e!=null && NetworkUtil.checkedNetWork(context))addBannerData();
            }
        });

        BmobQuery<UpdateBean> query2 = new BmobQuery<>();
        query2.findObjects(new FindListener<UpdateBean>() {
            @Override
            public void done(List<UpdateBean> list, BmobException e) {
                if (e!=null && NetworkUtil.checkedNetWork(context))initUpdateForm();
            }
        });
    }
    //批量添加banner
    private void addBannerData(){
        BannerData b = new BannerData();
        b.setImg("http://bmob-cdn-8707.b0.upaiyun.com/2019/02/10/c2305e4d406d2fce80d60c7ea30bdf23.png");
        b.setUrl("http://waimai.meituan.com/");

        BannerData b1 = new BannerData();
        b1.setImg("http://bmob-cdn-8707.b0.upaiyun.com/2019/02/10/f9a1705c406bec8680b24575f6f2cc26.jpg");
        b1.setUrl("http://waimai.meituan.com/");

        BannerData b2 = new BannerData();
        b2.setImg("http://bmob-cdn-8707.b0.upaiyun.com/2019/02/10/b15c931540589ff2808d030d829a71cf.jpg");
        b2.setUrl("http://waimai.meituan.com/");

        BannerData b3 = new BannerData();
        b3.setImg("http://bmob-cdn-8707.b0.upaiyun.com/2019/02/10/08f321ba40e17de080f5a33372e1fb1a.jpg");
        b3.setUrl("http://waimai.meituan.com/");

        List<BmobObject> bannerList=new ArrayList<>();
        bannerList.add(b);
        bannerList.add(b1);
        bannerList.add(b2);
        bannerList.add(b3);
        new BmobBatch().insertBatch(bannerList).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> list, BmobException e) {
                if (e!=null && list!=null && list.size()>0){
                    Log.v("TAG", "BannerData批量添加成功");
                }
            }
        });
    }
    //初始化app版本更新表
    private void initUpdateForm(){
        UpdateBean updateBean = new UpdateBean();
        updateBean.versionCode = 1;
        updateBean.apkUrl = "https://www.baidu.com/test.apk";
        updateBean.forceUpdate = false;
        updateBean.title = "标题";
        updateBean.updateMsg = "更新内容";
        updateBean.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {

            }
        });
    }
}
