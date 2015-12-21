package com.apputils.example.dialog;

import com.apputils.example.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DialogLoading extends Dialog implements Loading {
	private ProgressBar mProgressbar;
	private TextView mText;

	public DialogLoading(Context context) {
		super(context, R.style.DefDialog);
		this.setContentView(R.layout.dialog_loading);
		this.setCanceledOnTouchOutside(false);
		mProgressbar = (ProgressBar) findViewById(R.id.mProgressbar);
		mText = (TextView) findViewById(R.id.mText);
	}

	public ProgressBar getProgressBar() {
		return mProgressbar;
	}

	public TextView getTextView() {
		return mText;
	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}

}
