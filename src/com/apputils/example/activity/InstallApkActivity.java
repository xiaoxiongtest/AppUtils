package com.apputils.example.activity;

import java.io.File;

import com.apputils.example.utils.AbAppUtil;
import com.apputils.example.utils.AbFileUtil;
import com.apputils.example.utils.MLog;
import com.lidroid.xutils.http.ResponseInfo;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

public class InstallApkActivity extends MActivity {

	public static final int INSTALL_APK = 1;
	/**
	 * 
	 * @param url
	 * @param path
	 * @param versionCode
	 *            新版本apk的versionCode
	 * @param mContext
	 * @param falg
	 */
	protected void downApkToLocal(String url, int versionCode) {
		if (TextUtils.isEmpty(url)) {
			return;
		}
		String fileName = "";
		final String apkPath;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + AbFileUtil.downNewApkDir;
			File file = new File(fileName);
			if (!file.exists()) {
				file.mkdirs();
			}
		} else {
			fileName = getApplicationContext().getFilesDir().getAbsolutePath() + AbFileUtil.downNewApkDir;
			File file = new File(fileName);
			if (!file.exists()) {
				boolean mkdirs = file.mkdirs();
				MLog.D(MLog.TAG_HTTP, "mkdirs:" + mkdirs);
			}
		}
		apkPath = fileName + AbAppUtil.getApplicationName(getApplicationContext()) + versionCode + ".apk";
		downFileToLocal(url, apkPath);
	}

	@Override
	public void downSuccessTodo(ResponseInfo<File> info, String target) {
		installApk(target);
	}

	/**
	 * 安装apk
	 * 
	 * @param path
	 */
	public void installApk(String path) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
		startActivityForResult(intent, INSTALL_APK);
	}

	/**
	 * 取消安装时候的回调
	 * 
	 * @param intent
	 */
	public void cancelInstall(Intent intent) {

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		cancelInstall(intent);
	}
}
