package com.apputils.example.utils;

import com.apputils.example.R;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 针对一些手机不能弹出toast的问题
 * 
 * @author ChuXin
 *
 */
public class MyToast extends Dialog {
	private static final int DIALOG_DISMISS = 1;
	public static final int LENGTH_LONG = 1;
	public static final int LENGTH_SHORT = 0;

	private TextView mTextView;
	private Handler handler;
	private static MyToast toast;
	private Activity activity;

	public MyToast(Activity activity, boolean cancelable, OnCancelListener cancelListener) {
		super(activity, cancelable, cancelListener);
		init(activity);
	}

	public MyToast(Activity activity, int theme) {
		super(activity, theme);
		init(activity);
	}

	public MyToast(Activity activity) {
		// 背景透明不变灰
		this(activity, R.style.DialogStyle);
	}

	private void init(Activity activity) {
		this.activity = activity;
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		this.setContentView(R.layout.dialog_mytoast);
		this.setCanceledOnTouchOutside(false);
		mTextView = (TextView) findViewById(R.id.mTextView);

	}

	public void setText(String s) {
		if (mTextView != null) {
			mTextView.setText(s);
		}
	}

	public Handler getHandler() {
		if (handler == null) {
			handler = new Handler() {
				public void dispatchMessage(android.os.Message msg) {
					switch (msg.what) {
					case DIALOG_DISMISS:
						if (MyToast.getToast(activity).isShowing()) {
							MyToast.getToast(activity).dismiss();
						}
						break;
					}
				};
			};
		}
		return handler;
	}

	public static MyToast getToast(Activity activity) {
		if (toast == null) {
			toast = new MyToast(activity);
			Window dialogWindow = toast.getWindow();
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
			dialogWindow.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
			lp.y = (int) AbDisplayUtil.dip2px(activity, 50); // 新位置Y坐标
			dialogWindow.setAttributes(lp);
		}
		return toast;
	}

	public static Dialog makeText(Activity activity, String s, int duration) {
		MyToast mToast = getToast(activity);
		mToast.setText(s);
		mToast.getHandler().removeMessages(DIALOG_DISMISS);
		mToast.show();
		mToast.getHandler().sendEmptyMessageDelayed(DIALOG_DISMISS, duration == LENGTH_SHORT ? 2000 : 3500);
		return mToast;
	}

	@Override
	public void dismiss() {
		super.dismiss();
		toast = null;
	}

}
