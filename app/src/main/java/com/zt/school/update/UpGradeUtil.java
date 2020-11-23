package com.zt.school.update;

import android.app.Activity;

import com.kongzue.dialog.v2.WaitDialog;
import com.zt.school.bmob.UpdateBean;
import com.zt.school.utils.AppInfoManagerUtils;
import com.zt.school.utils.ToasUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by 九七
 */

public class UpGradeUtil {
    private Activity activity;
    private boolean isClickCheckUpdate;
    public UpGradeUtil(Activity context){
        this.activity = context;
    }
    public void checkUpdate(final boolean isClickCheckUpdate){
        this.isClickCheckUpdate = isClickCheckUpdate;
        BmobQuery<UpdateBean> query = new BmobQuery<>();
        query.order("-versionCode");//以版本号降序查询
        query.findObjects(new FindListener<UpdateBean>(){
            @Override
            public void done(List<UpdateBean> list, BmobException e) {
                if (e==null && list!=null && list.size()>0){
                    //以集合第一条数据作为更新
                    UpdateBean bean = list.get(0);
                    List<String> mgs = new ArrayList<>();
                    mgs.add(bean.updateMsg);
                    update(bean.forceUpdate,bean.versionCode,bean.apkUrl,bean.title,mgs);
                }else {
                    if (isClickCheckUpdate)WaitDialog.dismiss();
                }
            }
        });
    }
    private void update(boolean forceUpdate, int newVersionCode, String apkUrl, String title, List<String> msgs){
        AppInfoManagerUtils managerUtils = new AppInfoManagerUtils(activity);
        boolean isUpdate = managerUtils.isUpGrade(newVersionCode);
        if(isUpdate){
            MyAdvertisementView myAdvertisementView = new MyAdvertisementView(activity);
            myAdvertisementView.showDialog(forceUpdate,title,msgs,apkUrl);
            if(isClickCheckUpdate) WaitDialog.dismiss();
        }else {
            if(isClickCheckUpdate){
                ToasUtils.showToastMessage("已经是最新版本");
                WaitDialog.dismiss();
            }
        }
    }
}
