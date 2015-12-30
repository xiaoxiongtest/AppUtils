package com.apputils.example.base;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class MyHolderBaseAdapter<T> extends BaseAdapter {
	private List<T> list;
	private BaseHolder<T> holder;
	protected Context mContext;

	public MyHolderBaseAdapter(Context mContext,List<T> list) {
		this.list = list;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			// ���ݲ�ͬ�Ľ�����ʹ��listview��item��ĿЧ������holder,xml-->view���̴�������
			holder = getHolder();
		} else {
			holder = (BaseHolder) convertView.getTag();
		}
		// ��holder��������,��ǰ��ĿҪȥչʾ������,���������Ӽ����л�ȡ
		holder.setData(list.get(position));

		return holder.getRootView();
	}

	public abstract BaseHolder<T> getHolder();
}
