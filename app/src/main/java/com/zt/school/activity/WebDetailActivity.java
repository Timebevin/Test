package com.zt.school.activity;

import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.kongzue.dialog.v2.WaitDialog;
import com.zt.school.R;
import com.zt.school.base.BaseActivity;
import com.zt.school.confige.Constant;
import com.zt.school.utils.ToasUtils;

import butterknife.BindView;

/**
 * 需传入：
 * TITLE：标题，必须
 * TYPE：加载类型，必须
 * URL：文章连接，必须
 */
public class WebDetailActivity extends BaseActivity {
    @BindView(R.id.id_root_top)
    View topView;
    @BindView(R.id.id_base_title_tv)
    TextView mTitleTv;
    @BindView(R.id.id_wb)
    WebView mWebView;
    @Override
    protected int setContentViewId() {
        return R.layout.layout_webview;
    }

    @Override
    protected boolean setStatusBarFontColorIsBlack() {
        return true;
    }

    @Override
    protected void initView() {
        String title = getIntent().getStringExtra(Constant.TITLE);
        mTitleTv.setText("文章详情");
        int type = getIntent().getIntExtra(Constant.TYPE, Constant.TYPE_LOAD_URL);
        if (type == Constant.TYPE_LOAD_URL) {
            String url = getIntent().getStringExtra(Constant.URL);
            initWebView(url);
        }else if (type==Constant.TYPE_LOAD_HTML){
            String html = getIntent().getStringExtra(Constant.HTML);
            showWebView(html);
        }
    }
    private boolean isLoadError;
    private void initWebView(String url) {
        WaitDialog.show(this, "数据加载中...").setCanCancel(true);
        mWebView.loadUrl(url);
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
                WaitDialog.dismiss();
                if(isLoadError){
                    ToasUtils.showToastMessage("网络连接异常 请连接成功后重试");
                    isLoadError = false;
                }
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
                isLoadError = true;
            }
        });
    }
    //加载html字符串
    private void showWebView(String html)
    {
        WaitDialog.show(this, "数据加载中...").setCanCancel(true);
        // 设置WevView要显示的网页
        mWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8",null);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setJavaScriptEnabled(true); //设置支持Javascript
        mWebView.requestFocus(); //触摸焦点起作用.如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件。
        //点击链接由自己处理，而不是新开Android的系统browser响应该链接。
        mWebView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                //设置点击网页里面的链接还是在当前的webview里跳转
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                WaitDialog.dismiss();
            }
        });
        //设置字体大小
        mWebView.getSettings().setDefaultFontSize(11);
    }
    @Override
    protected void onDestroy(){
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
            WaitDialog.dismiss();
        }
        return super.onKeyDown(keyCode,event);
    }
}
