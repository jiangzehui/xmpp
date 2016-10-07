package com.yyquan.jzh.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;


import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.yyquan.jzh.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzh on 2015/10/20.
 */
public class LocationActivity extends Activity implements LocationSource,
        AMapLocationListener, AMap.OnCameraChangeListener, PoiSearch.OnPoiSearchListener, AdapterView.OnItemClickListener {

    private MapView mapView;
    LinearLayout ll_back;
    private AMap aMap;
    private LocationManagerProxy mAMapLocationManager;
    private OnLocationChangedListener mListener;
    private Marker marker;// 定位雷达小图标
    LatLonPoint lp;
    private int currentPage = 0;
    PoiSearch.Query query;
    private PoiSearch poiSearch;
    List<PoiItem> list = new ArrayList<PoiItem>();
    private ListView mlist;
    MyAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_location);
        mapView = (MapView) findViewById(R.id.map_route);
        mlist = (ListView) findViewById(R.id.location_listView);
        ll_back= (LinearLayout) findViewById(R.id.location_layout_back);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mlist.setOnItemClickListener(this);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setOnCameraChangeListener(this);
            setUpMap();
        }


    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
//        ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
//        giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point1));
//        giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point2));
//        giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point3));
//        giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point4));
//        giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point5));
//        giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point6));
//        marker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
//                .icons(giflist).period(50));
        // 自定义系统定位小蓝点
//        MyLocationStyle myLocationStyle = new MyLocationStyle();
////        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
////                .fromResource(R.mipmap.location));// 设置小蓝点的图标
//        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
//        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
//        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
//        myLocationStyle.strokeWidth(0.1f);// 设置圆形的边框粗细
//        aMap.setMyLocationStyle(myLocationStyle);
        // aMap.setMyLocationRotateAngle(180);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setScaleControlsEnabled(true);
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        //设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 此方法已经废弃
     */
    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation aLocation) {
        if (mListener != null && aLocation != null && aLocation.getAMapException().getErrorCode() == 0) {
            mListener.onLocationChanged(aLocation);// 显示系统小蓝点
            marker.setPosition(new LatLng(aLocation.getLatitude(), aLocation
                    .getLongitude()));// 定位雷达小图标
            float bearing = aMap.getCameraPosition().bearing;
            aMap.setMyLocationRotateAngle(bearing);// 设置小蓝点旋转角度
            aMap.getMinZoomLevel();
            double a = aLocation.getLatitude();// 维度
            double b = aLocation.getLongitude();// 精度

            lp = new LatLonPoint(a, b);

            doSearchQuery();
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(this);
            /*
             * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
            mAMapLocationManager.requestLocationData(
                    LocationProviderProxy.AMapNetwork, 60 * 1000, 10, this);
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destroy();
            ;
        }
        mAMapLocationManager = null;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        LatLng l = cameraPosition.target;
        double a = l.latitude;
        double b = l.longitude;
        CircleOptions co = new CircleOptions();
        co.center(l);
        co.radius(2000);
        co.strokeColor(Color.WHITE).fillColor(getResources().getColor(R.color.location_cri_color)).strokeWidth(3);
        aMap.clear();
        aMap.addCircle(co);
        lp = new LatLonPoint(a, b);
        doSearchQuery();
    }

    protected void doSearchQuery() {
        currentPage = 0;
        query = new PoiSearch.Query("", "", "");//
        query.setPageSize(50);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查询页码

        poiSearch = new PoiSearch(this, query);// 初始化poiSearch对象
        poiSearch.setBound(new PoiSearch.SearchBound(lp, 20000));
        poiSearch.setOnPoiSearchListener(this);// 设置回调数据的监听器
        poiSearch.searchPOIAsyn();// 开始搜索
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {

        list = poiResult.getPois();

        if(adapter==null){
            adapter= new MyAdapter(list);
            mlist.setAdapter(adapter);
        }else{
            adapter.setList(list);
        }


       // Log.i("xxxxxxxxxx", list.toString());

        //  mlist.setAdapter(new MyAdapter(list));
    }

    @Override
    public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LatLonPoint ll = list.get(position).getLatLonPoint();
        double a = ll.getLatitude();
        double b = ll.getLongitude();
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(a, b)));

    }

    class MyAdapter extends BaseAdapter {
        private List<PoiItem> list;

        public MyAdapter(List<PoiItem> list) {

            this.list = list;
        }

        public void setList(List<PoiItem> lists){
            list=lists;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder view = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(LocationActivity.this).inflate(R.layout.location_listview_item, null);
                view = new ViewHolder();
                view.text = (TextView) convertView.findViewById(R.id.item_text);
                view.text1 = (TextView) convertView
                        .findViewById(R.id.item_text1);
                view.sure = (Button) convertView.findViewById(R.id.sure);

                convertView.setTag(view);

            } else {
                view = (ViewHolder) convertView.getTag();
            }
            final PoiItem po = list.get(position);
            view.text.setText(po.toString());
            view.text1.setText(po.getProvinceName()+po.getCityName()+po.getSnippet());
            view.sure.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent i = new Intent();
                    i.putExtra("location", po.getProvinceName() +po.getCityName()+po.getSnippet()+po.toString());
                    setResult(99, i);
                    finish();
                }
            });

            return convertView;
        }

        class ViewHolder {
            TextView text, text1;
            Button sure;
        }
    }
}
