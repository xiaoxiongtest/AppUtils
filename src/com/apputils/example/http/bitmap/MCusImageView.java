package com.apputils.example.http.bitmap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import com.apputils.example.utils.AbImageUtil;
import com.apputils.example.utils.MLog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;

public class MCusImageView {

	public String path = null;
	public File myCaptureFile = null;
	public Bitmap bm = null;

	public MCusImageView(Context context, int id) {
		Drawable drawable = context.getResources().getDrawable(id);
		bm = AbImageUtil.drawableToBitmap(drawable);
		try {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				path = Environment.getExternalStorageDirectory() + "/apputils/img/";
			} else {
				path = context.getCacheDir().getPath() + "/apputils/img/";
			}
			File dirFile = new File(path);
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
			path = path + id + ".jpg";
			myCaptureFile = new File(path);
			if (!myCaptureFile.exists()) {
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
				bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
				bos.flush();
				bos.close();
			}
		} catch (Exception e) {
			MLog.D(MLog.TAG_IMG, "图片获取本地地址失败：" + e.getMessage());
			path = null;
			if (myCaptureFile != null) {
				myCaptureFile.delete();
			}
		}
	}

	public String getPath() {
		return path;
	}

	public File getFile() {
		return myCaptureFile;
	}

	public Bitmap getBitmap() {
		return bm;
	}
}
