package com.apputils.example.base;

import android.content.Context;
import android.view.View;

public abstract class BaseHolder<T> {

	private View view;
	private T mData;
	protected Context mContext;

	public BaseHolder(Context mContext) {
		this.mContext = mContext;
		// λ�õ�xml--->view����,view����Ҫȥlistview��getView����չʾ��view
		view = initView();
		// ��ͬ��convertView.settag(this);
		view.setTag(this);
	}

	// ��������
	public void setData(T mData) {
		// ��ȡgetView�����ж�Ӧ��ǰ��ĿҪȥչʾ������,���������ڸ����в���ȷ��,���Է���
		this.mData = mData;
		// ���ݱ�����õ��ؼ�����ȥ,
		refreshView();// ���������ø��ؼ��Ĳ���(��������δ֪,�ؼ�δ֪),����
	}

	public T getData() {
		return mData;
	}

	// ���ص��ǹ��췽����xmlת���ɵ�view����,�����view����Ҫȥ����չʾ��convertView
	public View getRootView() {
		return view;// view������xml-->view,initView()�����õ��Ľ��
	}

	public abstract void refreshView();

	public abstract View initView();
}
