package com.apputils.example.utils;

import com.apputils.example.http.MHttpUtils;
import com.apputils.example.utils.common.ShareMode;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.os.Bundle;

/**
 * 集成第三方分享  QQ,微信,新浪微博
 *
 */
public class MyShareUtils {
	private static MyShareUtils myShareUtils;
	private Activity mContext;
	private Tencent mTencent;

	public static MyShareUtils getInstance(Activity context) {
		if (myShareUtils == null) {
			myShareUtils = new MyShareUtils(context);
		}
		return myShareUtils;
	}

	public MyShareUtils(Activity context) {
		this.mContext = context;
		initConfig();
	}

	public void initConfig() {
		mTencent = Tencent.createInstance(MHttpUtils.INITCONFIG.getQQappId(), mContext);

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

	public void shareToQQ(ShareMode mode) {
		Bundle params = new Bundle();
		// 分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_ SUMMARY不能全为空，最少必须有一个是有值的。
		params.putString(QQShare.SHARE_TO_QQ_TITLE, mode.title);
		// 分享的消息摘要，最长50个字
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mode.content);
		// 这条分享消息被好友点击后的跳转URL。
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mode.targetUrl);
		// 判断是否有本地分享图片，如果有，就用本地的，如果没有就用网络的
		if(mode.localImgUrl == null){
			// 分享的图片URL
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mode.imgUrl);
		}else{
			 params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,mode.localImgUrl);
		}
		mTencent.shareToQQ(mContext, params, new BaseUiListener());
	}
}
