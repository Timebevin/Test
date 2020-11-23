package com.zt.school.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.zt.school.R;
import com.zt.school.activity.VideoDetailActivity;
import com.zt.school.adapter.VideoListAdapter;
import com.zt.school.base.BaseFragment;
import com.zt.school.bmob.VideoBean;
import com.zt.school.confige.Constant;
import com.zt.school.utils.NetworkUtil;
import com.zt.school.utils.ToasUtils;
import com.zt.school.utils.UIUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class FragmentVideo extends BaseFragment {
    @BindView(R.id.top_bar_view)
    LinearLayout topBarView;
    @BindView(R.id.listview)
    ListView customListView;
    @BindView(R.id.id_base_smartrefreshlayout)
    SmartRefreshLayout smartRefreshLayout;

    private VideoListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }
    private void init(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            topBarView.setPadding(0, UIUtils.getStateBarHeight(),0,0);
        }
        listener();
    }
    private void listener(){
        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                smartRefreshLayout.finishLoadMore();
            }
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                getBmobVideos();
                smartRefreshLayout.finishRefresh();
            }
        });
        smartRefreshLayout.autoRefresh();
        smartRefreshLayout.setEnableLoadMore(false);
    }
    private void getBmobVideos(){
        BmobQuery<VideoBean> query = new BmobQuery<>();
        query.order("-createdAt");
        query.addWhereEqualTo("isShow", true);
        query.findObjects(new FindListener<VideoBean>() {
            @Override
            public void done(List<VideoBean> list, BmobException e) {
                if (e==null){
                    if (list!=null && list.size()>0){
                        if(adapter==null){
                            customListView.setAdapter(adapter = new VideoListAdapter(getActivity(),list,R.layout.item_video_item));
                            customListView.setOnScrollListener(new AbsListView.OnScrollListener(){
                                @Override
                                public void onScrollStateChanged(AbsListView absListView, int scrollState){
                                }
                                @Override
                                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
                                    //item不在屏幕可见范围内则暂停视频
                                    //JzvdStd.onScrollReleaseAllVideos(view,firstVisibleItem,visibleItemCount,totalItemCount);
                                }
                            });
                            customListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    VideoBean video = adapter.getItem(position);
                                    Intent intent = new Intent(getActivity(), VideoDetailActivity.class);
                                    intent.putExtra(Constant.VIDEO, video);
                                    startActivity(intent);
                                }
                            });
                        }else {
                            adapter.refreshData(list);
                        }
                    }else {
                        ToasUtils.showToastMessage("暂未上传视频。。登陆bmob后台添加");
                    }
                }else {
                    if (!NetworkUtil.checkedNetWork(getActivity())){
                       ToasUtils.showToastMessage("网络连接异常，请连接成功后刷新重试");
                    }else {
                        ToasUtils.showToastMessage("暂未上传视频。。登陆bmob后台添加");
                    }
                }
            }
        });
    }
}
