package com.yyquan.jzh.location;

import android.content.Context;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

/**
 * Created by jzh on 2015/10/6.
 */
public class Location {

    private LocationManagerProxy mLocationManagerProxy;//高德定位
    public String location;
    public String city;
    Context context;

    public Location(Context context) {
        this.context = context;
        mLocationManagerProxy = LocationManagerProxy.getInstance(context);
        //此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        //注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
        //在定位结束后，在合适的生命周期调用destroy()方法
        //其中如果间隔时间为-1，则定位只定一次
        mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 5 * 1000, 15, all);
        mLocationManagerProxy.setGpsEnable(false);

    }


    AMapLocationListener all = new AMapLocationListener() {

        @Override
        public void onLocationChanged(android.location.Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null && amapLocation.getAMapException().getErrorCode() == 0) {
                //获取位置信息
                location = amapLocation.getAddress();
                city = amapLocation.getProvince() + "·" + amapLocation.getCity() + amapLocation.getDistrict();

            }
        }
    };

    /**
     * 停止定位
     */
    public void stopLocation() {
        if (mLocationManagerProxy != null) {
            mLocationManagerProxy.removeUpdates(all);
            mLocationManagerProxy.destory();
        }
        mLocationManagerProxy = null;
    }


}
