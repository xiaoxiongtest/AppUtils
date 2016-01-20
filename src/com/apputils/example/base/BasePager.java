package com.apputils.example.base;
import com.apputils.example.activity.DisResponseMsgCallBack;
import com.apputils.example.activity.MActivity;

import com.lidroid.xutils.http.RequestParams;
import android.view.View;
import com.apputils.example.http.AccessNetUtils;

public abstract class BasePager {
	public MActivity context;
	private AccessNetUtils accessNetUtils;
	public DisResponseMsgCallBack disResponseMsgCallBack;
	public View view;
	
	public BasePager(MActivity context) {
		//构建当前界面的展示效果,但是父类对于子类的界面效果未知
		//上下文环境需要给子类的initView方法去做使用,所以在调用initView()方法前就得传递
		this.context = context;
		this.accessNetUtils= AccessNetUtils.getInstance(context);
		view = initView();
		
	}
	
	//xml--->view,context
	public abstract View initView();
	//数据填充UI的操作
	public abstract void initData();
	
	//返回对应界面生成的view对象的方法
	public View getRootView(){
		return view;
	}
	/**
	 * 网络数据请求成功后的回调
	 * 
	 * @param id
	 * @param obj
	 *            如果数据为单个对象，则以当个对象接收，如果是集合则用ArrayList<？>
	 * 
	 * @param type
	 *            json数据的类型；0表示数据为单个对象，1表示对象集合
	 */
	public void disResposeMsg(String id, Object obj, int type){
		
	}
	public void accessNet(String id) {
		accessNetUtils.accessNet(id, null, getDisResponseMsgCallBack());
	}

	public void accessNet(String id, String[][] params) {
		accessNetUtils.accessNet(id, params, null, getDisResponseMsgCallBack());
	}
	public void accessNet(String id, String[][] params,RequestParams request) {
		accessNetUtils.accessNet(id, params, null, getDisResponseMsgCallBack());
	}
	public DisResponseMsgCallBack getDisResponseMsgCallBack(){
		if(disResponseMsgCallBack == null){
			disResponseMsgCallBack = new DisResponseMsgCallBack(){

				@Override
				public void disResposeMMsg(String id, Object obj, int type) {
					disResposeMsg(id, obj, type);
				}
			};
		}
		return disResponseMsgCallBack;
	}

}
