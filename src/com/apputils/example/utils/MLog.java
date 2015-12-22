package com.apputils.example.utils;

import com.apputils.example.http.MHttpUtils;

import android.util.Log;

public class MLog {
	public static String TAG_UTILS = "com.xpputils.utils";
	public static String TAG_DEFAULT = "com.xpputils.default";
	public static String TAG_HTTP = "com.xpputils.http";
	public static String TAG_ABS = "com.xpputils.abs";
	public static String TAG_IMG = "com.xpputils.img";
	public static String TAG_THIRD ="com.xpputils.third";

	public static void V(String tag, String msg) {
		if (MHttpUtils.INITCONFIG.getLog()) {
			Log.v(tag, msg);
		}
	}

	public static void V(String msg) {
		if (MHttpUtils.INITCONFIG.getLog()) {
			Log.v(TAG_DEFAULT, msg);
		}
	}

	public static void D(String tag, String msg) {
		if (MHttpUtils.INITCONFIG.getLog()) {
			Log.d(tag, msg);
		}
	}

	public static void D(String msg) {
		if (MHttpUtils.INITCONFIG.getLog()) {
			Log.d(TAG_DEFAULT, msg);
		}
	}

	public static void I(String tag, String msg) {
		if (MHttpUtils.INITCONFIG.getLog()) {
			Log.i(tag, msg);
		}
	}

	public static void I(String msg) {
		if (MHttpUtils.INITCONFIG.getLog()) {
			Log.i(TAG_DEFAULT, msg);
		}
	}

	public static void W(String tag, String msg) {
		if (MHttpUtils.INITCONFIG.getLog()) {
			Log.w(tag, msg);
		}
	}

	public static void W(String msg) {
		if (MHttpUtils.INITCONFIG.getLog()) {
			Log.w(TAG_DEFAULT, msg);
		}
	}

	public static void E(String tag, String msg) {
		if (MHttpUtils.INITCONFIG.getLog()) {
			Log.e(tag, msg);
		}
	}

	public static void E(String msg) {
		if (MHttpUtils.INITCONFIG.getLog()) {
			Log.e(TAG_DEFAULT, msg);
		}
	}

	public static void p(Exception e) {
		if (MHttpUtils.INITCONFIG.getLog()) {
			e.printStackTrace();
		}
	}
}
