# AppUtils
该项目是基于gson-2.2.4.jar和xUtils-2.6.14.jar的一些基本的封装
## 集成的步骤
1. 将github上的源码下载下来导入项目中，并让所开发的项目进行依赖（该步骤就不介绍了，百度上有很多）
2. 创建一个类继承Application，并在oncreate方法中加入以下代码

		// 设置模式开发模式true,生产模式false
		// 开发模式读取文件initFrame.xml，生产模式读取文件initFrame_p.xml
		InitConfig.isDebug = true;
		//设置默认参数，如果有就设置，如果没有就不要设置
		setAutoParams();
		//访问网络框架的初始化
		MHttpUtils.init(getApplicationContext());
		=====================================
		// 设置默认参数（这是例子）
		public void setAutoParams() {
		MHttpUtils.AUTOPARMS.put("platform", "Android");
		MHttpUtils.AUTOPARMS.put("version", "14");
		}
		=======================================
		清单文件中需要设置application中的name
		<application
	        android:name="com.example.my.MyApplication"	
	        android:allowBackup="true"
	        android:icon="@drawable/ic_launcher"
	        android:label="@string/app_name"
	        android:theme="@style/AppTheme" >
	        <activity
	            android:name=".MainActivity"
	            android:label="@string/app_name"
	            android:screenOrientation="portrait" >
	 			<intent-filter>
	                <action android:name="android.intent.action.MAIN" />
	                <category android:name="android.intent.category.LAUNCHER" />
	            </intent-filter>
	        </activity>
	    </application>

3.在assets文件中创建一个文件initFrame.xml,格式如下：

	<?xml version="1.0" encoding="UTF-8"?>
   	    <!-- 
	    	error 需要显示的系统报错的界面，一般为dialog
	    	loading 正在请求网络的时候显示的界面，一般为dialog
	    	log 日志是否显示
	    	package 以下访问网络需要拼接的包名
	    	uri 主机名和端口号
	    	url 路径
	    	temppath 临时文件路径
	 	-->
	<FrameInit 
	    error="" 
	    loading=""
	    log="true"
	    package="com.example.my.jsonclass"
	    uri="http://10.0.7.73:8080"
	    url="/MyStore/servlet"
	    temppath="">
	    <!-- 
	    	有返回值，添加classname,无返回值，则不需要添加classname
	    	type  0 为get请求 1为post请求
	    	cachetime 缓存时间
	    	classname 实例
	     -->
    
	    <url name="MBAppVersoin" 		type="0" 	cachetime="" classname=".JsonAppUpdata">/NewAppInfo</url>
	    <url name="MBListview" 		    type="0" 	cachetime="" classname=".JsonListview">/MyListviewTest</url>
	</FrameInit>
4.如果initFrame.xml文件是需要加密，则将该文件与加密.jar放在同一文件夹下,单机加密.bat，将生成的initFrame_p.xml放到assets文件夹下，将1中的InitConfig.isDebug = false;

5.根据上面文件中package+classname等于json的实例类的路径，如果不是json数据classname中就以""表示

6.自定义错误码assets/errorInfo.xml
<?xml version="1.0" encoding="UTF-8"?>

	<ErrorInfo version="1">
	    <error des="原因未知"				type="-1"  title="错误">原因未知</error>
	    <error des="没有网络"  				type="100" title="警告">网络连接失败，请检查网络</error>
	    <error des="未登录"   				type="101" title="警告">当前未登录，请登录</error>
	    <error des="用户名或密码错误"  		type="102" title="警告">用户名或密码错误，请检查</error>
	    <error des="json解析错误"  			type="103" title="错误">json解析错误，请联系客服</error>
	</ErrorInfo>

7.需要的权限

    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />


##MActivity 
集成访问网络获取数据的动画的dialog

下载文件显示进度的progressbar和notification

访问网络获取的数据调用accessNet方法,然后覆写disResposeMsg方法，在对获取的数据进行下一步的处理

	accessNet("MBListview", new String[][] { { "page", currentPage + "" } });
		
		// id initFrame.xml该接口的名称
		// obj 获取的数据， 如果是json类型的会返回一个对象，如果不是则是一个字符串
		// type 返回的数据类型 
			//json数据但是以[]开始的 ARRAY_OBJECT
			//json数据但是以{}开始的 SINGLE_OBJECT
			//不是json数据 SINGLE_STRING
		public void disResposeMsg(String id, Object obj, int type) {
		//获取数据成功进行处理
	}

##RefreshListview 下拉刷新，上拉加载listview
	
	listview.setOnRefreshListener(new OnRefreshListener() {
		@Override
		public void pullUpLoadMore() {
			//上拉加载的监听
		}

		@Override
		public void pullDownRefresh() {
			//下拉刷新的监听
		}
	});
	//设置上拉加载和下拉刷新是否可用
	listview.setPullMoreEnable(false);
	listview.setPullUpdateEnable(false);
	//监听事件处理结束，动画恢复需要调用的方法
	listview.onFinish();

##SlideShowView 轮播图
	// imageUrls 所有图片资源的url地址
	// isAutoPlay 是否开启轮播
	slideshowView.setBitmapList(String[] imageUrls, boolean isAutoPlay)
##AbImageUtil 图片处理类
图片的缩放，裁剪，圆边，旋转

	//访问网络获取bitmap类型的图片资源
	AbImageUtil.getInstance(getApplicationContext()).getBitmapFromUrl("http://image.zcool.com.cn/59/54/m_1303967870670.jpg", new ImageCallBack() {
					@Override
					public void onLoadFailed(String arg1) {
						//图片下载失败
					}
					
					@Override
					public void onLoadCompleted(Bitmap bm) {
						//图片下载成功
					}
				});
##MyShareUtils 第三方分享  QQ,微信,新浪微博

	需要的jar包
	libammsdk.jar  //微信分享
	mta-sdk-1.6.2.jar //QQ分享
	open_sdk_r5509.jar //QQ分享
	weibosdkcore.jar  //新浪微博
### 第三方账号的配置信息，放在assets/third.xml 规范如下

	<?xml version="1.0" encoding="UTF-8"?>
	<!--
		qq_appid QQ分享的appid
		sina_appkey 新浪分享的appkey
		sina_redirect_url 新浪分享的回调
		sina_scope 新浪的授权
		wx_appid 微信分享的appid
		
		qq_enable="true" QQ分享是否可用
		sina_enable="true" sina分享是否可用
		wx_friendcricle_enable="true" 微信朋友圈是否可用
		wx_friend_enable="true" 微信朋友是否可用
 	-->
	<ThirdKey qq_appid=""
	    	  sina_appkey=""
	    	  wx_appid=""
	    	  qq_enable="" 
			  sina_enable="" 
			  wx_friendcricle_enable=""
		      wx_friend_enable=""
	    	  >
	</ThirdKey>

###QQ分享
清单文件中需要的配置

    <activity
        android:name="com.tencent.tauth.AuthActivity"
        android:launchMode="singleTask"
        android:noHistory="true" >
        <intent-filter>
            <action android:name="android.intent.action.VIEW" />

            <category android:name="android.intent.category.DEFAULT" />
            <category android:name="android.intent.category.BROWSABLE" />
			<!--tencent+appid-->
            <data android:scheme="tencent22222222" />
        </intent-filter>
    </activity>
    <activity
        android:name="com.tencent.connect.common.AssistActivity"
        android:configChanges="orientation|keyboardHidden|screenSize"
        android:theme="@android:style/Theme.Translucent.NoTitleBar" />
###新浪分享

	<activity
            android:name="com.sina.weibo.sdk.component.WeiboSdkBrowser"
            android:configChanges="keyboardHidden|orientation"
            android:exported="false"
            android:windowSoftInputMode="adjustResize" >
    </activity>
###微信分享

###分享

	shareUtils =  MyShareUtils.getInstance(TestActivity.this,savedInstanceState);
	ShareMode mode = new ShareMode();
	//设置一些要分享的内容
	mode.title = "标题";
	mode.content = "内容";
	mode.targetUrl = "跳转的地址";
	mode.localImgUrl = new MCusImageView(getApplicationContext(), R.drawable.appmain_subject_1).getPath();
	mode.imgUrl = "http://img3.cache.netease.com/photo/0005/2013-03-07/8PBKS8G400BV0005.jpg";
	mode.des="这是描述信息";
	mode.thumbImage=new MCusImageView(getApplicationContext(), R.drawable.ic_launcher).getBitmap();
	shareUtils = MyShareUtils.getInstance(ViewpagerTest.this,savedInstanceState);
	shareUtils.setShareMode(mode);
	//显示分享的popupwindow
	LinearLayout lay = (LinearLayout) findViewById(R.id.lay);
	shareUtils.showPopupWindow(lay);

##极光推送
清单文件中需要的权限

	<permission
        android:name="com.example.my.permission.JPUSH_MESSAGE"
        android:protectionLevel="signature" />
    <uses-permission android:name="com.example.my.permission.JPUSH_MESSAGE" />
    <uses-permission android:name="android.permission.RECEIVE_USER_PRESENT" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.WRITE_SETTINGS" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_LOCATION_EXTRA_COMMANDS" />
    <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
 
需要文件配置
	
	<activity
            android:name="cn.jpush.android.ui.PushActivity"
            android:configChanges="orientation|keyboardHidden"
            android:exported="false"
            android:theme="@android:style/Theme.NoTitleBar" >
            <intent-filter>
                <action android:name="cn.jpush.android.ui.PushActivity" />

                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="应用包名" />
            </intent-filter>
        </activity>

        <!-- Required SDK 核心功能 -->
        <!-- option since 2.0.5 可配置PushService，DaemonService,PushReceiver,AlarmReceiver的android:process参数 将JPush相关组件设置为一个独立进程 -->
        <!-- 如：android:process=":remote" -->
        <service
            android:name="cn.jpush.android.service.PushService"
            android:enabled="true"
            android:exported="false" >
            <intent-filter>
                <action android:name="cn.jpush.android.intent.REGISTER" />
                <action android:name="cn.jpush.android.intent.REPORT" />
                <action android:name="cn.jpush.android.intent.PushService" />
                <action android:name="cn.jpush.android.intent.PUSH_TIME" />
            </intent-filter>
        </service>
        <!-- Required SDK核心功能 -->
        <service
            android:name="cn.jpush.android.service.DownloadService"
            android:enabled="true"
            android:exported="false" >
        </service>
        <!-- Required SDK 核心功能 since 1.8.0 -->
        <service
            android:name="cn.jpush.android.service.DaemonService"
            android:enabled="true"
            android:exported="true" >
            <intent-filter>
                <action android:name="cn.jpush.android.intent.DaemonService" />

                <category android:name="应用包名" />
            </intent-filter>
        </service>
        <!-- Required SDK核心功能 -->
        <receiver
            android:name="cn.jpush.android.service.PushReceiver"
            android:enabled="true"
            android:exported="false" >
            <intent-filter android:priority="1000" >
                <action android:name="cn.jpush.android.intent.NOTIFICATION_RECEIVED_PROXY" /> <!-- Required 显示通知栏 -->
                <category android:name="应用包名" />
            </intent-filter>
            <intent-filter>
                <action android:name="android.intent.action.USER_PRESENT" />
                <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
            </intent-filter>
            <!-- Optional -->
            <intent-filter>
                <action android:name="android.intent.action.PACKAGE_ADDED" />
                <action android:name="android.intent.action.PACKAGE_REMOVED" />

                <data android:scheme="package" />
            </intent-filter>
        </receiver>

        <!-- Required SDK核心功能 -->
        <receiver android:name="cn.jpush.android.service.AlarmReceiver" />

        <!-- User defined. 用户自定义的广播接收器 -->
        <receiver
            android:name="当前应用中对jpush消息进行的监听"
            android:enabled="true" >
            <intent-filter>
                <action android:name="cn.jpush.android.intent.REGISTRATION" /> <!-- Required 用户注册SDK的intent -->
                <action android:name="cn.jpush.android.intent.UNREGISTRATION" />
                <action android:name="cn.jpush.android.intent.MESSAGE_RECEIVED" /> <!-- Required 用户接收SDK消息的intent -->
                <action android:name="cn.jpush.android.intent.NOTIFICATION_RECEIVED" /> <!-- Required 用户接收SDK通知栏信息的intent -->
                <action android:name="cn.jpush.android.intent.NOTIFICATION_OPENED" /> <!-- Required 用户打开自定义通知栏的intent -->
                <action android:name="cn.jpush.android.intent.ACTION_RICHPUSH_CALLBACK" /> <!-- Optional 用户接受Rich Push Javascript 回调函数的intent -->
                <action android:name="cn.jpush.android.intent.CONNECTION" /> <!-- 接收网络变化 连接/断开 since 1.6.3 -->
                <category android:name="应用包名" />
            </intent-filter>
        </receiver>

        <!-- Required . Enable it you can get statistics data with channel -->
        <meta-data
            android:name="JPUSH_CHANNEL"
            android:value="developer-default" />
        <meta-data
            android:name="JPUSH_APPKEY"
            android:value="appkey" /> <!-- </>值来自开发者平台取得的AppKey -->

JPush的初始化 Application
	
	//jpush
	JPushInterface.setDebugMode(true);
    JPushInterface.init(this);
创建一个类集成BroadcastReceiver

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		MLog.D(MLog.TAG_JPUSH, "onReceive - " + intent.getAction());

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			System.out.println("id:" + bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID));
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
			System.out.println("收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
			System.out.println("附加参数：" + bundle.getString(JPushInterface.EXTRA_EXTRA));
			System.out.println("消息的id" + bundle.getString(JPushInterface.EXTRA_MSG_ID));
			// 自定义消息不会展示在通知栏，完全要开发者写代码去处理
		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
			System.out.println("收到了通知");
			System.out.println("html path:" + bundle.getString(JPushInterface.EXTRA_RICHPUSH_HTML_PATH));
			System.out.println("html res:" + bundle.getString(JPushInterface.EXTRA_RICHPUSH_HTML_RES));
			// 在这里可以做些统计，或者做些其他工作
		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			System.out.println("用户点击打开了通知");
			// 在这里可以自己写代码去定义用户点击后的行为
			System.out.println("附加参数：" + bundle.getString(JPushInterface.EXTRA_EXTRA));
			System.out.println("通知的标题：" + bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE));
			System.out.println("通知的内容：" + bundle.getString(JPushInterface.EXTRA_ALERT));
			System.out.println("消息的id:" + bundle.getString(JPushInterface.EXTRA_MSG_ID));
			Intent i = new Intent(context, ViewpagerTest.class); // 自定义打开的界面
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		} else {
			MLog.D(MLog.TAG_JPUSH, "Unhandled intent - " + intent.getAction());
		}
	}
