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
	
	