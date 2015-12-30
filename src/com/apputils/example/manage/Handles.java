package com.apputils.example.manage;

import java.util.ArrayList;
import android.os.Handler;
import android.os.Message;

public class Handles {
	private ArrayList<MHandler> HANDLES = new ArrayList<MHandler>();
	
	public void sendMsg(String id,Message msg){
		for(MHandler handle : this.get(id)){
			handle.sendMessage(msg);
		}
	}
	public void sendMsg(String id,int type){
		for(MHandler handle : this.get(id)){
			handle.sendEmptyMessage(type);
		}
	}

	/**
	 * 向HANDLES中添加一个MHandler对象
	 * 
	 * @param handler
	 */
	public void add(MHandler handler) {
		HANDLES.add(handler);
	}

	/**
	 * 从HANDLES中删除一个MHandler对象
	 * 
	 * @param handler
	 */
	public void remove(MHandler handler) {
		HANDLES.remove(handler);
	}

	/**
	 * 获取HANDLES中activity,fragment,fragmentActivity的个数
	 * 
	 * @return
	 */
	public int size() {
		return HANDLES.size();
	}

	/**
	 * 根据id获取activity,fragment,fragmentActivity的对象
	 * 
	 * @param ind
	 * @return
	 */
	public Handler get(int ind) {
		return HANDLES.get(ind);
	}

	/**
	 * 根据id获取activity,fragment,fragmentActivity的集合
	 * 
	 * @param id
	 * @return
	 */
	public ArrayList<MHandler> get(String id) {
		ArrayList<MHandler> retn = new ArrayList<MHandler>();
		for (int i = 0; i < HANDLES.size(); i++) {
			if (HANDLES.get(i).id.equals(id)) {
				retn.add(HANDLES.get(i));
			}
		}
		return retn;
	}

	/**
	 * 将id所对应activity,fragment,fragmentActivity其中一个对象进行关闭
	 * 
	 * @param id
	 */
	public void closeOne(String id) {
		for (MHandler handle : this.get(id)) {
			handle.sendEmptyMessage(MHandler.MSG_CLOSE);
			break;
		}
	}

	/**
	 * 将id所对应activity,fragment,fragmentActivity关闭
	 * 
	 * @param id
	 */
	public void close(String id) {
		for (MHandler fhand : get(id)) {
			fhand.sendEmptyMessage(MHandler.MSG_CLOSE);
		}
	}

	/**
	 * 将HANDLES中所有的activity,fragment,fragmentActivity全部关闭
	 */
	public void closeAll() {
		for (MHandler fhand : HANDLES) {
			fhand.sendEmptyMessage(MHandler.MSG_CLOSE);
		}
	}
}
