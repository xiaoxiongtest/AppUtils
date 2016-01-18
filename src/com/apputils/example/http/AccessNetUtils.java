package com.apputils.example.http;

import java.util.ArrayList;

import com.apputils.example.manage.MHandler;
import com.apputils.example.utils.MLog;
import com.apputils.example.utils.NetWorkHelper;
import com.apputils.example.utils.common.InitError;
import com.apputils.example.utils.common.InitUrl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.apputils.example.activity.DisResponseMsgCallBack;
import com.apputils.example.activity.MActivity;

import android.os.Message;
import android.text.TextUtils;

public class AccessNetUtils {
	private static AccessNetUtils accessNetUtils;
	private MActivity activity;

	public AccessNetUtils(MActivity activity) {
		this.activity = activity;
	}

	public static AccessNetUtils getInstance(MActivity activity) {
		if (accessNetUtils == null) {
			accessNetUtils = new AccessNetUtils(activity);
		}
		return accessNetUtils;
	}

	public void accessNet(String id, DisResponseMsgCallBack callback) {
		accessNet(id, null, callback);
	}

	public void accessNet(String id, String[][] params, DisResponseMsgCallBack callback) {
		accessNet(id, params, null, callback);
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
	public void accessNet(final String id, String[][] params, RequestParams request,
			final DisResponseMsgCallBack callback) {
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
			if (NetWorkHelper.getNetWorkType(activity) == NetWorkHelper.NETWORKTYPE_INVALID) {
				MHttpUtils.INITCONFIG.getMsgDialog(activity);
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
				activity.getHandler().sendMessage(msg);
				return;
			}

			MLog.D(MLog.TAG_HTTP, id + " " + (initUrl.type == 0 ? "get" : "post") + " " + url);

			activity.getHttpUtils().send(initUrl.type == 0 ? HttpMethod.GET : HttpMethod.POST, url, request,
					new RequestCallBack<String>() {
						@Override
						public void onStart() {
							activity.getHandler().sendEmptyMessage(MHandler.MSG_SHOW_DIALOG);
							super.onStart();
						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							activity.getHandler().sendEmptyMessage(MHandler.MSG_CLOSE_DIALOG);
							Message msg = new Message();
							InitError error = new InitError();
							error.title = "错误";
							error.value = arg1;
							msg.obj = error;
							msg.what = MHandler.MSG_SHOW_ERROR;
							activity.getHandler().sendMessage(msg);
						}

						@Override
						public void onSuccess(ResponseInfo<String> info) {
							activity.getHandler().sendEmptyMessage(MHandler.MSG_CLOSE_DIALOG);
							String json = info.result;
							MLog.D(MLog.TAG_HTTP, json);
							if (json != null && json.length() > 0) {
								if (MHttpUtils.INITCONFIG.getJsonObject(id) != null) {
									// json解析
									Gson gson = new Gson();
									Object obj = MHttpUtils.INITCONFIG.getJsonObject(id);
									try {
										callback.disResposeMMsg(id, gson.fromJson(json, obj.getClass()),
												MActivity.SINGLE_OBJECT);
									} catch (Exception e) {
										try {
											callback.disResposeMMsg(id, getJsonList(json, obj.getClass()),
													MActivity.ARRAY_OBJECT);
										} catch (Exception error) {
											activity.showError("错误", "json数据解析错误");
										}
									}
								} else {
									MLog.D(MLog.TAG_HTTP, "返回的数据不是json");
									callback.disResposeMMsg(id, json, MActivity.SINGLE_STRING);
								}

							} else {
								MLog.D(MLog.TAG_HTTP, "未返回数据");
								callback.disResposeMMsg(id, null, MActivity.SUCCESS_BUT_NODATA);
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
}
