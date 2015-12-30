package com.apputils.example.manage;

import android.os.Handler;
import android.os.Message;

public class MHandler extends Handler {
	public String id;
	public HandleMsgLisnener msglisnener;
	/** @Fields MSG_LOAD : 关闭页面 */
	public static final int MSG_CLOSE = 100;

	/** @Fields MSG_LOAD : 开启一个加载框 */
	public static final int MSG_SHOW_DIALOG = 101;

	/** @Fields MSG_LOAD : 关闭一个加载框 */
	public static final int MSG_CLOSE_DIALOG = 102;

	public static final int MSG_SHOW_ERROR = 103;
	
	public static final int MSG_CLOSE_ERROR = 104;

	public void setId(String id) {
		this.id = id;
	}

	public void close() {
		this.sendEmptyMessage(MSG_CLOSE);
	}

	public interface HandleMsgLisnener {
		public void onMessage(Message msg);
	}

	public void setHandleMsgLisnener(HandleMsgLisnener msglisnener) {
		this.msglisnener = msglisnener;
	}

	@Override
	public synchronized void handleMessage(Message msg) {
		if (msglisnener != null) {
			msglisnener.onMessage(msg);
		}
	}
}
