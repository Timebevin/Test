package com.zt.school.utils;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.zt.school.bean.MyLocationBean;
import com.zt.school.confige.MySharePreference;
import java.util.ArrayList;
import java.util.List;
/**
 * 定位工具
 */
public class GaoDeMapUtils implements AMapLocationListener {
    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    public GaoDeMapUtils(){
        mlocationClient = new AMapLocationClient(UIUtils.getContext());
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(1000*60);//1min刷新一次定位
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                double lat = amapLocation.getLatitude();//获取纬度
                double lng = amapLocation.getLongitude();//获取经度
                String address = amapLocation.getAddress();
                String country = amapLocation.getCountry();//国家信息
                String province = amapLocation.getProvince();//省信息
                String city = amapLocation.getCity();//城市信息
                String district = amapLocation.getDistrict();//城区信息
                String street = amapLocation.getStreet();//街道信息

                String adCode = amapLocation.getAdCode();

                MyLocationBean myLocationBean = new MyLocationBean(String.valueOf(lng), String.valueOf(lat), adCode, address, province, city, district, street);
                MySharePreference.saveCurrentLocationInfo(myLocationBean);

                Log.v("TAG", " lat:"+lat+"  lng:"+lng);
                Log.v("TAG", "详细地址：" + address);
                Log.v("TAG", "国家信息：" + country);
                Log.v("TAG", "省信息：" + province);
                Log.v("TAG", "城市信息：" + city);
                Log.v("TAG", "城区信息：" + district);
                Log.v("TAG", "街道信息：" + street);
                Log.v("TAG", "地区编码adCode ：" + adCode);
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("TAG","定位失败location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }
    public void destory(){
        mlocationClient.stopLocation();
        mlocationClient.onDestroy();
    }
    /**
     * app内打开高德
     * http://lbs.amap.com/api/amap-mobile/guide/android/route
     * http://lbs.amap.com/api/uri-api/guide/travel/route
     */
    public static void openGaoDeMap(Context context, String sLat, String sLng, String sName, String eLat, String eLng, String eName) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        String url;
        if(isAppInstalled(context,"com.autonavi.minimap")){
            //原生app打开
            //t = 0（驾车）= 1（公交）= 2（步行）= 3（骑行）= 4（火车）= 5（长途客车）
            url = "amapuri://route/plan/?sid=BGVIS1&slat=" + sLat + "&slon=" + sLng + "&sname=" + sName + "&did=BGVIS2&dlat="+eLat+"&dlon="+eLng+"&dname="+eName+"&dev=0&t=2";
        }else {
            //网页打开
            //驾车：mode=car,
            //公交:：mode=bus；
            //步行：mode=walk；
            //骑行：mode=ride；
            url = "http://uri.amap.com/navigation?from="+sLng+","+sLat+","+sName+"&to="+eLng+","+eLat+","+eName+"&mode=walk&policy=1&coordinate=gaode&callnative=0";
        }
        Uri uri = Uri.parse(url);
        intent.setData(uri);
        context.startActivity(intent);
    }
    /**
     * app内打开百度地图导航功能(默认坐标点是高德地图，需要转换)
     */
    public static void openBaiduMap(Context context, String sLat, String sLng, String endLat, String endLng, String endName){
        LatLng lag = new LatLng(Double.valueOf(endLat), Double.valueOf(endLng));
        LatLng latLng = GCJ02ToBD09(lag);
        if(isAppInstalled(context,"com.baidu.BaiduMap")){
            //原生app打开
            openBaiduNavi(context,sLat,sLng,endLat,endLng);
        }else{
            //网页打开
            String url = "http://api.map.baidu.com/marker?location=" + latLng.latitude + "," + latLng.longitude + "&title=" + endName + "&content=" + endName + "&output=html";
            Uri webpage = Uri.parse(url);
            Intent webIntent = new Intent(Intent.ACTION_VIEW,webpage);
            context.startActivity(webIntent);
        }
    }
    /**
     * 打开百度地图导航客户端
     * intent = Intent.getIntent("baidumap://map/direction?origin=39.98871,116.43234&destination=40.057406655722,116.2964407172&coord_type=bd09ll&mode=walking&src=andr.baidu.openAPIdemo")
     */
    private static void openBaiduNavi(Context context, String sLat, String sLng, String lat, String lng){
        LatLng sLag = new LatLng(Double.valueOf(sLat), Double.valueOf(sLng));
        LatLng sLatLng = GCJ02ToBD09(sLag);
        LatLng eLag = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
        LatLng eLatLng = GCJ02ToBD09(eLag);
        String url = "baidumap://map/direction?origin="+sLatLng.latitude+","+sLatLng.longitude+"&destination="+eLatLng.latitude+","+eLatLng.longitude+"&coord_type=bd09ll&mode=walking&src=andr.baidu.openAPIdemo";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setPackage("com.baidu.BaiduMap");
        context.startActivity(intent);
    }

    /**
     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换
     * 即谷歌、高德 转 百度
     * https://blog.csdn.net/qq_35605213/article/details/80596561
     */
    public static LatLng GCJ02ToBD09(LatLng latLng) {
        double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
        double z = Math.sqrt(latLng.longitude * latLng.longitude + latLng.latitude * latLng.latitude) + 0.00002 * Math.sin(latLng.latitude * x_pi);
        double theta = Math.atan2(latLng.latitude, latLng.longitude) + 0.000003 * Math.cos(latLng.longitude * x_pi);
        double bd_lat = z * Math.sin(theta) + 0.006;
        double bd_lng = z * Math.cos(theta) + 0.0065;
        return new LatLng(bd_lat, bd_lng);
    }

    /**
     * 判断是否安装某一软件
     * 高德地图： "com.autonavi.minimap"
     * 百度地图："com.baidu.BaiduMap"
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);
        //存储所有已安装程序的包名
        List<String> pName = new ArrayList<>();
        //从info中将报名字逐一取出
        if (pInfo != null) {
            for (int i = 0; i < pInfo.size(); i++) {
                String pn = pInfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);
    }
}
