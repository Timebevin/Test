package com.zt.school.bmob;

import cn.bmob.v3.BmobObject;

/**
 * Author: 九七
    版本更新类
 */

public class UpdateBean extends BmobObject{
    public int versionCode;//版本号
    public boolean forceUpdate;//是否强制更新
    public String apkUrl;//apk下载链接
    public String title;//更新标题
    public String updateMsg;//更新内容
}
