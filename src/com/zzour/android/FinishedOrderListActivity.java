package com.zzour.android;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.zzour.android.base.BaseActivity;
import com.zzour.android.models.OrderSummary;
import com.zzour.android.models.User;
import com.zzour.android.network.api.AcountApi;
import com.zzour.android.network.api.MyOrderApi;
import com.zzour.android.network.api.results.OrderListResult;
import com.zzour.android.settings.LocalPreferences;
import com.zzour.android.utils.ActivityTool;
import com.zzour.android.views.adapters.OrderListItemsAdapter;

public class FinishedOrderListActivity extends BaseActivity{
	
	private OrderListItemsAdapter mAdapter = null;
	
	private View mLoadMoreView = null;
	private Button mLoadMoreButton = null;
	private Handler mLoadMoreHandler = new Handler();
	
	private int page = 1;
	private final int count = 6;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if (!LocalPreferences.authed(this)){
			return;
		}
		setContentView(R.layout.finished_order_list);
		
		// add load more to list view
        mLoadMoreView = getLayoutInflater().inflate(R.layout.loadmore, null);
        mLoadMoreButton = (Button)mLoadMoreView.findViewById(R.id.loadMoreButton);
        mLoadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	mLoadMoreButton.setText(getString(R.string.loading_text));
        	    new Thread(new Runnable() {
        	       @Override
        	       public void run() {
        	    	   loadMoreData();
        	    	   mLoadMoreHandler.post(new Runnable(){
        	    		   public void run(){
        	    			   mAdapter.notifyDataSetChanged();
        	    			   mLoadMoreButton.setText(getString(R.string.load_more_button_text));
        	    		   }
        	    	   });
        	       }
        	    }).start();
        	}
        });
        ListView list = (ListView)findViewById(R.id.order_list);
        list.addFooterView(mLoadMoreView);
		
		mAdapter = new OrderListItemsAdapter(this, R.drawable.logo);
		list.setAdapter(mAdapter);

		// add onclick listener
		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				//Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
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
		});
		
		mLoadMoreButton.setText(getString(R.string.loading_text));
	    new Thread(new Runnable() {
	       @Override
	       public void run() {
	    	   loadMoreData();
	    	   mLoadMoreHandler.post(new Runnable(){
	    		   public void run(){
	    			   mAdapter.notifyDataSetChanged();
	    			   mLoadMoreButton.setText(getString(R.string.load_more_button_text));
	    			   Log.d(TAG, "data loaded");
	    		   }
	    	   });
	       }
	    }).start();
	}
	
	public void loadMoreData(){
		// get orders from server side
		User user = LocalPreferences.getUser(this);
		OrderListResult result = MyOrderApi.getOrdersByType("all", page, count, user);
		if (result != null && result.isNeedLogin() && !result.isSuccess()){
			// login and try again
			AcountApi.loginNormal(user.getUserName(), user.getPwd(), user.getType(), this);
			result = MyOrderApi.getOrdersByType("all", page, count, user);
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
}