package com.apputils.example.mobile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.apputils.example.dialog.DialogError;
import com.apputils.example.dialog.DialogLoading;
import com.apputils.example.dialog.Loading;
import com.apputils.example.dialog.MsgDialog;
import com.apputils.example.utils.MLog;
import com.apputils.example.utils.common.InitError;
import com.apputils.example.utils.common.InitUrl;
import com.apputils.example.xmlencrypt.XMLDES;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.content.Context;

public class InitConfig {
	public HashMap<Integer, InitError> mErrorMap = new HashMap<Integer, InitError>();
	public HashMap<String, InitUrl> mUrlMap = new HashMap<String, InitUrl>();
	public int mSoTimeOut, mConnectionTimeOut;
	/** @Fields isDebug 设置调试模式true为开发模式，false为生产模式 */
	public static boolean isDebug = false;
	public String mUri = "", mUrl = "", mLoading = null, mError = null, mLog = "true", mTemppath = "", mPackage = "",
			mVersion = "";

	public InitConfig(Context mContext) {
		try {
			readUrl(mContext);
			readError(mContext);
		} catch (Exception e) {
			throw new IllegalStateException("Init Error:" + e.getMessage());
		}
	}

	private void readError(Context mContext) throws Exception {
		InputStream istr = mContext.getAssets().open("errorInfo.xml");
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(istr, "UTF-8");

		int eventType = xpp.getEventType();
		String reading = "", version = "", des = "", type = "", title = "", value = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				reading = xpp.getName();
				if (xpp.getName().toUpperCase(Locale.ENGLISH).equals("ERROR")) {
					des = type = title = value = "";
					for (int i = 0; i < xpp.getAttributeCount(); i++) {
						if (xpp.getAttributeName(i).toUpperCase(Locale.ENGLISH).equals("DES")) {
							des = xpp.getAttributeValue(i);
						} else if (xpp.getAttributeName(i).toUpperCase(Locale.ENGLISH).equals("TITLE")) {
							title = xpp.getAttributeValue(i);
						} else if (xpp.getAttributeName(i).toUpperCase(Locale.ENGLISH).equals("TYPE")) {
							type = xpp.getAttributeValue(i);
						}
					}
				} else if (xpp.getName().toUpperCase(Locale.ENGLISH).equals("ERRORINFO")) {
					for (int i = 0; i < xpp.getAttributeCount(); i++) {
						if (xpp.getAttributeName(i).toUpperCase(Locale.ENGLISH).equals("VERSION")) {
							des = xpp.getAttributeValue(i);
						}
					}
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (reading != null && reading.toUpperCase(Locale.ENGLISH).equals("ERROR")) {
					InitError initu = new InitError();
					if (type.length() > 0) {
						initu.type = Integer.parseInt(type);
					} else {
						initu.type = -1;
					}
					initu.des = des;
					initu.value = value;
					mErrorMap.put(initu.type, initu);
				}
				reading = null;
			} else if (eventType == XmlPullParser.TEXT) {
				if (reading != null && reading.toUpperCase(Locale.ENGLISH).equals("URL")) {
					value = xpp.getText();
				}
			}
			eventType = xpp.next();
		}
		istr.close();
	}

	private void readUrl(Context mContext) throws Exception {
		InputStream istr = null;
		// 生产模式需要解密后使用
		if (!isDebug) {
			istr = mContext.getAssets().open("initFrame_p.xml");
			String readstr = getStreamString(istr);
			XMLDES des = new XMLDES();// 使用默认密钥
			try {
				istr = getStringStream(des.decrypt(readstr));// 解密后转成输入流
			} catch (Exception e) {
				throw new IllegalStateException("initFrame_p.xml Decrypt Error:" + e.getMessage());
			}
		} else {// 开发模式不需要解密，直接使用initFrame.xml
			istr = mContext.getAssets().open("initFrame.xml");
		}
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(istr, "UTF-8");

		int eventType = xpp.getEventType();
		String reading = "", name = "", type = "", value = "", classname = "", cachetime = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_DOCUMENT) {
			} else if (eventType == XmlPullParser.END_DOCUMENT) {
			} else if (eventType == XmlPullParser.START_TAG) {
				reading = xpp.getName();
				if (xpp.getName().toUpperCase(Locale.ENGLISH).equals("URL")) {
					name = type = value = classname = cachetime = "";
					for (int i = 0; i < xpp.getAttributeCount(); i++) {
						if (xpp.getAttributeName(i).toUpperCase(Locale.ENGLISH).equals("NAME")) {
							name = xpp.getAttributeValue(i);
						} else if (xpp.getAttributeName(i).toUpperCase(Locale.ENGLISH).equals("TYPE")) {
							type = xpp.getAttributeValue(i);
						} else if (xpp.getAttributeName(i).toUpperCase(Locale.ENGLISH).equals("CLASSNAME")) {
							classname = xpp.getAttributeValue(i);
						} else if (xpp.getAttributeName(i).toUpperCase(Locale.ENGLISH).equals("CACHETIME")) {
							cachetime = xpp.getAttributeValue(i);
						}
					}
				} else if (xpp.getName().toUpperCase(Locale.ENGLISH).equals("FRAMEINIT")) {
					for (int i = 0; i < xpp.getAttributeCount(); i++) {
						if (xpp.getAttributeName(i).toUpperCase(Locale.ENGLISH).equals("URI")) {
							this.mUri = xpp.getAttributeValue(i);
						} else if (xpp.getAttributeName(i).toUpperCase(Locale.ENGLISH).equals("URL")) {
							this.mUrl = xpp.getAttributeValue(i);
						} else if (xpp.getAttributeName(i).toUpperCase(Locale.ENGLISH).equals("LOADING")) {
							this.mLoading = xpp.getAttributeValue(i);
						} else if (xpp.getAttributeName(i).toUpperCase(Locale.ENGLISH).equals("ERROR")) {
							this.mError = xpp.getAttributeValue(i);
						} else if (xpp.getAttributeName(i).toUpperCase(Locale.ENGLISH).equals("LOG")) {
							this.mLog = xpp.getAttributeValue(i);
						} else if (xpp.getAttributeName(i).toUpperCase(Locale.ENGLISH).equals("TEMPPATH")) {
							this.mTemppath = xpp.getAttributeValue(i);
						} else if (xpp.getAttributeName(i).toUpperCase(Locale.ENGLISH).equals("PACKAGE")) {
							this.mPackage = xpp.getAttributeValue(i);
						} else if (xpp.getAttributeName(i).toUpperCase(Locale.ENGLISH).equals("CONNECTIONTIMEOUT")) {
							try {
								this.mSoTimeOut = Integer.parseInt(xpp.getAttributeValue(i));
							} catch (Exception e) {
								MLog.E("initFrame.xml 中 ConnectionTimeOut配置错误");
							}
						} else if (xpp.getAttributeName(i).toUpperCase(Locale.ENGLISH).equals("SOTIMEOUT")) {
							try {
								this.mSoTimeOut = Integer.parseInt(xpp.getAttributeValue(i));
							} catch (Exception e) {
								MLog.E("initFrame.xml 中 SoTimeOut配置错误");
							}
						}
					}
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (reading != null && reading.toUpperCase(Locale.ENGLISH).equals("URL")) {
					InitUrl initu = new InitUrl();
					if (type.length() > 0) {
						initu.type = Integer.parseInt(type);
					} else {
						initu.type = 0;
					}
					if (cachetime.length() > 0) {
						initu.cacheTime = Long.parseLong(cachetime);
					} else {
						initu.cacheTime = InitUrl.DEF_CACHETIME;
					}
					initu.className = classname;
					initu.url = value;
					mUrlMap.put(name, initu);
				}
				reading = null;
			} else if (eventType == XmlPullParser.TEXT) {
				if (reading != null && reading.toUpperCase(Locale.ENGLISH).equals("URL")) {
					value = xpp.getText();
				}
			}
			eventType = xpp.next();
		}
		istr.close();
	}

	/**
	 * 将一个输入流转化为字符串
	 */
	public static String getStreamString(InputStream tInputStream) {
		if (tInputStream != null) {
			try {
				BufferedReader tBufferedReader = new BufferedReader(new InputStreamReader(tInputStream));
				StringBuffer tStringBuffer = new StringBuffer();
				String sTempOneLine = new String("");
				while ((sTempOneLine = tBufferedReader.readLine()) != null) {
					tStringBuffer.append(sTempOneLine);
				}
				return tStringBuffer.toString();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 将一个字符串转化为输入流
	 */
	public static InputStream getStringStream(String sInputString) {
		if (sInputString != null && !sInputString.trim().equals("")) {
			try {
				ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(sInputString.getBytes());
				return tInputStringStream;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	public Object getJsonObject(String id) {
		InitUrl initUrl = mUrlMap.get(id);
		String clas = initUrl.className;
		if (clas == null || clas.length() == 0) {
			return null;
		} else {
			if (clas.startsWith(".")) {
				clas = mPackage + clas;
			}
			MLog.D(MLog.TAG_HTTP, "mode: " + clas);
			Class<?> cls;
			try {
				cls = Class.forName(clas);
				return cls.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				throw new IllegalAccessError("Api error,pls check initFrame");
			}
		}
	}

	/**
	 * 获取错误提示信息
	 * 
	 * @param type
	 * @return
	 */
	public InitError getError(int type) {
		if (mErrorMap.containsKey(type)) {
			return mErrorMap.get(type);
		}
		return null;
	}

	/**
	 * 拼接配置文件中请求接口
	 * 
	 * @param id
	 * @return
	 */
	public InitUrl getUrl(String id) {
		if (mUrlMap.containsKey(id)) {
			InitUrl url = new InitUrl();
			InitUrl nurl = mUrlMap.get(id);
			if (this.mUrl.startsWith("/")) {
				url.url = this.mUri + this.mUrl + nurl.url;
			} else {
				url.url = this.mUrl + nurl.url;
			}
			if (nurl.url.toUpperCase(Locale.ENGLISH).startsWith("HTTP://")
					|| nurl.url.toUpperCase(Locale.ENGLISH).startsWith("HTTPS://")) {
				url.url = nurl.url;
			}
			url.className = nurl.className;
			url.type = nurl.type;
			url.cacheTime = nurl.cacheTime;
			return url;
		}
		return null;
	}

	/**
	 * 获取请求的方法，默认get
	 * 
	 * @param id
	 * @return
	 */
	public HttpMethod getType(String id) {
		if (mUrlMap.containsKey(id)) {
			InitUrl initUrl = mUrlMap.get(id);
			if (initUrl.type == 1) {
				return HttpMethod.POST;
			}
		}
		return HttpMethod.GET;
	}

	/**
	 * 是否允许打印LOG日志
	 * 
	 * @return
	 */
	public boolean getLog() {
		if (this.mLog != null && this.mLog.toUpperCase(Locale.ENGLISH).equals("TRUE")) {
			return true;
		}
		return false;
	}

	/**
	 * 获取正在加载时候dialog页面
	 * 
	 * @param context
	 * @return
	 */
	public Loading getLoading(Context context) {
		if (mLoading != null && mLoading.length() > 0) {
			try {
				Class<?> clazz = Class.forName(mLoading);
				Constructor<?> clst = clazz.getConstructor(Context.class);
				Object obj = clst.newInstance(context);
				if (obj instanceof Loading) {
					return (Loading) obj;
				} else {
					MLog.E("not custom Loading");
					return new DialogLoading(context);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public MsgDialog getMsgDialog(Context context) {
		if (mError != null && mError.length() > 0) {
			try {
				Class<?> clazz = Class.forName(mError);
				Constructor<?> clst = clazz.getConstructor(Context.class);
				Object obj = clst.newInstance(context);
				if (obj instanceof MsgDialog) {
					return (MsgDialog) obj;
				} else {
					MLog.E("not custom Loading");
					return new DialogError(context);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getTempPath() {
		return mTemppath;
	}

}
