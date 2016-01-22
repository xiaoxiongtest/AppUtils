package com.apputils.example.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.apputils.example.R;
import com.apputils.example.baidu.listener.MyLocationListener;
import com.apputils.example.base.MyBaseAdapter;
import com.apputils.example.utils.common.LocationInfo;
import com.apputils.example.utils.common.LocationItemInfo;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.DistanceUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BaiduMapDemoActivity extends Activity
		implements OnClickListener, OnItemClickListener, OnGetPoiSearchResultListener {
	private LocationClient mLocationClient;
	private MyLocationReceiver receiver;
	private BaiduMap mBaiduMap;
	private MapView mMapView;
	private PoiSearch mPoiSearch;
	private List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
	private ListView mListview;
	private EditText mEdit;
	private Button mBtn;
	private LatLng position;
	private MyAdapter adpter;
	private int distance_limited;
	private double last_latitude = -1;
	private double last_lontitude = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		distance_limited = getIntent().getIntExtra("distance_limited", 1000);
		boolean allgesturesenabled = getIntent().getBooleanExtra("allgesturesenabled", true);
		// 定位
		mLocationClient = new LocationClient(this.getApplicationContext());
		// 百度地图
		MyLocationListener mMyLocationListener = new MyLocationListener(getApplicationContext());
		mLocationClient.registerLocationListener(mMyLocationListener);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_baidutest);
		initView();
		// 关闭一切手势操作
		UiSettings settings = mBaiduMap.getUiSettings();
		settings.setAllGesturesEnabled(allgesturesenabled);
		initLocation();
		mLocationClient.start();// 定位SDK
								// start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
		mLocationClient.requestLocation();
		// 对获取的结果进行监听
		receiver = new MyLocationReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.apputils.baidumap");
		registerReceiver(receiver, filter);
		// 搜索模块的初始化
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
	}

	public void onGetPoiResult(PoiResult result) {
		// 获取POI检索结果
		List<PoiInfo> allPoi = result.getAllPoi();
		if (allPoi != null) {
			for (int i = 0; i < allPoi.size(); i++) {
				Map<Object, Object> map = new HashMap<Object, Object>();
				map.put("title", allPoi.get(i).name);
				map.put("addr", allPoi.get(i).address);
				map.put("latitude", allPoi.get(i).location.latitude);
				map.put("lontitude", allPoi.get(i).location.longitude);
				list.add(map);
			}
			adpter.notifyDataSetChanged();
		} else {
			Toast.makeText(getApplicationContext(), "未搜索到相关信息", Toast.LENGTH_SHORT).show();
		}

	}

	public void onGetPoiDetailResult(PoiDetailResult result) {
		// 获取Place详情页检索结果
	}

	public void initView() {
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mListview = (ListView) findViewById(R.id.myListview);
		mListview.setOnItemClickListener(this);
		adpter = new MyAdapter(getApplicationContext(), list);
		mListview.setAdapter(adpter);
		mEdit = (EditText) findViewById(R.id.mEdit);
		mBtn = (Button) findViewById(R.id.mBtn);
		mBtn.setOnClickListener(this);
	}

	public class MyLocationReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			LocationInfo info = (LocationInfo) intent.getExtras().getSerializable("info");
			if (last_latitude != info.latitude || last_lontitude != info.lontitude) {
				list.clear();
				position = new LatLng(info.latitude, info.lontitude);
				// 定义地图状态
				MapStatus mMapStatus = new MapStatus.Builder().target(position).zoom(18).build();
				// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
				MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
				// 改变地图状态
				mBaiduMap.setMapStatus(mMapStatusUpdate);
				initOverlay(position);
				last_latitude = info.latitude;
				last_lontitude = info.lontitude;
				Map<Object, Object> map = new HashMap<Object, Object>();
				map.put("title", "[位置]");
				map.put("addr", info.addr);
				map.put("latitude", info.latitude);
				map.put("lontitude", info.lontitude);
				list.add(map);
				adpter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * 初始化覆盖物
	 */
	public void initOverlay(LatLng llA) {
		MarkerOptions ooA = new MarkerOptions().position(llA)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka)).zIndex(9).draggable(true);
		// 掉下动画
		// ooA.animateType(MarkerAnimateType.drop);
		mBaiduMap.addOverlay(ooA);
	}

	@Override
	protected void onDestroy() {
		mMapView.onDestroy();
		mPoiSearch.destroy();
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		mMapView.onResume();
		super.onResume();
	}

	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("gcj02");// 可选，默认gcj02，设置返回的定位结果坐标系，
		int span = 1000;
		option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIgnoreKillProcess(true);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		mLocationClient.setLocOption(option);
	}

	@Override
	protected void onStop() {
		mLocationClient.stop();
		super.onStop();
	}

	public class MyAdapter extends MyBaseAdapter<Map<Object, Object>> {

		public MyAdapter(Context mContext, List<Map<Object, Object>> als) {
			super(mContext, als);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.item_baidumap, null);
			}
			TextView title = (TextView) convertView.findViewById(R.id.title);
			TextView addr = (TextView) convertView.findViewById(R.id.addr);
			title.setText((String) als.get(position).get("title"));
			addr.setText((String) als.get(position).get("addr"));
			return convertView;
		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.mBtn) {
			//先定位到当前，然后在查询热点
			if(last_latitude!=-1&&last_lontitude!=-1){
				position = new LatLng(last_latitude, last_lontitude);
				// 定义地图状态
				MapStatus mMapStatus = new MapStatus.Builder().target(position).zoom(18).build();
				// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
				MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
				// 改变地图状态
				mBaiduMap.setMapStatus(mMapStatusUpdate);
			}
			String key = mEdit.getText().toString().trim();
			if (key == null || key.length() <= 0) {
				Toast.makeText(getApplicationContext(), "请输入有效的关键字", Toast.LENGTH_LONG).show();
				return;
			}
			if (position == null) {
				Toast.makeText(getApplicationContext(), "当前没有获取位置数据", Toast.LENGTH_LONG).show();
				return;
			}
			// 将以前的数据清除
			Map<Object, Object> map = list.get(0);
			list.clear();
			list.add(map);
			// 进行搜索
			// 搜索附近的poi
			mPoiSearch.searchNearby(new PoiNearbySearchOption().location(position).keyword(key).radius(1000).pageNum(0)
					.pageCapacity(10));
			// 将软件盘关闭
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm != null && mEdit != null) {
				imm.hideSoftInputFromWindow(mEdit.getWindowToken(), 0);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Map<Object, Object> map = list.get(arg2);
		double distance = DistanceUtil.getDistance(position,
				new LatLng((Double) map.get("latitude"), (Double) map.get("lontitude")));
		if (distance > distance_limited) {
			Toast.makeText(getApplicationContext(), "该位置已经超出当前位置" + distance_limited + "m", Toast.LENGTH_LONG).show();
			return;
		}
		Intent intent = new Intent();
		LocationItemInfo info = new LocationItemInfo((String) map.get("title"), (String) map.get("addr"),
				(Double) map.get("latitude"), (Double) map.get("lontitude"));
		Bundle mBundle = new Bundle();
		mBundle.putSerializable("iteminfo", info);
		intent.putExtras(mBundle);
		setResult(RESULT_OK, intent);
		finish();
	}

}
