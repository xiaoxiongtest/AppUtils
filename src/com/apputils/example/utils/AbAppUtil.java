package com.apputils.example.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.apputils.example.R;
import com.apputils.example.utils.common.AppInfo;
import com.apputils.example.utils.common.NotificationInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.RemoteViews;

public class AbAppUtil {

	/**
	 * 创建桌面快捷方式
	 * 
	 * @param cxt
	 *            Context
	 * @param icon
	 *            快捷方式图标
	 * @param title
	 *            快捷方式标题
	 * @param cls
	 *            要启动的类
	 */
	public void createDeskShortCut(Context cxt, int icon, String title, Class<?> cls) {
		// 创建快捷方式的Intent
		Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		// 不允许重复创建
		shortcutIntent.putExtra("duplicate", false);
		// 需要现实的名称
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
		// 快捷图片
		Parcelable ico = Intent.ShortcutIconResource.fromContext(cxt.getApplicationContext(), icon);
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, ico);
		Intent intent = new Intent(cxt, cls);
		// 下面两个属性是为了当应用程序卸载时桌面上的快捷方式会删除
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		// 点击快捷图片，运行的程序主入口
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		// 发送广播。OK
		cxt.sendBroadcast(shortcutIntent);
	}

	/**
	 * 回到home，后台运行
	 * 
	 * @param context
	 */
	public static void goHome(Context context) {

		Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
		mHomeIntent.addCategory(Intent.CATEGORY_HOME);
		mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		context.startActivity(mHomeIntent);
	}

	/**
	 * 获取应用签名
	 * 
	 * @param context
	 * @param pkgName
	 * @return
	 */
	public static String getSign(Context context, String pkgName) {
		try {
			PackageInfo pis = context.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
			return hexdigest(pis.signatures[0].toByteArray());
		} catch (NameNotFoundException e) {
			MLog.p(e);
			return "";
		}
	}

	/**
	 * 获取公匙
	 * 
	 * @param signature
	 * @return
	 */
	public static String getPublicKey(byte[] signature) {
		try {
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			X509Certificate cert = (X509Certificate) certFactory
					.generateCertificate(new ByteArrayInputStream(signature));

			String publickey = cert.getPublicKey().toString();
			publickey = publickey.substring(publickey.indexOf("modulus: ") + 9,
					publickey.indexOf("\n", publickey.indexOf("modulus:")));
			return publickey;
		} catch (CertificateException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将签名字符串转换成需要的32位签名
	 * 
	 * @param paramArrayOfByte
	 * @return
	 */
	private static String hexdigest(byte[] paramArrayOfByte) {
		final char[] hexDigits = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
		try {
			MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
			localMessageDigest.update(paramArrayOfByte);
			byte[] arrayOfByte = localMessageDigest.digest();
			char[] arrayOfChar = new char[32];
			for (int i = 0, j = 0;; i++, j++) {
				if (i >= 16) {
					return new String(arrayOfChar);
				}
				int k = arrayOfByte[i];
				arrayOfChar[j] = hexDigits[(0xF & k >>> 4)];
				arrayOfChar[++j] = hexDigits[(k & 0xF)];
			}
		} catch (Exception e) {
		}
		return "";
	}

	/**
	 * 获取设备的可用内存大小
	 * 
	 * @param cxt
	 * @return
	 */
	public static int getDeviceUsableMemory(Context cxt) {
		ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// 返回当前系统的可用内存
		return (int) (mi.availMem / (1024 * 1024));
	}

	/**
	 * 清理后台进程与服务
	 * 
	 * @param cxt
	 * @return
	 */
	@SuppressLint("NewApi")
	public static int gc(Context cxt) {
		long i = getDeviceUsableMemory(cxt);
		int count = 0; // 清理掉的进程数
		ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
		// 获取正在运行的service列表
		List<RunningServiceInfo> serviceList = am.getRunningServices(100);
		if (serviceList != null)
			for (RunningServiceInfo service : serviceList) {
				if (service.pid == android.os.Process.myPid())
					continue;
				try {
					android.os.Process.killProcess(service.pid);
					count++;
				} catch (Exception e) {
					e.getStackTrace();
					continue;
				}
			}

		// 获取正在运行的进程列表
		List<RunningAppProcessInfo> processList = am.getRunningAppProcesses();
		if (processList != null)
			for (RunningAppProcessInfo process : processList) {
				// 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了
				// 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着
				if (process.importance > RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
					// pkgList 得到该进程下运行的包名
					String[] pkgList = process.pkgList;
					for (String pkgName : pkgList) {
						MLog.D("======正在杀死包名：" + pkgName);
						try {
							am.killBackgroundProcesses(pkgName);
							count++;
						} catch (Exception e) { // 防止意外发生
							e.getStackTrace();
							continue;
						}
					}
				}
			}
		MLog.D("清理了" + (getDeviceUsableMemory(cxt) - i) + "M内存");
		return count;
	}

	/**
	 * 获取apk相关信息
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 * @throws NameNotFoundException
	 */
	public static AppInfo getApp(Context context, String packageName) throws NameNotFoundException {
		PackageInfo pack = context.getPackageManager().getPackageInfo(packageName, 0);
		AppInfo apk = new AppInfo();
		ApplicationInfo app = pack.applicationInfo;
		apk.setName(app.loadLabel(context.getPackageManager()).toString());
		apk.setPackage(pack.packageName);
		apk.setVersion(pack.versionCode);
		apk.setVersionName(pack.versionName);
		return apk;
	}

	/**
	 * 获取设备已装APK信息
	 * 
	 * @param context
	 * @return
	 */
	public static List<AppInfo> getAppList(Context context) {
		List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
		List<AppInfo> list = new ArrayList<AppInfo>();
		for (PackageInfo packinfo : packages) {
			AppInfo apk = new AppInfo();
			ApplicationInfo app = packinfo.applicationInfo;
			if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				apk.setName(app.loadLabel(context.getPackageManager()).toString());
				apk.setPackage(packinfo.packageName);
				apk.setVersion(packinfo.versionCode);
				apk.setVersionName(packinfo.versionName);
				if (!apk.getPackage().startsWith("com.wjwl.apkfactory")) {
					list.add(apk);
				}
			}
		}
		return list;
	}

	/**
	 * 安装apk
	 * 
	 * @param context
	 * @param path
	 */
	public static void install(Context context, String path) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * 打开apk
	 * 
	 * @param context
	 * @param packag
	 */
	public static void open(Context context, String packag) {
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(packag);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * 删除APK
	 * 
	 * @param context
	 * @param packag
	 */
	public static void deleteApp(Context context, String packag) {
		Uri packageURI = Uri.parse("package:" + packag);
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(uninstallIntent);
	}

	/**
	 * 用来判断服务是否运行.
	 *
	 * @param ctx
	 *            the ctx
	 * @param className
	 *            判断的服务名字 "com.xxx.xx..XXXService"
	 * @return true 在运行 false 不在运行
	 */
	public static boolean isServiceRunning(Context ctx, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> servicesList = activityManager.getRunningServices(Integer.MAX_VALUE);
		Iterator<RunningServiceInfo> l = servicesList.iterator();
		while (l.hasNext()) {
			RunningServiceInfo si = (RunningServiceInfo) l.next();
			if (className.equals(si.service.getClassName())) {
				isRunning = true;
			}
		}
		return isRunning;
	}

	/**
	 * 停止服务.
	 *
	 * @param ctx
	 *            the ctx
	 * @param className
	 *            the class name
	 * @return true, if successful
	 */
	public static boolean stopRunningService(Context ctx, String className) {
		Intent intent_service = null;
		boolean ret = false;
		try {
			intent_service = new Intent(ctx, Class.forName(className));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (intent_service != null) {
			ret = ctx.stopService(intent_service);
		}
		return ret;
	}

	/***
	 * 获取top的Activity的ComponentName
	 * 
	 * @param paramContext
	 * @return
	 */
	public static ComponentName getTopActivityCompomentName(Context paramContext) {
		List<ActivityManager.RunningTaskInfo> localList = null;
		if (paramContext != null) {
			ActivityManager localActivityManager = (ActivityManager) paramContext.getSystemService("activity");
			if (localActivityManager != null) {
				localList = localActivityManager.getRunningTasks(1);

				if ((localList == null) || (localList.size() <= 0)) {
					return null;
				}
			}
		}
		ComponentName localComponentName = localList.get(0).topActivity;
		return localComponentName;
	}

	/***
	 * 查看是否后台
	 * 
	 * @param paramContext
	 * @return
	 */
	public static boolean isAppRunningBackground(Context paramContext) {
		String pkgName = null;
		List<RunningAppProcessInfo> localList = null;
		if (paramContext != null) {
			pkgName = paramContext.getPackageName();
			ActivityManager localActivityManager = (ActivityManager) paramContext.getSystemService("activity");
			if (localActivityManager != null) {
				localList = localActivityManager.getRunningAppProcesses();
				if ((localList == null) || (localList.size() <= 0)) {
					return false;
				}
			}
		}

		for (Iterator<RunningAppProcessInfo> localIterator = localList.iterator(); localIterator.hasNext();) {
			ActivityManager.RunningAppProcessInfo info = localIterator.next();
			if (info.processName.equals(pkgName) && info.importance != 100) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 导入数据库.
	 *
	 * @param context
	 *            the context
	 * @param dbName
	 *            the db name
	 * @param rawRes
	 *            the raw res
	 * @return true, if successful
	 */
	public static boolean importDatabase(Context context, String dbName, int rawRes) {
		int buffer_size = 1024;
		InputStream is = null;
		FileOutputStream fos = null;
		boolean flag = false;

		try {
			String dbPath = "/data/data/" + context.getPackageName() + "/databases/" + dbName;
			File dbfile = new File(dbPath);
			// 判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
			if (!dbfile.exists()) {
				// 欲导入的数据库
				if (!dbfile.getParentFile().exists()) {
					dbfile.getParentFile().mkdirs();
				}
				dbfile.createNewFile();
				is = context.getResources().openRawResource(rawRes);
				fos = new FileOutputStream(dbfile);
				byte[] buffer = new byte[buffer_size];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.flush();
			}
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
				}
			}
		}
		return flag;
	}

	/**
	 * 获取屏幕尺寸与密度.
	 *
	 * @param context
	 *            the context
	 * @return mDisplayMetrics
	 */
	public static DisplayMetrics getDisplayMetrics(Context context) {
		Resources mResources;
		if (context == null) {
			mResources = Resources.getSystem();

		} else {
			mResources = context.getResources();
		}
		DisplayMetrics mDisplayMetrics = mResources.getDisplayMetrics();
		return mDisplayMetrics;
	}

	/**
	 * 打开键盘.
	 *
	 * @param context
	 *            the context
	 */
	public void showSoftInput(Context context) {
		InputMethodManager inputMethodManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 关闭键盘事件.
	 *
	 * @param context
	 *            the context
	 */
	public void closeSoftInput(Context context) {
		InputMethodManager inputMethodManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null && ((Activity) context).getCurrentFocus() != null) {
			inputMethodManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 获取手机指定 UID对应的应程序用通过所有网络方式接收的字节流量总数(包括 wifi)
	 * 
	 * @param context
	 * @return
	 */
	public static long getRxBytes(Context context) {
		int uid = context.getApplicationInfo().uid;
		long rxbytes = 0L;
		try {
			Class clazz = Class.forName("android.net.TrafficStats");
			Method method = clazz.getMethod("getUidRxBytes", new Class[] { Integer.TYPE });
			rxbytes = ((Long) method.invoke(clazz, new Object[] { Integer.valueOf(uid) })).longValue();
		} catch (Exception e) {
			rxbytes = -2L;
		}
		return rxbytes;
	}

	/**
	 * 获取手机指定 UID对应的应用程序通过所有网络方式发送的字节流量总数(包括 wifi)
	 * 
	 * @param context
	 * @return
	 */
	public static long getTxBytes(Context context) {
		int uid = context.getApplicationInfo().uid;
		long txbytes = 0L;
		try {
			Class clazz = Class.forName("android.net.TrafficStats");
			Method method = clazz.getMethod("getUidTxBytes", new Class[] { Integer.TYPE });
			txbytes = ((Long) method.invoke(clazz, new Object[] { Integer.valueOf(uid) })).longValue();
		} catch (Exception e) {
			txbytes = -2L;
		}
		return txbytes;
	}

	/**
	 * 获取appname
	 * 
	 * @param mContext
	 * @return
	 */
	public static String getApplicationName(Context mContext) {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = mContext.getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(mContext.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}
		String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
		return applicationName;
	}

	/**
	 * 获取versionCode
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 获取versionName
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取app图标
	 * 
	 * @param mContext
	 * @return
	 */
	public static int getIcon(Context mContext) {
		try {
			PackageInfo pi = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
			return pi.applicationInfo.icon;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}
}
