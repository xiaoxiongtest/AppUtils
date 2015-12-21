package com.apputils.example.dialog;

import com.apputils.example.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DialogDowning extends Dialog implements Loading {
	private ProgressBar mProgressbar;
	private TextView mText;

	public DialogDowning(Context context) {
		super(context, R.style.DefDialog);
		LayoutInflater inflater = LayoutInflater.from(context);
		this.setContentView(inflater.inflate(R.layout.dialog_downing, null), new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
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
