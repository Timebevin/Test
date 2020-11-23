package com.zt.school.bmob;

import cn.bmob.v3.BmobUser;
/**
 * 普通用户
 */
public class UserBean extends BmobUser {
    public String logo;
    public String motto;
    public String nickname;
    public String plat_type;//第三方登陆类型 wechat qq
    public String openid;//第三方登陆的openid
    public String three_nickname;//第三方登陆的昵称
    public UserBean(){}
}
