package com.zt.school.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import com.zt.school.R;
import com.zt.school.base.BaseActivity;
import com.zt.school.confige.Constant;
import butterknife.BindView;

public class AdWebDetailActivity extends BaseActivity {
    @BindView(R.id.webview)
    WebView mWebView;
    @BindView(R.id.id_base_title_tv)
    TextView mTitleTv;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_webdetail;
    }

    @Override
    protected int setStatueBarColor() {
        return getIntent().getIntExtra(Constant.BJ_COLOR,R.color.text_center_tab_selected);
    }

    @Override
    protected void initView() {
        String title = getIntent().getStringExtra(Constant.TITLE);
        mTitleTv.setText(title);
        initWebView();
    }
    private void initWebView() {
        mWebView.loadUrl(getIntent().getStringExtra(Constant.URL));
        WebSettings setting = mWebView.getSettings();
        mWebView.setInitialScale(50);
        setting.setBuiltInZoomControls(true);
        setting.setUseWideViewPort(true);//支持缩放
        setting.setJavaScriptEnabled(true);//支持js功能(js实现动态网页，标签控制/请求服务器...)
        mWebView.setWebViewClient(new WebViewClient() {
            //开始加载网页
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
            //网页加载结束
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
            //所有的连接跳转都会回调此方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mWebView.loadUrl(url);//连接跳转时，强制在当前webview加载
                return true;
            }
            //加载失败
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });
    }
    @Override
    protected void doBackClick(View view) {
        toMain();
    }
    private void toMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_scale,R.anim.translate_none);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mWebView!=null){
            mWebView.stopLoading();
            mWebView.clearCache(true);
            mWebView.clearHistory();
            mWebView.destroy();
            mWebView=null;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            toMain();
            return super.onKeyDown(keyCode,event);
        }
        return super.onKeyDown(keyCode,event);
    }
}
