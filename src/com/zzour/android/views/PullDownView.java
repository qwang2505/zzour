package com.zzour.android.views;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zzour.android.R;
import com.zzour.android.views.ScrollOverListView.OnScrollOverListener;

public class PullDownView extends LinearLayout implements OnScrollOverListener{
	private static final String TAG = "ZZOUR";
	
	private static final int START_PULL_DEVIATION = 50;
	private static final int AUTO_INCREMENTAL = 10;
	
	private static final int WHAT_DID_LOAD_DATA = 1;
	private static final int WHAT_ON_REFRESH = 2;
	private static final int WHAT_DID_REFRESH = 3;
	private static final int WHAT_SET_HEADER_HEIGHT = 4;
	private static final int WHAT_DID_MORE = 5;
	
	private static final int DEFAULT_HEADER_VIEW_HEIGHT = 105;
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
	
	private View mHeaderView;
	private LayoutParams mHeaderViewParams;	
	private TextView mHeaderViewDateView;
	private TextView mHeaderTextView;
	private ImageView mHeaderArrowView;
	private View mHeaderLoadingView;
	private View mFooterView;
	private TextView mFooterTextView;
	private View mFooterLoadingView;
	private ScrollOverListView mListView;
	
	private OnPullDownListener mOnPullDownListener;
	private RotateAnimation mRotateOTo180Animation;
	private RotateAnimation mRotate180To0Animation;
	
	private int mHeaderIncremental;
	private float mMotionDownLastY;
	
	private boolean mIsDown;
	private boolean mIsRefreshing;
	private boolean mIsFetchMoreing;
	private boolean mIsPullUpDone;
	private boolean mEnableAutoFetchMore;
	
	private static final int HEADER_VIEW_STATE_IDLE = 0;
	private static final int HEADER_VIEW_STATE_NOT_OVER_HEIGHT = 1;
	private static final int HEADER_VIEW_STATE_OVER_HEIGHT = 2;
	private int mHeaderViewState = HEADER_VIEW_STATE_IDLE;


	public PullDownView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initHeaderViewAndFooterViewAndListView(context);
	}

	public PullDownView(Context context) {
		super(context);
		initHeaderViewAndFooterViewAndListView(context);
	}
	
	public interface OnPullDownListener {
		void onRefresh();
		void onMore();
	}
	
	public void notifyDidLoad() {
		mUIHandler.sendEmptyMessage(WHAT_DID_LOAD_DATA);
	}
	
	public void notifyDidRefresh() {
		mUIHandler.sendEmptyMessage(WHAT_DID_REFRESH);
	}

	public void notifyDidMore() {
		mUIHandler.sendEmptyMessage(WHAT_DID_MORE);
	}

	public void setOnPullDownListener(OnPullDownListener listener){
		mOnPullDownListener = listener;
	}

	public ListView getListView(){
		return mListView;
	}

	public void enableAutoFetchMore(boolean enable, int index){
		if(enable){
			mListView.setBottomPosition(index);
			mFooterLoadingView.setVisibility(View.VISIBLE);
		}else{
			mFooterTextView.setText(R.string.my_order_load_more_text);
			mFooterLoadingView.setVisibility(View.GONE);
		}
		mEnableAutoFetchMore = enable;
	}

	private void initHeaderViewAndFooterViewAndListView(Context context){
		setOrientation(LinearLayout.VERTICAL);
		mHeaderView = LayoutInflater.from(context).inflate(R.layout.pulldown_header, null);
		mHeaderViewParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		addView(mHeaderView, 0, mHeaderViewParams);
		
		mHeaderTextView = (TextView) mHeaderView.findViewById(R.id.pulldown_header_text);
		mHeaderArrowView = (ImageView) mHeaderView.findViewById(R.id.pulldown_header_arrow);
		mHeaderLoadingView = mHeaderView.findViewById(R.id.pulldown_header_loading);
		
		mRotateOTo180Animation = new RotateAnimation(0, 180, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateOTo180Animation.setDuration(250);
		mRotateOTo180Animation.setFillAfter(true);
		
		mRotate180To0Animation = new RotateAnimation(180, 0, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		mRotate180To0Animation.setDuration(250);
		mRotate180To0Animation.setFillAfter(true);

		mFooterView = LayoutInflater.from(context).inflate(R.layout.pulldown_footer, null);
		mFooterTextView = (TextView) mFooterView.findViewById(R.id.pulldown_footer_text);
		mFooterLoadingView = mFooterView.findViewById(R.id.pulldown_footer_loading);
		mFooterView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!mIsFetchMoreing){
					mIsFetchMoreing = true;
					mFooterLoadingView.setVisibility(View.VISIBLE);
					mOnPullDownListener.onMore();
				}
			}
		});

		mListView = new ScrollOverListView(context);
		mListView.setOnScrollOverListener(this);
		mListView.setCacheColorHint(0);
		addView(mListView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		mOnPullDownListener = new OnPullDownListener() {
			@Override
			public void onRefresh() {}
			@Override
			public void onMore() {}
		};
	}

	private void checkHeaderViewState(){
		if(mHeaderViewParams.height >= DEFAULT_HEADER_VIEW_HEIGHT){
			if(mHeaderViewState == HEADER_VIEW_STATE_OVER_HEIGHT) return;
			mHeaderViewState = HEADER_VIEW_STATE_OVER_HEIGHT;
			mHeaderTextView.setText(R.string.my_order_pull_tip_2);
			mHeaderArrowView.startAnimation(mRotateOTo180Animation);
		}else{
			if(mHeaderViewState == HEADER_VIEW_STATE_NOT_OVER_HEIGHT
					|| mHeaderViewState == HEADER_VIEW_STATE_IDLE) return;
			mHeaderViewState = HEADER_VIEW_STATE_NOT_OVER_HEIGHT;
			mHeaderTextView.setText(R.string.my_order_pull_tip_1);
			mHeaderArrowView.startAnimation(mRotate180To0Animation);
		}
	}
	
	private void setHeaderHeight(final int height){
		mHeaderIncremental = height;
		mHeaderViewParams.height = height;
		mHeaderView.setLayoutParams(mHeaderViewParams);
	}

	class HideHeaderViewTask extends TimerTask{
		@Override
		public void run() {
			if(mIsDown) {
				cancel();
				return;
			}
			mHeaderIncremental -= AUTO_INCREMENTAL;
			if(mHeaderIncremental > 0){
				mUIHandler.sendEmptyMessage(WHAT_SET_HEADER_HEIGHT);
			}else{
				mHeaderIncremental = 0;
				mUIHandler.sendEmptyMessage(WHAT_SET_HEADER_HEIGHT);
				cancel();
			}
		}
	}

	class ShowHeaderViewTask extends TimerTask{

		@Override
		public void run() {
			if(mIsDown) {
				cancel();
				return;
			}
			mHeaderIncremental -= AUTO_INCREMENTAL;
			if(mHeaderIncremental > DEFAULT_HEADER_VIEW_HEIGHT){
				mUIHandler.sendEmptyMessage(WHAT_SET_HEADER_HEIGHT);
			}else{
				mHeaderIncremental = DEFAULT_HEADER_VIEW_HEIGHT;
				mUIHandler.sendEmptyMessage(WHAT_SET_HEADER_HEIGHT);
				if(!mIsRefreshing){
					mIsRefreshing = true;
					mUIHandler.sendEmptyMessage(WHAT_ON_REFRESH);
				}
				cancel();
			}
		}
	}


	private Handler mUIHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case WHAT_DID_LOAD_DATA:{
					mHeaderViewParams.height = 0;
					mHeaderLoadingView.setVisibility(View.GONE);
					mHeaderTextView.setText(R.string.my_order_pull_tip_1);
					mHeaderViewDateView = (TextView) mHeaderView.findViewById(R.id.pulldown_header_date);
					mHeaderViewDateView.setVisibility(View.VISIBLE);
					mHeaderViewDateView.setText(PullDownView.this.getResources().getText(R.string.my_order_pull_tip_3) + dateFormat.format(new Date(System.currentTimeMillis())));
					mHeaderArrowView.setVisibility(View.VISIBLE);
					showFooterView();
					return;
				}
			
				case WHAT_ON_REFRESH:{
					mHeaderArrowView.clearAnimation();
					mHeaderArrowView.setVisibility(View.INVISIBLE);
					mHeaderLoadingView.setVisibility(View.VISIBLE);
					mOnPullDownListener.onRefresh();					
					return;
				}
				
				case WHAT_DID_REFRESH :{
					mIsRefreshing = false;
					mHeaderViewState = HEADER_VIEW_STATE_IDLE;
					mHeaderArrowView.setVisibility(View.VISIBLE);
					mHeaderLoadingView.setVisibility(View.GONE);
					mHeaderViewDateView.setText(PullDownView.this.getResources().getText(R.string.my_order_pull_tip_3) + dateFormat.format(new Date(System.currentTimeMillis())));
					setHeaderHeight(0);
					showFooterView();
					return;
				}
				
				case WHAT_SET_HEADER_HEIGHT :{
					setHeaderHeight(mHeaderIncremental);
					return;
				}
				
				case WHAT_DID_MORE :{
					mIsFetchMoreing = false;
					mFooterTextView.setText(R.string.my_order_load_more_text);
					mFooterLoadingView.setVisibility(View.GONE);
				}
			}
		}
		
	};

	private void showFooterView(){
		if(mListView.getFooterViewsCount() == 0 && isFillScreenItem()){
			mListView.addFooterView(mFooterView);
			mListView.setAdapter(mListView.getAdapter());
		}
	}

	private boolean isFillScreenItem(){
		final int firstVisiblePosition = mListView.getFirstVisiblePosition();
		final int lastVisiblePostion = mListView.getLastVisiblePosition() - mListView.getFooterViewsCount();
		final int visibleItemCount = lastVisiblePostion - firstVisiblePosition + 1;
		final int totalItemCount = mListView.getCount() - mListView.getFooterViewsCount();
		if (mListView.getCount() == 0) return false;
		
		//if(visibleItemCount < totalItemCount) return true;
		return true;
	}

	@Override
	public boolean onListViewTopAndPullDown(int delta) {
		if(mIsRefreshing || mListView.getCount() - mListView.getFooterViewsCount() == 0) return false;
		
		int absDelta = Math.abs(delta);
		final int i = (int) Math.ceil((double)absDelta / 2);
		
		mHeaderIncremental += i;
		if(mHeaderIncremental >= 0){ // && mIncremental <= mMaxHeight
			setHeaderHeight(mHeaderIncremental);
			checkHeaderViewState();
		}
		return true;
	}

	@Override
	public boolean onListViewBottomAndPullUp(int delta) {
		if(!mEnableAutoFetchMore || mIsFetchMoreing) return false;
		if(isFillScreenItem()){
			mIsFetchMoreing = true;
			mFooterTextView.setText(R.string.my_order_load_more_text_2);
			mFooterLoadingView.setVisibility(View.VISIBLE);
			mOnPullDownListener.onMore();
			return true;
		}
		return false;
	}

	@Override
	public boolean onMotionDown(MotionEvent ev) {
		mIsDown = true;
		mIsPullUpDone = false;
		mMotionDownLastY = ev.getRawY();
		return false;
	}

	@Override
	public boolean onMotionMove(MotionEvent ev, int delta) {
		if(mIsPullUpDone) return true;
		
		final int absMotionY = (int) Math.abs(ev.getRawY() - mMotionDownLastY);
		if(absMotionY < START_PULL_DEVIATION) return true;
		
		final int absDelta = Math.abs(delta);
		final int i = (int) Math.ceil((double)absDelta / 2);
		
		if(mHeaderViewParams.height > 0 && delta < 0){
			mHeaderIncremental -= i;
			if(mHeaderIncremental > 0){
				setHeaderHeight(mHeaderIncremental);
				checkHeaderViewState();
			}else{
				mHeaderViewState = HEADER_VIEW_STATE_IDLE;
				mHeaderIncremental = 0;
				setHeaderHeight(mHeaderIncremental);
				mIsPullUpDone = true;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean onMotionUp(MotionEvent ev) {
		mIsDown = false;
		if(mHeaderViewParams.height > 0){
			int x = mHeaderIncremental - DEFAULT_HEADER_VIEW_HEIGHT;
			Timer timer = new Timer(true);
			if(x < 0){
				timer.scheduleAtFixedRate(new HideHeaderViewTask(), 0, 10);
			}else{
				timer.scheduleAtFixedRate(new ShowHeaderViewTask(), 0, 10);
			}
			return true;
		}
		return false;
	}

}
