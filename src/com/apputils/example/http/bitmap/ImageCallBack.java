package com.apputils.example.http.bitmap;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;

import android.graphics.Bitmap;

public abstract class ImageCallBack {
	public void onLoadCompleted(String arg1, BitmapDisplayConfig arg3, Bitmap bm, BitmapLoadFrom arg4) {
		onLoadCompleted(bm);
	}

	public abstract void onLoadCompleted(Bitmap bm);

	public abstract void onLoadFailed(String arg1);
}
