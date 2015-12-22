package com.apputils.example.utils.common;

public class ShareMode {
	/**
	 * 分享的标题
	 */
	public String title;
	/**
	 * 分享的摘要
	 */
	public String content;
	/**
	 * 分享点击跳转的链接
	 */
	public String targetUrl;
	/**
	 * 分享的图片的网络地址
	 */
	public String imgUrl;
	/**
	 * 分享的图片的本地地址
	 */
	public String localImgUrl;

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

	@Override
	public String toString() {
		return "ShareMode [title=" + title + ", content=" + content + ", targetUrl=" + targetUrl + ", imgUrl=" + imgUrl
				+ ", localImgUrl=" + localImgUrl + "]";
	}
	
}
