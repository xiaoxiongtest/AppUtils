package com.apputils.example.utils.common;

import java.io.Serializable;

public class LocationItemInfo implements Serializable{
	public String title;
	public String addr;
	public double latitude;
	public double lontitude;
	public LocationItemInfo(String title, String addr, double latitude, double lontitude) {
		super();
		this.title = title;
		this.addr = addr;
		this.latitude = latitude;
		this.lontitude = lontitude;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
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
	@Override
	public String toString() {
		return "LocationItemInfo [title=" + title + ", addr=" + addr + ", latitude=" + latitude + ", lontitude="
				+ lontitude + "]";
	}
	
}
