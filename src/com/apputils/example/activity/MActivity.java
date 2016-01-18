package com.apputils.example.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.apputils.example.R;
import com.apputils.example.dialog.DialogError;
import com.apputils.example.dialog.DialogLoading;
import com.apputils.example.dialog.Loading;
import com.apputils.example.dialog.MsgDialog;
import com.apputils.example.http.AccessNetUtils;
import com.apputils.example.http.MHttpUtils;
import com.apputils.example.manage.MHandler;
import com.apputils.example.manage.MHandler.HandleMsgLisnener;
import com.apputils.example.utils.AbDisplayUtil;
import com.apputils.example.utils.MLog;
import com.apputils.example.utils.NetWorkHelper;
import com.apputils.example.utils.common.InitError;
import com.apputils.example.utils.common.InitUrl;
import com.apputils.example.utils.verify.Md5;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RemoteViews;

public class MActivity extends Activity {
	protected MHandler handler;
	protected Loading loadingDialog;
	/**
	 * 是否显示加载进度的dialog&错误信息提示的dialog
	 */
	protected boolean state;
	/**
	 * 设置是否有标题 默认没有
	 */
	protected boolean hasTitle = false;
	/**
	 * 设置是否全屏，默认不全屏
	 */
	protected boolean isFullScreen = false;
	// 返回数据的类型
	public static final int SINGLE_OBJECT = 0;
	public static final int ARRAY_OBJECT = 1;
	public static final int SINGLE_STRING = 2;
	public static final int SUCCESS_BUT_NODATA =3;
	// 提示消息的id
	private static final int NOTIFICATION_ID = 3;
	public List<String> downing_list = new ArrayList<String>();
	/**
	 * 下载文件是否在通知提示栏显示
	 */
	protected boolean tooltipShow = false;
	/**
	 * 是否显示表示文件下载进度的progressbar
	 */
	protected boolean progressbarShow = false;
	private NotificationManager mNotificationManager;
	private Notification notification = null;

	/**
	 * 下载文件的弹出框按返回键是否可以取消
	 */
	protected boolean downLoadingCancel = false;

	/**
	 * @Fields LoadShow : 访问网络dialog是否显示
	 */
	protected boolean LoadShow = true;
	protected boolean ErrorShow = true;

	private int LoadingSize = 0;
	protected HttpUtils httpUtils;
	//private AccessNetUtils accessNetUtils;
	private DisResponseMsgCallBack disResponseMsgCallBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (!hasTitle) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		if (isFullScreen) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		MLog.I("" + this.getClass().toString());
		AbDisplayUtil.init(this);
		MHttpUtils.init(this);
		handler = getHandler();
		initdialog();
		getHttpUtils();
		//accessNetUtils = accessNetUtils.getInstance(this);
		getDisResponseMsgCallBack();
		super.onCreate(savedInstanceState);
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
	public HttpUtils getHttpUtils(){
		if(httpUtils == null){
			// 初始化网络
			httpUtils = MHttpUtils.getInstance();
		}
		return httpUtils;
	}

	public MHandler getHandler() {
		if (handler == null) {
			handler = new MHandler();
			String className = this.getClass().getSimpleName();
			handler.setId(className);
			handler.setHandleMsgLisnener(new HandleMsgLisnener() {
				@Override
				public void onMessage(Message msg) {

					switch (msg.what) {
					case MHandler.MSG_CLOSE:
						// 判断是否是嵌入的activity
						if (getParent() == null) {
							finish();
						}
						break;
					case MHandler.MSG_SHOW_DIALOG:
						showLoad();
						break;
					case MHandler.MSG_CLOSE_DIALOG:
						closeLoad();
						break;
					case MHandler.MSG_SHOW_ERROR:
						InitError error = (InitError) msg.obj;
						showError(error.title, error.value);
						break;
					default:
						disHandlerMsg(msg);
						break;
					}

				}
			});
			MHttpUtils.HANDLES.add(handler);
		}
		return handler;
	}

	public void downFileToLocal(String url, final String target) {
		if (TextUtils.isEmpty(url) || TextUtils.isEmpty(target)) {
			return;
		}
		final String urlMd5 = Md5.mD5(url);
		if (!downing_list.contains(urlMd5)) {
			downing_list.add(urlMd5);
			MHttpUtils.getInstance().download(url, target, new RequestCallBack<File>() {
				private ProgressDialog mProgressDialog;

				@Override
				public void onStart() {
					if (tooltipShow) {
						Intent intent = new Intent(MActivity.this, MActivity.class);

						PendingIntent pIntent = PendingIntent.getActivity(MActivity.this, 0, intent, 0);
						mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
						notification = new Notification();
						notification.icon = R.drawable.ic_launcher;
						notification.tickerText = "新通知";
						// 通知栏显示所用到的布局文件
						notification.contentView = new RemoteViews(getPackageName(), R.layout.content_view);
						notification.contentIntent = pIntent;
						mNotificationManager.notify(NOTIFICATION_ID, notification);
						MLog.D(MLog.TAG_HTTP, "创建下载进度通知");
					}

					if (progressbarShow) {
						mProgressDialog = new ProgressDialog(MActivity.this);
						mProgressDialog.setTitle("下载中...");// 设置ProgressDialog标题
						mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置进度条风格，风格为长形
						mProgressDialog.setCanceledOnTouchOutside(false);
						mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						mProgressDialog.setMax(100);
						mProgressDialog.setCancelable(downLoadingCancel);
						mProgressDialog.setProgress(0);
						mProgressDialog.show();
						MLog.D(MLog.TAG_HTTP, "创建下载进度条弹框");
					}
				}

				@Override
				public void onLoading(final long total, final long current, boolean isUploading) {
					if (tooltipShow && notification != null && mNotificationManager != null) {
						notification.contentView.setTextViewText(R.id.content_view_text1,
								(int) (current * 100 / total) + "%");
						notification.contentView.setProgressBar(R.id.content_view_progress, 100,
								(int) (current * 100 / total), false);
						mNotificationManager.notify(NOTIFICATION_ID, notification);
					}
					if (progressbarShow && mProgressDialog != null) {
						MLog.D(MLog.TAG_HTTP, "" + (int) (current * 100 / total));
						mProgressDialog.setProgress((int) (current * 100 / total));
						mProgressDialog.setTitle("下载中" + (int) (current * 100 / total) + "%");
					}
				}

				@Override
				public void onSuccess(ResponseInfo<File> info) {
					downing_list.remove(urlMd5);
					mNotificationManager.cancel(NOTIFICATION_ID);
					if (progressbarShow && mProgressDialog != null) {
						mProgressDialog.dismiss();
					}
					MLog.D(MLog.TAG_HTTP, "下载成功");
					downSuccessTodo(info, target);
				}

				@Override
				public void onFailure(HttpException e, String str) {
					downing_list.remove(urlMd5);
					mNotificationManager.cancel(NOTIFICATION_ID);
					File apk = new File(target);
					if (apk.exists()) {
						apk.delete();
					}
					showError("错误", str);
				}
			});
		} else {
			showError("提示", "正在下载，请稍后再试...");
		}
	}

	/**
	 * 文件下载成功后需要进行的操作
	 */
	public void downSuccessTodo(ResponseInfo<File> info, String target) {

	}

	/**
	 * 显示错误提示框
	 * 
	 * @param title
	 * @param msg
	 */
	public void showError(String title, String msg) {
		try {
			Context context = null;
			if (null != this && getParent() != null && getParent() instanceof Activity)
				context = getParent();
			else
				context = this;
			if (null == context && !state)
				return;
			MsgDialog errordialog = MHttpUtils.INITCONFIG.getMsgDialog(context);
			if (errordialog == null) {
				errordialog = new DialogError(context);
			}
			if (msg != null && msg.length() > 0) {
				errordialog.setMsg(title, msg);
			}
			errordialog.show();

		} catch (Exception e) {
			MLog.D("showError:" + this.getClass().toString() + "已被清除");
		}
	}

	/**
	 * 主线程开启一个加载框
	 */
	public void showLoad() {
		if (this.LoadShow && state) {
			LoadingSize++;
			try {
				this.loadingDialog.show();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 主线程关闭一个加载框
	 */
	public void closeLoad() {
		if (this.LoadShow || LoadingSize > 0) {
			LoadingSize--;
			if (LoadingSize > 0) {
				return;
			}
			LoadingSize = 0;
			try {
				this.loadingDialog.dismiss();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 初始化访问网络dialog
	 */
	protected void initdialog() {
		Context context;
		if (this.getParent() != null && this.getParent() instanceof Activity) {
			context = this.getParent();
		} else {
			context = this;
		}

		Loading loading = MHttpUtils.INITCONFIG.getLoading(context);
		if (loading == null) {
			loading = new DialogLoading(context);

		}
		this.loadingDialog = loading;
	}

	@Override
	protected void onResume() {
		state = true;
		super.onResume();
	}

	@Override
	protected void onPause() {
		state = false;
		super.onPause();
	}

	/**
	 * 关闭当前页面所有加载框
	 */
	protected void closeAll() {
		MHttpUtils.HANDLES.remove(handler);
		LoadingSize = 0;
		handler.sendEmptyMessage(MHandler.MSG_CLOSE_DIALOG);
	}

	@Override
	protected void onDestroy() {
		closeAll();
		super.onDestroy();
	}
	
	public void accessNet(String id) {
		AccessNetUtils.getInstance(this).accessNet(id, null, getDisResponseMsgCallBack());
	}

	public void accessNet(String id, String[][] params) {
		AccessNetUtils.getInstance(this).accessNet(id, params, null, getDisResponseMsgCallBack());
	}
	public void accessNet(String id, String[][] params,RequestParams request) {
		AccessNetUtils.getInstance(this).accessNet(id, params, request, getDisResponseMsgCallBack());
	}

	protected void disHandlerMsg(Message msg) {

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

}
