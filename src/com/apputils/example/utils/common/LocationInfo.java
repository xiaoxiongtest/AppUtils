package com.apputils.example.utils.common;

import java.io.Serializable;

public class LocationInfo implements Serializable{
	public String str_time;
	public int errorcode;
	public double latitude;
	public double lontitude;
	public float radius;
	public float speed;// 单位：公里每小时
	public int satellite;
	public double height;
	public float direction;// 单位：米
	public String addr;
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public int operationers;
	public String describe;//type == BDLocation.TypeGpsLocation gps定位成功
							//BDLocation.TypeNetWorkLocation 网络定位成功
							//BDLocation.TypeOffLineLocation 离线定位成功，离线定位结果也是有效的
							//BDLocation.TypeServerError 服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因
							//BDLocation.TypeNetWorkException 网络不同导致定位失败，请检查网络是否通畅
							//BDLocation.TypeCriteriaException 无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机
	public String getStr_time() {
		return str_time;
	}
	public void setStr_time(String str_time) {
		this.str_time = str_time;
	}
	public int getErrorcode() {
		return errorcode;
	}
	public void setErrorcode(int errorcode) {
		this.errorcode = errorcode;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLontitude() {
		return lontitude;
	}
	public void setLontitude(double lontitude) {
		this.lontitude = lontitude;
	}
	public float getRadius() {
		return radius;
	}
	public void setRadius(float radius) {
		this.radius = radius;
	}
	public float getSpeed() {
		return speed;
	}
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	public int getSatellite() {
		return satellite;
	}
	public void setSatellite(int satellite) {
		this.satellite = satellite;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	public float getDirection() {
		return direction;
	}
	public void setDirection(float direction) {
		this.direction = direction;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public int getOperationers() {
		return operationers;
	}
	public void setOperationers(int operationers) {
		this.operationers = operationers;
	}
	@Override
	public String toString() {
		return "LocationInfo [str_time=" + str_time + ", errorcode=" + errorcode + ", latitude=" + latitude
				+ ", lontitude=" + lontitude + ", radius=" + radius + ", speed=" + speed + ", satellite=" + satellite
				+ ", height=" + height + ", direction=" + direction + ", addr=" + addr + ", operationers="
				+ operationers + ", describe=" + describe + "]";
	}
	
}
