package com.zt.school.activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.zt.school.R;
import com.zt.school.base.BaseActivity;
import com.zt.school.bean.SelectAddressBean;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 商家经纬度位置选择获取类
 * Android高德地图设置中心点图标，通过逆地理编码实时获取中心点的经纬度和详细位置
 * https://blog.csdn.net/yu19931202/article/details/72765414
 */
public class ShopMapAddressSelectActivity extends BaseActivity implements AMap.OnCameraChangeListener, AMap.OnMapLoadedListener, GeocodeSearch.OnGeocodeSearchListener {

    @BindView(R.id.map)
    MapView mMapView;
    @BindView(R.id.tv)
    TextView selectAddressTv;

    private LatLng target;
    private AMap aMap;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_shop_map_address_select;
    }

    @Override
    protected boolean setStatusBarFontColorIsBlack() {
        return false;
    }
    @Override
    protected int setStatueBarColor() {
        return R.color.text_center_tab_selected;
    }
    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }
    @Override
    protected void initView(Bundle savedInstanceState) {

        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mMapView.getMap();
            //设置滑动监听
            aMap.setOnCameraChangeListener(this);
            aMap.setOnMapLoadedListener(this);
        }

        //实现定位蓝点：
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//定位一次，且将视角移动到地图中心点。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。

        //精度框
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 自定义精度范围的圆形边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));//圆圈的颜色,设为透明的时候就可以去掉园区区域了
        myLocationStyle.strokeWidth(0);//边框宽度

        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

        //改变地图的缩放级别 3--19
        aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }



    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    /**
     * 第二步就是获取中心点图标的经纬度,下面的代码块就是地图移动后的监听方法
     */
    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        //这个target 就是地图移动过后中心点的经纬度
        target = cameraPosition.target;
        //这个方法是逆地理编码解析出详细位置
        Geo(target);
    }

    /**
     * 第一步，需要在地图中心点设置图标，下面是代码
     */
    @Override
    public void onMapLoaded(){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.setFlat(true);
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.position(new LatLng(0, 0));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_dw)));
        Marker mPositionMark = aMap.addMarker(markerOptions);
        mPositionMark.showInfoWindow();//主动显示indowindow
        mPositionMark.setPositionByPixels(mMapView.getWidth() / 2,mMapView.getHeight() / 2);
    }

    /**
     * 第三步就是逆地理编码获取地图中心点坐标的详细位置
     */
    //先要执行逆地理编码的搜索
    public void Geo(LatLng latlng) {
        GeocodeSearch geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);//和上面一样
        // 第一个参数表示一个Latlng(经纬度)，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(target.latitude,target.longitude), 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    private String selectAddress;
    //在这个方法里面获取到详细位置
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        //如果逆地理编码成功，就获取到中心点的详细位置，并且在TextView中进行显示，就如同一开始的那张图片所示。
        if (i == AMapException.CODE_AMAP_SUCCESS) {
            if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null && regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {
                String addressName = regeocodeResult.getRegeocodeAddress().getFormatAddress();
                selectAddress = addressName;
                String detailAddress = addressName;
                if (target!=null){
                    detailAddress = detailAddress + "\n" + "纬度：" + target.latitude + "\n" + "经度：" + target.longitude;
                }
                selectAddressTv.setText(detailAddress);
            }
        }
    }
    @Override
    public void onGeocodeSearched(GeocodeResult result, int i) {

    }

    @OnClick(R.id.id_tv_qd) void click(View view){
        if (target!=null){
            SelectAddressBean selectAddressBean = new SelectAddressBean();
            selectAddressBean.address = selectAddress;
            selectAddressBean.lat = String.valueOf(target.latitude);
            selectAddressBean.lng = String.valueOf(target.longitude);
            Intent intent = new Intent();
            intent.putExtra("address", selectAddressBean);
            setResult(200, intent);
            finish();
        }
    }

}
