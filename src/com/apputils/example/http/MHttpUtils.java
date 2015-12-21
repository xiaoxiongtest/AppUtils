package com.apputils.example.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.apputils.example.manage.Handles;
import com.apputils.example.mobile.InitConfig;
import com.apputils.example.utils.AbDeviceUtil;
import com.apputils.example.utils.MLog;
import com.apputils.example.utils.common.InitError;
import com.lidroid.xutils.HttpUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

public class MHttpUtils {
	/**
	 * @Fields Frame_inited 是否初始化
	 */
	private static boolean Frame_inited = false;
	/** @Fields HANDLES : 控制层 */
	public static Handles HANDLES = new Handles();
	private static Context CONTEXT;
	/** @Fields INITCONFIG : 配置 */
	public static InitConfig INITCONFIG;
	/** @Fields AUTOPARMS : 默认传参 */
	public static HashMap<String, String> AUTOPARMS = new HashMap<String, String>();
	public static SharedPreferences initParams;
	public static HttpUtils instance;
	public static String DEVICEID = "";

	// 没有网络的提示信息设置
	public static InitError NOT_NET;

	public static void setNotNetInfo(InitError error) {
		NOT_NET = error;
	}

	public static InitError getNotNetInfo() {
		return NOT_NET;
	}
	

	public static HttpUtils getInstance() {
		if (instance == null) {
			return new HttpUtils(INITCONFIG.mConnectionTimeOut);
		}
		return instance;
	}

	/**
	 * 将框架进行初始化
	 * 
	 * @param context
	 */
	public static void init(Context context) {
		if (!Frame_inited) {
			DEVICEID = AbDeviceUtil.getDeviceid(context);
			CONTEXT = context.getApplicationContext();
			reInit(context);
			initParams = context.getSharedPreferences("initAuParams", Context.MODE_PRIVATE);
		}
		Frame_inited = true;
	}

	private static void reInit(Context context) {
		// 将assets中的配置信息
		INITCONFIG = new InitConfig(context);
		Frame_inited = true;
	}

	public static String getAutoParamStr() {
		if (isEmptyAutoParms()) {
			MLog.D(MLog.TAG_HTTP, "未设置默认传参");
			return null;
		} else {
			String str = "";
			for (String key : AUTOPARMS.keySet()) {
				String value = AUTOPARMS.get(key);
				str = str + (str.length() > 0 ? ("&" + key + "=" + value) : (key + "=" + value));
			}
			MLog.D(MLog.TAG_HTTP, "默认参数：" + str);
			return str;
		}
	}

	/**
	 * 获取默认传参,若AUTOPARMS为空则从SharedPreferences获取
	 * 
	 * @return
	 */
	public static HashMap<String, String> getAutoAddParms() {
		if (!isEmptyAutoParms()) {
			MLog.D("未设置默认传参");
			return null;
		}
		return AUTOPARMS;
	}

	/**
	 * 当前默认传参是否为空
	 */
	public static boolean isEmptyAutoParms() {
		return null == AUTOPARMS || AUTOPARMS.isEmpty();
	}

	/**
	 * 清除默认传参
	 */
	public static void clearAutoAddParms() {
		initParams.edit().clear();
	}

	/**
	 * 设置默认参数
	 */
	public static void setAutoAddParms(HashMap<String, String> autoaddparms) {
		if (autoaddparms == null || autoaddparms.isEmpty())
			return;
		clearAutoAddParms();
		Editor edit = initParams.edit();
		for (String key : autoaddparms.keySet()) {
			edit.putString(key, autoaddparms.get(key));
		}
		AUTOPARMS = autoaddparms;
		edit.commit();
	}

}
