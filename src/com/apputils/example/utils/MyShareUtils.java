package com.apputils.example.utils;

import com.apputils.example.R;
import com.apputils.example.http.MHttpUtils;
import com.apputils.example.http.bitmap.MCusImageView;
import com.apputils.example.utils.common.ShareMode;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

/**
 * 集成第三方分享 QQ,微信,新浪微博
 *
 */
public class MyShareUtils implements IWeiboHandler.Response, IWXAPIEventHandler {
	private static MyShareUtils myShareUtils;
	private Activity mContext;
	private Tencent mTencent;
	private IWeiboShareAPI mWeiboShareAPI;
	private Bundle savedInstanceState;
	private IWXAPI wxApi;
	public static int WECHAT_FRIENDS = 0;
	public static int WECHAT_FRIENDS_CIRCLE = 1;

	public static MyShareUtils getInstance(Activity context, Bundle savedInstanceState) {
		if (myShareUtils == null) {
			myShareUtils = new MyShareUtils(context, savedInstanceState);
		}
		return myShareUtils;
	}

	public MyShareUtils(Activity context, Bundle savedInstanceState) {
		this.savedInstanceState = savedInstanceState;
		this.mContext = context;
		initConfig();
	}

	public void initConfig() {
		// QQ
		mTencent = Tencent.createInstance(MHttpUtils.INITCONFIG.getQQappId(), mContext);
		// sina
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mContext, MHttpUtils.INITCONFIG.getSinappKey());
		// 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
		// 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
		// 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
		// 失败返回 false，不调用上述回调
		if (savedInstanceState != null) {
			mWeiboShareAPI.handleWeiboResponse(mContext.getIntent(), this);
		}
		// 如果未安装微博客户端，设置下载微博对应的回调
		if (!mWeiboShareAPI.isWeiboAppInstalled()) {
			mWeiboShareAPI.registerWeiboDownloadListener(new IWeiboDownloadListener() {
				@Override
				public void onCancel() {
					Toast.makeText(mContext, R.string.weibosdk_demo_cancel_download_weibo, Toast.LENGTH_SHORT).show();
				}
			});
		}

		// 检查微博客户端环境是否正常，如果未安装微博，弹出对话框询问用户下载微博客户端
		if (mWeiboShareAPI.checkEnvironment(true)) {
			// 注册第三方应用 到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
			// 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
			mWeiboShareAPI.registerApp();
		}
		// 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
		// 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
		// 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
		// 失败返回 false，不调用上述回调
		// if (savedInstanceState != null) {
		mWeiboShareAPI.handleWeiboResponse(mContext.getIntent(), this);
		// 微信
		wxApi = WXAPIFactory.createWXAPI(mContext, MHttpUtils.INITCONFIG.getWXappId());
		wxApi.registerApp(MHttpUtils.INITCONFIG.getWXappId());
		wxApi.handleIntent(mContext.getIntent(), this);

	}

	@Override
	public void onResponse(BaseResponse baseResp) {
		switch (baseResp.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			Toast.makeText(mContext, R.string.weibosdk_demo_toast_share_success, Toast.LENGTH_LONG).show();
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			Toast.makeText(mContext, R.string.weibosdk_demo_toast_share_canceled, Toast.LENGTH_LONG).show();
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			Toast.makeText(mContext,
					mContext.getString(R.string.weibosdk_demo_toast_share_failed) + "Error Message: " + baseResp.errMsg,
					Toast.LENGTH_LONG).show();
			break;
		}
	}

	public class BaseUiListener implements IUiListener {
		@Override
		public void onError(UiError e) {
			MLog.D(MLog.TAG_THIRD,
					"QQ分享失败  code:" + e.errorCode + ", msg:" + e.errorMessage + ", detail:" + e.errorDetail);
		}

		@Override
		public void onCancel() {
			MLog.D(MLog.TAG_THIRD, "QQ分享取消");
		}

		@Override
		public void onComplete(Object arg0) {
			MLog.D(MLog.TAG_THIRD, "QQ分享成功");
		}

	}

	public void onNewIntent(Intent intent) {
		if (intent != null) {
			mWeiboShareAPI.handleWeiboResponse(intent, this);
		}
	}

	public IWeiboShareAPI getIWeiboShareAPI() {
		return mWeiboShareAPI;
	}

	public void shareToQQ(ShareMode mode) {
		Bundle params = new Bundle();
		// 分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_ SUMMARY不能全为空，最少必须有一个是有值的。
		params.putString(QQShare.SHARE_TO_QQ_TITLE, mode.title);
		// 分享的消息摘要，最长50个字
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mode.content);
		// 这条分享消息被好友点击后的跳转URL。
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mode.targetUrl);
		// 判断是否有本地分享图片，如果有，就用本地的，如果没有就用网络的
		params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mode.imgUrl);
		params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, mode.localImgUrl);
		mTencent.shareToQQ(mContext, params, new BaseUiListener());
	}

	/**
	 * 分享到微信
	 * 
	 * @param flag
	 *            <p>
	 *            {@link #WECHAT_FRIENDS} 微信好友
	 *            <p>
	 *            {@link #WECHAT_FRIENDS_CIRCLE} 微信朋友圈
	 * @param mode
	 */
	public void shareToWechat(int flag, ShareMode mode) {
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = mode.targetUrl;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = mode.title;
		msg.description = mode.content;
		// 这里替换一张自己工程里的图片资源
		msg.setThumbImage(mode.thumbImage);
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
		wxApi.sendReq(req);
	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面。
	 * 
	 * @see {@link #sendMultiMessage} 或者 {@link #sendSingleMessage}
	 */
	public void shareToSina(ShareMode mode) {
		// 1. 初始化微博的分享消息
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
		weiboMessage.textObject = new TextObject();
		weiboMessage.textObject.text = mode.content;
		// 用户可以分享其它媒体资源（网页、音乐、视频、声音中的一种）
		weiboMessage.mediaObject = new WebpageObject();
		weiboMessage.mediaObject.actionUrl = mode.targetUrl;
		weiboMessage.mediaObject.identify = Utility.generateGUID();
		weiboMessage.mediaObject.title = mode.title;
		weiboMessage.mediaObject.description = mode.des;
		// 大小要小于15k
		weiboMessage.mediaObject.setThumbImage(mode.thumbImage);
		// 2. 初始化从第三方到微博的消息请求
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = weiboMessage;
		// 3. 发送请求消息到微博，唤起微博分享界面
		mWeiboShareAPI.sendRequest(request);
	}

	@Override
	public void onReq(BaseReq arg0) {

	}

	@Override
	public void onResp(BaseResp resp) {
		MLog.D(MLog.TAG_THIRD, "微信分享errorCode:" + resp.errCode + ",errorStr：" + resp.errStr);
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			// 分享成功
			Toast.makeText(mContext, "分享成功", Toast.LENGTH_SHORT).show();
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			// 分享取消
			Toast.makeText(mContext, "分享取消", Toast.LENGTH_SHORT).show();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			// 分享拒绝
			Toast.makeText(mContext, "分享拒绝", Toast.LENGTH_SHORT).show();
			break;
		}
	}

}
