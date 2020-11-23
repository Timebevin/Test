package com.zt.school.confige;

/**
 * Created by 九七
 */

public class Constant {
    public static final int STATE_UN_LOING = 0; //未登录状态
    public static final int STATE_NORMAL_LOGIN_SUC = 1;//普通用户登录状态
    public static final int STATE_BUSINESS_LOGIN_SUC = 2;//商家登录状态

    public static final int PAY_WX = 1; //支付宝
    public static final int PAY_ZFB = 2;//微信支付

    public static final int TYPE_LOAD_URL = 1;
    public static final int TYPE_LOAD_HTML = 2;

    public static final String POSITION = "position";
    public static final String IMAGES = "images";
    public static final String TITLE = "title";
    public static final String PRICE_MARK = "￥";
    public static final String KEYWORD = "keyword";
    public static final String URL = "url";
    public static final String DATA = "data";
    public static final String LIST = "list";
    public static final String TYPE = "type";
    public static final String HTML = "html";
    public static final String STATE = "state";
    public static final String ORDER = "order";
    public static final String OBJECTID = "objectId";
    public static final String SHOP = "shop";
    public static final String VIDEO = "video";
    public static final String BJ_COLOR = "bj_color";
    public static final String WX_APPID = "wx046c4024ed2eca48";
    public static final String WX_APPSECRET = "5e6472cded17a68572b1d0775eddb890";
    public static final String TENCENT_APP_ID = "1109011627";
    /**
     * 订单状态
     * {"全部","待付款","待使用","待评价","退款/售后"}
     * 0: 待付款/订单已创建  【待付款】
     * 1：付款成功，等待商家接单
     * 2: 商家已接单-等待菜品制作完成-骑手送货-确认收货 【待使用】
     * 3：商家拒绝/取消接单（此情况商家由于特殊原因）
     * 4：顾客确认收货/待评价            【待评价】
     * 5：交易完成/订单评价完成
     * 6：申请退款/售后   【退款/售后】
     * 7：退款成功
     * 8：退款失败
     * 9: 取消订单
     *
     * 注：
     *  1.因为没有骑手端，故需要顾客自己确认收货。
     *  2.美团的方案：是骑手送到货之后骑手自己进行确认收货 vs 在骑手送到货时顾客也可以在APP上进行确认收货
     *  3.本APP没有骑手端故采用顾客确认收货方案，若有需要可自行在此基础上开发骑手端送餐逻辑
     */
    public static final int ORDER_STATE_WAIT_PAY = 0;
    public static final int ORDER_STATE_PAY_SUCCESS = 1;
    public static final int ORDER_STATE_SHOP_HAVE_RECEIPT = 2;
    public static final int ORDER_STATE_SHOP_REFUSE_RECEIPT = 3;
    public static final int ORDER_STATE_WAIT_COMMENT = 4;
    public static final int ORDER_STATE_COMPLETE = 5;
    public static final int ORDER_STATE_APPLY_REFUND = 6;
    public static final int ORDER_STATE_REFUND_SUCCESS = 7;
    public static final int ORDER_STATE_REFUND_FAIL = 8;
    public static final int ORDER_STATE_USER_CANCEL = 9;
}
