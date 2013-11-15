package com.zzour.android;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.zzour.android.views.PullDownView.OnPullDownListener;
import com.zzour.android.views.PullDownView;
import com.zzour.android.R;
import com.zzour.android.base.BaseActivity;
import com.zzour.android.models.OrderSummary;
import com.zzour.android.models.User;
import com.zzour.android.network.api.AcountApi;
import com.zzour.android.network.api.MyOrderApi;
import com.zzour.android.network.api.results.OrderListResult;
import com.zzour.android.settings.LocalPreferences;
import com.zzour.android.utils.ActivityTool;
import com.zzour.android.views.adapters.OrderListItemsAdapter;

public class FinishedOrderListActivity extends BaseActivity implements OnPullDownListener, OnItemClickListener{
	
	private static final int WHAT_DID_LOAD_DATA = 0;
	private static final int WHAT_DID_REFRESH = 1;
	private static final int WHAT_DID_MORE = 2;
	
	private OrderListItemsAdapter mAdapter = null;
	
	private Handler mLoadMoreHandler = new Handler();
	
	private PullDownView mPullDownView = null;
	private ListView mListView;
	
	private int page = 1;
	private final int count = 6;
	private boolean inited = false;
	
	private static final String STATUS = "finished";
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		setContentView(R.layout.finished_order_list);
		
		mPullDownView = (PullDownView) findViewById(R.id.pull_down_view);
		mPullDownView.setOnPullDownListener(this);
		mListView = mPullDownView.getListView();
		
		mListView.setOnItemClickListener(this);
		mAdapter = new OrderListItemsAdapter(this, R.drawable.logo);
        mListView.setAdapter(mAdapter);
        
        //mPullDownView.enableAutoFetchMore(true, 1);
        mPullDownView.enableAutoFetchMore(false, 1);
        
		if (!LocalPreferences.authed(this)){
			new Thread(new Runnable() {
				@Override
				public void run() {
					Message msg = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
					msg.sendToTarget();
				}
			}).start();
			return;
		}
        
        loadData();
        this.inited = true;
	}
	
	@Override
	public void onResume(){
		if (LocalPreferences.authed(this) && !this.inited){
			loadData();
		}
		super.onResume();
	}
	
	private void loadData(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				loadMoreData(true);
				Message msg = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
				msg.sendToTarget();
			}
		}).start();
	}
	
	private Handler mUIHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case WHAT_DID_LOAD_DATA:{
					mAdapter.notifyDataSetChanged();
					mPullDownView.notifyDidLoad();
					break;
				}
				case WHAT_DID_REFRESH :{
					mAdapter.notifyDataSetChanged();
					mPullDownView.notifyDidRefresh();
					break;
				}
				
				case WHAT_DID_MORE:{
					mAdapter.notifyDataSetChanged();
					mPullDownView.notifyDidMore();
					break;
				}
			}
			
		}
		
	};
	
	public void loadMoreData(boolean refresh){
		// get orders from server side
		User user = LocalPreferences.getUser(this);
		OrderListResult result = null;
		if (refresh){
			page = 1;
			result = MyOrderApi.getOrdersByType(STATUS, page, count, user);
		} else {
			result = MyOrderApi.getOrdersByType(STATUS, page, count, user);
		}
		if (result != null && result.isNeedLogin() && !result.isSuccess()){
			AcountApi.loginNormal(user.getUserName(), user.getNickName(), user.getPwd(), user.getType(), this);
			result = MyOrderApi.getOrdersByType(STATUS, page, count, user);
		}
		if (result == null){
			mLoadMoreHandler.post(new Runnable(){
	     		   public void run(){
	     			   Toast.makeText(FinishedOrderListActivity.this, "加载数据失败，请重新尝试", Toast.LENGTH_SHORT).show();
	     		   }
	 	    	});
	     		return;
		}
		ArrayList<OrderSummary> orders = result.getOrders();
    	if (orders == null || orders.size() == 0){
    		mLoadMoreHandler.post(new Runnable(){
     		   public void run(){
     			   Toast.makeText(FinishedOrderListActivity.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
     		   }
 	    	});
     		return;
    	}
    	page += 1;
    	if (refresh){
    		mAdapter.clearItems();
    	}
		for (int i=0; i < orders.size(); i++){
    		OrderSummary order = orders.get(i);
    		mAdapter.addItem(order);
    	}
	}
	
	@Override
	public void onBackPressed(){
		// back to main activity
		ActivityTool.backToMain(this, null);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if (mAdapter == null){
    		Log.e(TAG, "list adapter is null, something happende");
    		return;
    	}
    	OrderSummary order = mAdapter.getOrderAtPosition(position);
    	Intent intent = new Intent(FinishedOrderListActivity.this, OrderDetailActivity.class);
    	intent.putExtra("order_id", order.getId());
    	intent.putExtra("shop_name", order.getShopName());
    	intent.putExtra("status", order.getStatus());
    	intent.putExtra("time", order.getTime());
    	intent.putExtra("price", order.getPrice());
    	ActivityTool.startActivity(FinishedOrderListActivity.this, OrderDetailActivity.class, intent);
	}

	@Override
	public void onRefresh() {
		new Thread(new Runnable() {
	 	       @Override
	 	       public void run() {
	 	    	   loadMoreData(true);
	 	    	   Message msg = mUIHandler.obtainMessage(WHAT_DID_REFRESH);
	 	    	   msg.sendToTarget();
	 	       }
	 	    }).start();
	}

	@Override
	public void onMore() {
		new Thread(new Runnable() {
	 	       @Override
	 	       public void run() {
	 	    	   loadMoreData(false);
	 	    	   Message msg = mUIHandler.obtainMessage(WHAT_DID_MORE);
	 	    	   msg.sendToTarget();
	 	       }
	 	    }).start();
	}
}
