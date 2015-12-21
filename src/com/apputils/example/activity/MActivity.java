package com.apputils.example.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.apputils.example.R;
import com.apputils.example.dialog.DialogError;
import com.apputils.example.dialog.DialogLoading;
import com.apputils.example.dialog.Loading;
import com.apputils.example.dialog.MsgDialog;
import com.apputils.example.http.InitUrl;
import com.apputils.example.http.MHttpUtils;
import com.apputils.example.manage.MHandler;
import com.apputils.example.manage.MHandler.HandleMsgLisnener;
import com.apputils.example.utils.AbDisplayUtil;
import com.apputils.example.utils.MLog;
import com.apputils.example.utils.NetWorkHelper;
import com.apputils.example.utils.common.InitError;
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
import android.view.Window;
import android.widget.RemoteViews;

public class MActivity extends Activity {
	protected MHandler handler;
	protected Loading loadingDialog;
	protected boolean state;
	public static final int SINGLE_OBJECT = 0;
	public static final int ARRAY_OBJECT = 1;
	public static final int SINGLE_STRING = 2;
	public static final int NOTIFICATION_ID = 3;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MLog.I("class:" + this.getClass().toString());
		AbDisplayUtil.init(this);
		MHttpUtils.init(this);
		handler = new MHandler();
		String className = this.getClass().getName();
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
					break;
				}

			}
		});
		MHttpUtils.HANDLES.add(handler);
		initdialog();
		// 初始化网络
		httpUtils = MHttpUtils.getInstance();
		super.onCreate(savedInstanceState);
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
		accessNet(id, null);
	}

	public void accessNet(String id, String[][] params) {
		accessNet(id, params, null);
	}

	/**
	 * 请求网络
	 * 
	 * @param id
	 *            initUrl.xml文件中需要调用的接口的名称
	 * @param params
	 *            url中需要携带的参数
	 * @param request
	 *            请求中可能要添加的请求头参数
	 */
	public void accessNet(final String id, String[][] params, RequestParams request) {
		InitUrl initUrl = MHttpUtils.INITCONFIG.getUrl(id);
		if (initUrl != null) {
			String url = initUrl.url;
			// 拼接参数
			if (params != null) {
				boolean tag = true;
				for (String[] param : params) {
					if (!TextUtils.isEmpty(param[0]) && !TextUtils.isEmpty(param[1])) {
						if (tag) {
							url = url + "?";
							tag = false;
						} else {
							url = url + "&";
						}
						url = url + param[0] + "=" + param[1];
					}
				}
			}
			// 拼接自默认参数
			String autoParam = MHttpUtils.getAutoParamStr();

			if (!TextUtils.isEmpty(autoParam)) {
				if (initUrl.url.length() == url.length()) {
					url = url + "?";
				} else {
					url = url + "&";
				}
				if (null != autoParam) {
					url += autoParam;
				}
			}
			// 检查是否有网络
			if (NetWorkHelper.getNetWorkType(getApplicationContext()) == NetWorkHelper.NETWORKTYPE_INVALID) {
				MHttpUtils.INITCONFIG.getMsgDialog(MActivity.this);
				Message msg = new Message();
				InitError notNetInfo = MHttpUtils.getNotNetInfo();
				if (notNetInfo == null || notNetInfo.title == null || notNetInfo.value == null) {
					InitError error = new InitError();
					error.title = "警告";
					error.value = "网络连接失败，请检查网络";
					msg.obj = error;
				} else {
					msg.obj = notNetInfo;
				}
				msg.what = MHandler.MSG_SHOW_ERROR;
				handler.sendMessage(msg);
				return;
			}

			MLog.D(MLog.TAG_HTTP, id + " " + (initUrl.type == 0 ? "get" : "post") + " " + url);

			httpUtils.send(initUrl.type == 0 ? HttpMethod.GET : HttpMethod.POST, url, request,
					new RequestCallBack<String>() {
						@Override
						public void onStart() {
							handler.sendEmptyMessage(MHandler.MSG_SHOW_DIALOG);
							super.onStart();
						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							handler.sendEmptyMessage(MHandler.MSG_CLOSE_DIALOG);
							Message msg = new Message();
							InitError error = new InitError();
							error.title = "错误";
							error.value = arg1;
							msg.obj = error;
							msg.what = MHandler.MSG_SHOW_ERROR;
							handler.sendMessage(msg);
						}

						@Override
						public void onSuccess(ResponseInfo<String> info) {
							handler.sendEmptyMessage(MHandler.MSG_CLOSE_DIALOG);
							String json = info.result;
							MLog.D(MLog.TAG_HTTP, json);
							if (json != null) {
								if (MHttpUtils.INITCONFIG.getJsonObject(id) != null) {
									// json解析
									Gson gson = new Gson();
									Object obj = MHttpUtils.INITCONFIG.getJsonObject(id);
									try {
										disResposeMsg(id, gson.fromJson(json, obj.getClass()), SINGLE_OBJECT);
									} catch (Exception e) {
										try {
											disResposeMsg(id, getJsonList(json, obj.getClass()), ARRAY_OBJECT);
										} catch (Exception error) {
											showError("错误", "json数据解析错误");
										}
									}
								} else {
									MLog.D(MLog.TAG_HTTP, "返回的数据不是json");
									disResposeMsg(id, json, SINGLE_STRING);
								}

							} else {
								MLog.D(MLog.TAG_HTTP, "未返回数据");
							}
						}
					});
		}
	}

	private <T> ArrayList<T> getJsonList(String json, Class<T> clazz) throws Exception {
		ArrayList<T> lst = new ArrayList<T>();
		JsonArray array = new JsonParser().parse(json).getAsJsonArray();
		for (final JsonElement elem : array) {
			lst.add(new Gson().fromJson(elem, clazz));
		}
		return lst;
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
	public void disResposeMsg(String id, Object obj, int type) {

	}

}
