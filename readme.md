# AppUtils
该项目是基于gson-2.2.4.jar和xUtils-2.6.14.jar的一些基本的封装
## 目录
 * [集成的步骤](#index)
 * [MActivity](#MActivity)
 * [RefreshListview](#RefreshListview)
 * [关于listview的两个BaseAdapter集成](#basepager)
 * [ViewPager的集成](#viewpager)
 * [SlideShowView 轮播图](#SlideShowView)
 * [AbImageUtil 图片处理类](#AbImageUtil)
 * [类似于QQ5.0侧滑效果	DragLayout，MyRelativeLayout](#qq)
 * [二维码生成和扫描](#qr)
 * [MyShareUtils 第三方分享  QQ,微信,新浪微博](#share)
 * [极光推送](#jg)
 * [百度地图](#bd)

## 集成的步骤
<span id="index"></span>
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
<span id="MActivity"></span>
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
<span id="RefreshListview"></span>
	
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

##关于listview的两个BaseAdapter集成
<span id="basepager"></span>
###MyBaseAdapter
用法如下：

	public class ListAdapter extends MyBaseAdapter<T>{
		public ListAdapter(Context mContext, List<T> als) {
			super(mContext, als);
		//初始化的操作,可以调用的参数如下
		//protected List<T> als;
	    //protected Context mContext;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 设置view和逻辑处理
			return null;
		}
	}
###MyHolderBaseAdapter&BaseHolder 
用法如下：

	public class MyAdapter extends MyHolderBaseAdapter<T> {
		public MyAdapter(Context mContext, List<T> list) {
			super(mContext, list);
		//初始化的操作,可以调用的参数如下
		//protected List<T> als;
	    //protected Context mContext;
		}

		@Override
		public BaseHolder<T> getHolder() {
			return new MyHolder(mContext);
		}
	}
	
	====================================================
	public class MyHolder extends BaseHolder<T> {
		private View view;

		public MyHolder(Context mContext) {
			super(mContext);
		}

		@Override
		public void refreshView() {
			Content data = getData();
			//控件实例化，并设置数据
			//TextView tv = (TextView) view.findViewById(R.id.myTextview);
			//tv.setText(data.str);
		}

		@Override
		public View initView() {
			//布局实例化
			//view = View.inflate(mContext, R.layout.item_text, null);
			return view;
		}
	}
##ViewPager的集成
<span id="viewpager"></span>

	viewpager = (ViewPager) findViewById(R.id.viewpager);
		List<BasePager> list = new ArrayList<BasePager>();
		list.add(new PagerOne(MainActivity.this));
		list.add(new PagerTwo(MainActivity.this));
		viewpager.setAdapter(new BaseViewPagerAdapter(list));
		//禁止viewpager滑动,如果不需要请删除
		viewpager.setOnTouchListener(new View.OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
	});
	=========================================================
	public class PagerOne extends BasePager {
	private TextView tv;

	//父类中有几个变量可以直接使用的：
	//this.context = context;
	//this.accessNetUtils= AccessNetUtils.getInstance(context);
	//view = initView();

	
		public PagerOne(MActivity context) {
			super(context);
		}

		@Override
		public View initView() {
			//ui的初始化
			View view = View.inflate(context,R.layout.pager_one , null);
			tv = (TextView) view.findViewById(R.id.one);
			return view;
		}

		@Override
		public void initData() {
			//逻辑的处理
			tv.setText("one");
		}
	}



##SlideShowView 轮播图
<span id="SlideShowView"></span>
	// imageUrls 所有图片资源的url地址
	// isAutoPlay 是否开启轮播
	slideshowView.setBitmapList(String[] imageUrls, boolean isAutoPlay)
##AbImageUtil 图片处理类
<span id="AbImageUtil"></span>
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
##类似于QQ5.0侧滑效果	DragLayout，MyRelativeLayout
<span id="qq"></span>
需要的jar包
 nineoldandroids-2.4.0.jar
	
布局文件

	<?xml version="1.0" encoding="utf-8"?>
	<com.apputils.example.widget.DragLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/dl"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="左侧背景" >

	    <RelativeLayout
	        android:layout_width="match_parent"
	        android:layout_height="match_parent" >
		<!--左侧的布局内容-->
	
	    </RelativeLayout>

	    <com.apputils.example.widget.MyRelativeLayout
	        android:layout_width="match_parent"
	        android:layout_height="match_parent"
	        android:background="右侧背景" >
	
	        <!--右侧的布局内容-->
	    </com.apputils.example.widget.MyRelativeLayout>

	</com.apputils.example.widget.DragLayout>

逻辑处理

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		...
		DragLayout dl = (DragLayout) findViewById(R.id.dl);
		dl.setDragListener(new DragListener() {
			@Override
			public void onOpen() {
			//开启时
			}

			@Override
			public void onClose() {
			//关闭时
			}

			@Override
			public void onDrag(float percent) {
			//滑动时
			}
		});
	}
## 二维码生成和扫描
<span id="qr"></span>

### QRUtils
	//生成有logo的二维码
	createQRImageWithLogo();
	//生成普通的二维码
	createQRImage();
### 二维码扫描

	权限
	<uses-permission android:name="android.permission.CAMERA"></uses-permission>
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
	<uses-feature android:name="android.hardware.camera" />
	<uses-feature android:name="android.hardware.camera.autofocus" />
	<uses-permission android:name="android.permission.VIBRATE"/>
  	<uses-permission android:name="android.permission.FLASHLIGHT"/>

	<activity
	    android:name="com.apputils.example.activity.qr.CaptureActivity"
	    android:configChanges="orientation|keyboardHidden"
	    android:screenOrientation="landscape"
	    android:theme="@android:style/Theme.NoTitleBar.Fullscreen"
	    android:windowSoftInputMode="stateAlwaysHidden" >
	</activity>

	跳转的时候需要携带的数据
	Intent intent = new Intent();
	intent.setClass(MainActivity.this, CaptureActivity.class);
	intent.putExtra("getPhoto", false); //是否显示相册获取二维码的按钮，默认不显示
	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
	前一个activity中的回调
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if(resultCode == RESULT_OK){
				Bundle bundle = data.getExtras();
				//显示扫描到的内容
				String result = bundle.getString("result")
				//显示拍的二维码的图片
				Bitmap bitmap = (Bitmap) data.getParcelableExtra("bitmap");
			}
			break;
		}
    }


##MyShareUtils 第三方分享  QQ,微信,新浪微博
<span id="share"></span>

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
<span id="jg"></span>

需要的jar包
	
	armeabi/libjpush205.so
	armeabi-v7a/libjpush205.so
	libs/jpush-android-2.0.5.jar

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
##百度地图
<span id="bd"></span>
需要的jar包

	armeabi/libBaiduMapSDK_base_v3_6_1.so
	armeabi/libBaiduMapSDK_map_v3_6_1.so
	armeabi-v7a/libBaiduMapSDK_base_v3_6_1.so
	armeabi-v7a/libBaiduMapSDK_map_v3_6_1.so
	libs/baidumapapi_base_v3_6_1.jar
	libs/baidumapapi_map_v3_6_1.jar

清单文件中的配置

	<meta-data  
        android:name="com.baidu.lbsapi.API_KEY"  
        android:value="开发者 key" /> 
需要的权限

	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
	<uses-permission android:name="android.permission.INTERNET"/>
	<uses-permission android:name="com.android.launcher.permission.READ_SETTINGS" />
	<uses-permission android:name="android.permission.WAKE_LOCK"/>
	<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
	<uses-permission android:name="android.permission.GET_TASKS" />
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
	<uses-permission android:name="android.permission.WRITE_SETTINGS" />

如果有需要展示的控件

	<com.baidu.mapapi.map.MapView  
	    android:id="@+id/bmapView"  
	    android:layout_width="fill_parent"  
	    android:layout_height="fill_parent"  
	    android:clickable="true" />
sdk的初始化

	//在使用SDK各组件之前初始化context信息，传入ApplicationContext  
    //注意该方法要再setContentView方法之前实现  
    SDKInitializer.initialize(getApplicationContext()); 

创建地图activity

	public class TestActivity extends Activity {  
    public MapView mMapView;  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.activity_test);  
        //获取地图控件引用  
        mMapView = (MapView) findViewById(R.id.bmapView);  
    }  
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        mMapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        mMapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        mMapView.onPause();  
        }  
    }
###定位

其他功能参考百度地图开发文档