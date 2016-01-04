package com.apputils.example.utils;

import com.apputils.example.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Window;
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
	private Context context;

	public MyToast(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init(context);
	}

	public MyToast(Context context, int theme) {
		super(context, theme);
		init(context);
	}

	public MyToast(Context context) {
		this(context, 0);
	}

	private void init(Context context) {
		this.context = context;
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
						if (MyToast.getToast(context).isShowing()) {
							MyToast.getToast(context).dismiss();
						}
						break;
					}
				};
			};
		}
		return handler;
	}

	public static MyToast getToast(Context context) {
		if (toast == null) {
			toast = new MyToast(context);
		}
		return toast;
	}

	public static Dialog makeText(Context context, String s, int duration) {
		MyToast mToast = getToast(context);
		mToast.setText(s);
		mToast.getHandler().removeMessages(DIALOG_DISMISS);
		mToast.show();
		mToast.getHandler().sendEmptyMessageDelayed(DIALOG_DISMISS, duration == LENGTH_SHORT?2000:3500);
		return mToast;
	}

}
