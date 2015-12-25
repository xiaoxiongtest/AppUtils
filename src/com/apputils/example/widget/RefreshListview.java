package com.apputils.example.widget;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.apputils.example.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RefreshListview extends ListView implements OnScrollListener {
	private RotateAnimation upAnimation;
	private RotateAnimation downAnimation;
	private int mfirstVisibleItem = -1;
	private int headHight;
	private int downY = -1;
	// 下拉刷新
	private static final int PULL_REFRESH = 1;
	// 释放刷新
	private static final int RELEASE_REFRESH = 2;
	// 正在刷新
	private static final int IS_REFRESHING = 3;

	private int currentState = PULL_REFRESH;
	private LinearLayout refresh_header_view;
	private TextView state_text;
	private ImageView imageView;
	private ProgressBar mProgressBar;
	private TextView mTime;
	private OnRefreshListener onRefreshListener;
	private LinearLayout refresh_footer_root;
	private int footerHeight;
	private boolean isload;
	private boolean pullUpdate = true;
	private boolean pullMore = true;

	public RefreshListview(Context context, AttributeSet attrs) {
		this(context, attrs, 0);

	}

	public RefreshListview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initHeader();
		initFooter();
		// 滚动做监听
		setOnScrollListener(this);
	}

	public RefreshListview(Context context) {
		this(context, null);
	}

	private void initFooter() {
		View viewFooter = View.inflate(getContext(), R.layout.refresh_footer, null);
		refresh_footer_root = (LinearLayout) viewFooter.findViewById(R.id.refresh_footer_root);
		refresh_footer_root.measure(0, 0);
		footerHeight = refresh_footer_root.getMeasuredHeight();
		// 脚底的隐藏
		refresh_footer_root.setPadding(0, -footerHeight, 0, 0);
		this.addFooterView(viewFooter);
	}

	private void initHeader() {
		View headView = View.inflate(getContext(), R.layout.refresh_header, null);
		refresh_header_view = (LinearLayout) headView.findViewById(R.id.refresh_header_view);
		mProgressBar = (ProgressBar) headView.findViewById(R.id.progressbar);
		imageView = (ImageView) headView.findViewById(R.id.imageView);
		state_text = (TextView) headView.findViewById(R.id.state_text);
		mTime = (TextView) headView.findViewById(R.id.time);
		this.addHeaderView(headView);
		// 先将刷新头进行隐藏
		refresh_header_view.measure(0, 0);
		headHight = refresh_header_view.getMeasuredHeight();
		refresh_header_view.setPadding(0, -headHight, 0, 0);
		// 初始化动画
		initAnimation();
	}

	/**
	 * 设置下拉刷新是否可用，默认可用
	 * 
	 * @param pullUpdate
	 */
	public void setPullUpdateEnable(boolean pullUpdate) {
		this.pullUpdate = pullUpdate;
	}

	/**
	 * 设置上拉加载更多是否可用，默认可用
	 * 
	 * @param pullMore
	 */
	public void setPullMoreEnable(boolean pullMore) {
		this.pullMore = pullMore;
	}

	private void initAnimation() {
		upAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		upAnimation.setFillAfter(true);
		upAnimation.setDuration(300);
		downAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		downAnimation.setFillAfter(true);
		downAnimation.setDuration(300);
	}

	public void setStateOption() {
		switch (currentState) {
		case RELEASE_REFRESH:
			state_text.setText("释放刷新");
			imageView.startAnimation(upAnimation);
			break;
		case PULL_REFRESH:
			state_text.setText("下拉刷新");
			imageView.startAnimation(downAnimation);
			break;
		case IS_REFRESHING:
			state_text.setText("正在刷新");
			imageView.setVisibility(View.GONE);
			imageView.clearAnimation();
			mProgressBar.setVisibility(View.VISIBLE);
			mTime.setText(getTime());
			break;
		}
	}

	private String getTime() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (pullUpdate) {
				downY = (int) ev.getY();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (pullUpdate) {
				if (downY == -1) {
					downY = (int) ev.getY();
				}
				int moveY = (int) ev.getY();
				// 获取刷新头还隐藏的高度
				int padding = -headHight + moveY - downY;
				// padding>0 完整的拖拽出来,此时放手,刷新(释放刷新)
				// padding<0 没有完整的拖拽出来,此时防守,弹回,什么都不做(下拉刷新)

				// 在下拉动作中,才去判断是否完整的拖拽出来了
				if (moveY - downY > 0 && mfirstVisibleItem == 0) {
					if (padding > 0 && currentState == PULL_REFRESH) {
						// 变成释放刷新
						currentState = RELEASE_REFRESH;
						setStateOption();
					}

					if (padding < 0 && currentState == RELEASE_REFRESH) {
						// 变成下拉刷新
						currentState = PULL_REFRESH;
						setStateOption();
					}
					// 保证后续的ACTION_UP事件一定触发
					refresh_header_view.setPadding(0, padding, 0, 0);
					return true;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (pullUpdate) {
				downY = -1;
				if (currentState == RELEASE_REFRESH) {
					// 正在刷新操作
					currentState = IS_REFRESHING;
					setStateOption();
					// 刷新的时候完整显示刷新头
					refresh_header_view.setPadding(0, 0, 0, 0);
					// 回调(1,定义一个接口2,定义一个未实现(业务逻辑未知)的刷新方法,3,传递一个实现了接口的类对应的对象进来,4,调用刷新的方法)
					if (onRefreshListener != null) {
						onRefreshListener.pullDownRefresh();
					}
				}

				if (currentState == PULL_REFRESH) {
					// 界面弹回去
					refresh_header_view.setPadding(0, -headHight, 0, 0);
				}
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	// 刷新结束以后的ui处理
	public void onFinish() {
		if (currentState == IS_REFRESHING && pullUpdate) {
			// 状态的归位,变为默认的下拉刷新状态
			currentState = PULL_REFRESH;
			// 隐藏进度条
			mProgressBar.setVisibility(View.INVISIBLE);
			imageView.setVisibility(View.VISIBLE);
			state_text.setText("下拉刷新");
			refresh_header_view.setPadding(0, -headHight, 0, 0);
		}
		if (isload && pullMore) {
			isload = false;
			refresh_footer_root.setPadding(0, -footerHeight, 0, 0);
		}
	}

	public interface OnRefreshListener {
		// 未知业务逻辑的下拉刷新方法
		public void pullDownRefresh();

		// 未知业务逻辑的上拉加载放方法
		public void pullUpLoadMore();
	}

	public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
		this.onRefreshListener = onRefreshListener;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (pullMore) {
			// OnScrollListener.SCROLL_STATE_FLING 飞速滚动
			// OnScrollListener.SCROLL_STATE_TOUCH_SCROLL 慢慢的滚
			// OnScrollListener.SCROLL_STATE_IDLE 滚动状态改变的时候
			if (scrollState == OnScrollListener.SCROLL_STATE_FLING
					|| scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
				if ((getLastVisiblePosition() == getAdapter().getCount() - 1) && !isload) {
					isload = true;
					// UI显示加载更多的进度条,文字
					refresh_footer_root.setPadding(0, 0, 0, 0);
					// 真正的加载更多业务逻辑处理(回调)
					if (onRefreshListener != null) {
						onRefreshListener.pullUpLoadMore();
					}
				}
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// mfirstVisibleItem必须是0的时候,才可以准备去做刷新操作
		this.mfirstVisibleItem = firstVisibleItem;
	}

}
