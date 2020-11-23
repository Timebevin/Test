package com.zt.school.activity;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zt.school.R;
import com.zt.school.base.BaseActivity;
import com.zt.school.bmob.ShopBean;
import com.zt.school.confige.Constant;
import com.zt.school.fly.ShakeListener;
import com.zt.school.fly.StellarMap;
import com.zt.school.utils.UIUtils;
import com.zt.school.views.CusPopDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

public class RandomActivity extends BaseActivity {
    @BindView(R.id.framelayout)
    FrameLayout frameLayout;
    @BindView(R.id.id_base_title_tv)
    TextView mTitleTv;
    @Override
    protected int setContentViewId() {
        return R.layout.activity_random;
    }

    @Override
    protected int setStatueBarColor() {
        return R.color.text_center_tab_selected;
    }

    @Override
    protected void initView() {
        mTitleTv.setText(getIntent().getStringExtra(Constant.TITLE));
        BmobQuery<ShopBean> query = new BmobQuery<>();
        query.addWhereEqualTo("isPass", true);
        query.count(ShopBean.class, new CountListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if(e==null){
                    if (integer>0){
                        getAllShops();
                    }
                }
            }
        });
    }
    private void getAllShops(){
        BmobQuery<ShopBean> query = new BmobQuery<>();
        query.addWhereEqualTo("isPass", true);
        query.findObjects(new FindListener<ShopBean>() {
            @Override
            public void done(List<ShopBean> list, BmobException e) {
                if (e==null && list!=null && list.size()>0){
                    random(list);
                }
            }
        });
    }
    //随机打乱list集合
    /**
   * 使用new Random().nextInt(int number);方法 获取0-number之间的数字
   * 再加上从原list里面取出数据之后就删除 保证了不会重复取值
   * 最后数据都转移到了new_list里面
     */
    private void random(List<ShopBean> list){
        List new_list = new ArrayList();
        while(list.size()>0){
            int index = new Random().nextInt(list.size());
            new_list.add(list.get(index));
            list.remove(index);
        }
        updateUi(new_list);
    }
    private void updateUi(List<ShopBean> list){
        final StellarMap stellarMap = new StellarMap(UIUtils.getContext());
        stellarMap.setAdapter(new MyAdapter(list));
        //随机方式 6列9行
        stellarMap.setRegularity(6, 9);
        //设置内边距
        int padd = UIUtils.dp2px(10);
        stellarMap.setInnerPadding(padd,padd,padd,padd);
        //设置默认界面
        stellarMap.setGroup(0, true);//第一组播放动画

        //设置手机摇动是，更换组数据的效果
        final ShakeListener shakeListener = new ShakeListener(UIUtils.getContext());
        shakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
            @Override
            public void onShake() {
                stellarMap.zoomIn();//跳到下一组数据
            }
        });
        frameLayout.addView(stellarMap);
    }
    class MyAdapter implements StellarMap.Adapter {
        private List<ShopBean> data;
        private List<Integer> colorList = new ArrayList<>();
        public MyAdapter(List<ShopBean> list){
            this.data = list;
            colorList.add(R.color.color_red);
            colorList.add(R.color.white);
            colorList.add(R.color.color_green);
            colorList.add(R.color.tint);
        }
        //返回组的个数
        @Override
        public int getGroupCount() {
            return groupCount();
        }
        private int  groupCount(){
            int group;
            if (data.size()<20){ //设置每组返回20条数据
                group = 1;
            }else {
                group = data.size() / 20;
            }
            return group;
        }
        //返回某组的item的个数
        @Override
        public int getCount(int group) {
            int count = data.size() / getGroupCount();
            //将余出来的书添加到最后一个组里
            if(group==getGroupCount()-1){
                count += data.size() % getGroupCount();
            }
            return count;
        }
        //初始化布局
        @Override
        public View getView(int group, int position, View convertView) {
            //因为position每次滑动时都是都0开始，故从集合里获取的数据到是一样，所以要修改
            position += group * getCount(group - 1);

            final ShopBean bean = data.get(position);
            TextView textView = new TextView(UIUtils.getContext());
            textView.setTextColor(Color.BLACK);
            textView.setText(bean.name);

            Random random = new Random();
            //随机大小 15--20
            int size = 15 + random.nextInt(6);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,size);

            //随机颜色 0--255
//            int r = 0 + random.nextInt(256);
//            int g = 0 + random.nextInt(256);
//            int b = 0 + random.nextInt(256);
//            int color = Color.rgb(r, g, b);
//            textView.setTextColor(color);

            int index = new Random().nextInt(colorList.size());
            textView.setTextColor(getResources().getColor(colorList.get(index)));

            textView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    CusPopDialog cusPopDialog = new CusPopDialog(RandomActivity.this);
                    cusPopDialog.showDialog(bean);
                }
            });

            return textView;
        }

        //返回下一组的id
        @Override
        public int getNextGroupOnZoom(int group, boolean isZoomIn) {
            //isZoomIn 上滑false 下滑true
            if(isZoomIn){
                //下滑加载上一组数据
                if(group>0){
                    group--;
                }else {
                    group = getGroupCount() - 1;//最后一组
                }
            }else {
                //上滑加载下一组数据
                group++;
                if(group==getGroupCount()){
                    group = 0;//回到第一组
                }
            }
            return group;
        }
    }
}
