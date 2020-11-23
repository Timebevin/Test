package com.zt.school.bmob;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Author: 九七
 * 收货地址
 */

public class AddressBean extends BmobObject implements Serializable {
    public String userId;
    public String name,mobile,province,city,area, detailAddress;

}
