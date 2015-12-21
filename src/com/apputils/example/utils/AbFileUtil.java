package com.apputils.example.utils;

import java.io.File;

import javax.security.auth.callback.Callback;

import com.apputils.example.http.MHttpUtils;
import com.apputils.example.utils.common.ApkInfo;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.R;
import android.content.Context;
import android.os.Environment;

/**
 * 文件操作类
 * 
 *
 */
public class AbFileUtil {

	/** 默认下载文件地址. */
	private static String downPathRootDir = File.separator + "download" + File.separator;

	/** 默认下载图片文件地址. */
	private static String downPathImageDir = downPathRootDir + "cache_images" + File.separator;

	/** 默认下载文件地址. */
	public static String downPathFileDir = downPathRootDir + "cache_files" + File.separator;
	/** 默认下载新apk的地址 */
	public static String downNewApkDir = downPathRootDir + "new_apk" + File.separator;

	/** MB 单位B. */
	private static int MB = 1024 * 1024;

	/** 设置好的图片存储目录. */
	private static String imageDownFullDir = null;

	/** 设置好的文件存储目录. */
	private static String fileDownFullDir = null;

	/** 剩余空间大于200M才使用缓存. */
	private static int freeSdSpaceNeededToCache = 200 * MB;

	/**
	 * 以“GB/MB/KB/B”形式展示文件大小
	 */
	public static String getFileSize(Object obj) {
		if (obj == null) {
			return "";
		}
		long sized = Long.parseLong(obj.toString());
		double size = sized;
		if (size > 1024) {
			size = size / 1024;
			if (size > 1024) {
				size = size / 1024;
				if (size > 1024) {
					size = size / 1024d;
					return Dou2Str(size, 2) + " GB";
				} else {
					return Dou2Str(size, 2) + " MB";
				}
			} else {
				return Dou2Str(size, 0) + " KB";
			}
		} else {
			return Dou2Str(size, 0) + " B";
		}
	}

	public static String Dou2Str(double d, int count) {
		double dou = Math.abs(d);
		int rate = 1;
		for (int i = 0; i < count; i++) {
			rate *= 10;
		}
		dou = Math.round(dou * rate);
		dou = dou / rate;
		if (dou - Math.round(dou) == 0) {
			return (d < 0 ? "-" : "") + String.valueOf((int) dou);
		}
		return (d < 0 ? "-" : "") + String.valueOf(dou);
	}

}
