package com.apputils.example.base;

import android.content.Context;
import android.view.View;

public abstract class BaseHolder<T> {

	private View view;
	private T mData;
	protected Context mContext;

	public BaseHolder(Context mContext) {
		this.mContext = mContext;
		view = initView();
		view.setTag(this);
	}

	public void setData(T mData) {
		this.mData = mData;
		refreshView();
	}

	public T getData() {
		return mData;
	}

	public View getRootView() {
		return view;
	}

	public abstract void refreshView();

	public abstract View initView();
}
