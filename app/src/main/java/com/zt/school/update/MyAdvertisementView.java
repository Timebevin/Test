package com.zt.school.update;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.zt.school.R;
import com.zt.school.common.CommonAdapter;
import com.zt.school.common.CommonViewHolder;
import com.zt.school.utils.ToasUtils;
import com.zt.school.utils.UIUtils;

import java.util.List;

/**
 * Created by 九七
 */

public class MyAdvertisementView extends Dialog {
    private Context context;
    public MyAdvertisementView(@NonNull Context context) {
        super(context, R.style.LocatonDialogStyle);
        this.context = context;
        setContentView(R.layout.dialog_view);
        setCancelable(false);
    }
    public void showDialog(boolean forceUpdate, String title, List<String> msgs, final String apkUrl) {
        Window window = getWindow();
        //设置弹窗动画
        window.setWindowAnimations(R.style.style_dialog);
        //设置Dialog背景色(透明)
        window.setBackgroundDrawableResource(R.color.transparent);
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置弹窗位置
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        ListView listview = findViewById(R.id.listview);
        listview.setAdapter(new ListAdapter(context,msgs,R.layout.item_update_list));
        findViewById(R.id.id_ignore_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        if(forceUpdate){
            findViewById(R.id.id_ignore_tv).setVisibility(View.GONE);
        }
        findViewById(R.id.id_update_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DownLoadService.class);
                intent.putExtra("apkUrl", apkUrl);
                UIUtils.getContext().startService(intent);
                ToasUtils.showToastMessage("已加入后台更新中");
                dismiss();
            }
        });
        show();
    }
    class ListAdapter extends CommonAdapter<String> {
        public ListAdapter(Context context, List<String> datas, int layoutId) {
            super(context, datas, layoutId);
        }
        @Override
        public void convert(CommonViewHolder holder, String s) {
            holder.setTvText(R.id.id_tv, s);
        }
    }
}
