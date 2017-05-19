package com.example.liyb.network;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

//以下为baidu地图类
//以下为用到的android类
/**
 * Created by liyb on 2017/5/19.
 */


public class MapActivity extends AppCompatActivity {
    //声明变量：
    private static final String strKey = "y5nD401rEXZAFUvfuV0ky7A1GneOu8Hm";//此处是百度key，记得换成自己的key
    MapView mMapView = null;
    /**
     * 定位SDK核心类
     */
    private LocationClient locationClient;
    /**
     * 定位监听
     */
    public MyLocationListener myListener = new MyLocationListener();
    /**
     * 百度地图控件
     */
   // private MapView mapView;
    /**
     * 百度地图对象
     */
    public LocationClient mLocationClient = null;
   // public BDLocationListener myListener = new MyLocationListener();
    public void onCreate() {
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );
        //注册监听函数
    }

    private BaiduMap baiduMap;

    boolean isFirstLoc = true; // 是否首次定位

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);
        mMapView = (MapView) findViewById(R.id.bmapView);
        //获取百度地图对象
        baiduMap = mMapView.getMap();
        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);

        baiduMap.setMyLocationEnabled(true);
        /**
         * 定位初始化
         */
        //声明定位SDK核心类
        locationClient = new LocationClient(this);
        //注册监听
        locationClient.registerLocationListener(myListener);
        //定位配置信息
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);//定位请求时间间隔
        locationClient.setLocOption(option);
        //开启定位
        locationClient.start();

    }
    /**
     * 定位SDK监听函数
     */
    public class MyLocationListener implements BDLocationListener {
        public void onConnectHotSpotMessage(String var1, int var2){
        }
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        // 退出时销毁定位
        locationClient.stop();
        // 关闭定位图层
        baiduMap.setMyLocationEnabled(false);
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
}


