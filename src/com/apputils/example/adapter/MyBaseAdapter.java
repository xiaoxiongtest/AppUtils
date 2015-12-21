package com.apputils.example.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class MyBaseAdapter<T> extends BaseAdapter {
	protected List<T> als;
	protected Context mContext;

	public MyBaseAdapter(Context mContext, List<T> als) {
		this.als = als;
		this.mContext= mContext;
	}

	@Override
	public int getCount() {
		return als == null ? 0 : als.size();
	}

	@Override
	public Object getItem(int position) {
		return als == null ? null : als.get(position);
	}

	@Override
	public long getItemId(int position) {
		return als == null ? 0 : position;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

}
