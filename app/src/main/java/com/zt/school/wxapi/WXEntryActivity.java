package com.zt.school.wxapi;
/**
 * Created 九七
 */

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zt.school.activity.UserLoginActivity;
import com.zt.school.confige.Constant;
import com.zt.school.utils.ToasUtils;

/*
   微信登陆/分享唤醒类
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;

    public static IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, Constant.WX_APPID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                ToasUtils.showToastMessage("拒绝");
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (RETURN_MSG_TYPE_SHARE == resp.getType()){
                    ToasUtils.showToastMessage("分享失败");
                }else {
                    ToasUtils.showToastMessage("登录失败");
                }
                break;
            case BaseResp.ErrCode.ERR_OK:
                switch (resp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN:
                        //拿到了微信返回的code,立马再去请求access_token
                        String code = ((SendAuth.Resp) resp).code;
                        Message message = new Message();
                        message.what = UserLoginActivity.WX_LOGIN;
                        message.obj = code;
                        UserLoginActivity.mHandler.sendMessage(message);
                        break;
                    case RETURN_MSG_TYPE_SHARE:
                        ToasUtils.showToastMessage("微信分享成功");
                        finish();
                        break;
                }
                break;
        }
        finish();
    }
}
