package com.apputils.example.baidu.listener;

import com.apputils.example.utils.common.LocationInfo;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 实现实时位置回调监听
 */
public class MyLocationListener implements BDLocationListener {
	private Context mContext;

	public MyLocationListener(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		LocationInfo info = new LocationInfo();
		info.str_time = location.getTime();
		info.errorcode = location.getLocType();
		info.latitude = location.getLatitude();
		info.lontitude = location.getLongitude();
		info.radius = location.getRadius();

		if (info.errorcode == BDLocation.TypeGpsLocation) {// GPS定位结果
			info.speed = location.getSpeed();
			info.satellite = location.getSatelliteNumber();
			info.height = location.getAltitude();
			info.direction = location.getDirection();
			info.addr = location.getAddrStr();
			info.describe = "gps定位成功";
		} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
			info.addr = location.getAddrStr();
			// 运营商信息
			info.operationers = location.getOperators();
			info.describe = "网络定位成功";
		} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
			info.describe = "离线定位成功，离线定位结果也是有效的";
		} else if (location.getLocType() == BDLocation.TypeServerError) {
			info.describe = "服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因";
		} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
			info.describe = "网络不同导致定位失败，请检查网络是否通畅";
		} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
			info.describe = "无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机";
		}
		Intent intent = new Intent();
		intent.setAction("com.apputils.baidumap");
		Bundle mBundle = new Bundle();
		mBundle.putSerializable("info", info);
		intent.putExtras(mBundle);
		mContext.sendBroadcast(intent);
	}
}
