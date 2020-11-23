package com.zt.school.bmob;

import cn.bmob.v3.BmobObject;

/**
 * Author: 九七
 * 用户提交的订单封装对象类
 */

public class OrderBean extends BmobObject {
    public String shopId;
    public String userId;
    public String orderArray;
    public Integer state;//订单状态-明细见Constant类

    public String shopLogo;//商家logo
    public String shopName;//商家名称
    public String userName;//顾客名字
    public String userLogo;//顾客logo
    public String orderNumber;//订单编号
    public String totalPrice;
    public String payType;//支付方式 微信vs支付宝
    public Integer total;
    public String remarks;//备注
    public String addressId;//收货地址id

    public boolean closeOrder;//关闭订单-此字段由商家端操作
}
