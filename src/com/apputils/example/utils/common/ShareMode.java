package com.apputils.example.utils.common;

import android.graphics.Bitmap;

public class ShareMode {
	/**
	 * sina必需要视频的图片，但实际不显示大小要小于15k
	 */
	public Bitmap thumbImage;
	/**
	 * 分享的描述，用在sina
	 */
	public String des;
	/**
	 * 分享的标题,用在sina,qq
	 */
	public String title;
	/**
	 * 分享的摘要,用在sina,qq
	 */
	public String content;
	/**
	 * 分享点击跳转的链接,用在sina,qq
	 */
	public String targetUrl;
	/**
	 * 分享的图片的网络地址,用在qq
	 */
	public String imgUrl;
	/**
	 * 分享的图片的本地地址,用在qq,如果有本地路径就显示本地路径的图片，反之显示网络路径的图片，两图片至少传一个
	 */
	public String localImgUrl;
	
	public Bitmap getThumbImage() {
		return thumbImage;
	}

	public void setThumbImage(Bitmap thumbImage) {
		this.thumbImage = thumbImage;
	}
	
	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getLocalImgUrl() {
		return localImgUrl;
	}

	public void setLocalImgUrl(String localImgUrl) {
		this.localImgUrl = localImgUrl;
	}
}
