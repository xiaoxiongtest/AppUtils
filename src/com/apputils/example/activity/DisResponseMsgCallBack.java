package com.apputils.example.activity;
public interface DisResponseMsgCallBack {
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
	public abstract void disResposeMsg(String id, Object obj, int type);
}
