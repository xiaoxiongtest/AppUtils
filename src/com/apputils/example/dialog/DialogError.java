package com.apputils.example.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class DialogError implements MsgDialog {
	private AlertDialog.Builder builder;
	private AlertDialog dialog;

	public DialogError(Context context) {
		builder = new AlertDialog.Builder(context);
	}

	@Override
	public void setMsg(String title, String msg) {
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton("确定", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

	}

	@Override
	public void show() {
		dialog = builder.create();
		dialog.show();
	}

	@Override
	public void toLogin() {

	}

	@Override
	public void dismiss() {
		dialog.dismiss();
	}

}
