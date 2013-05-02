package com.zzour.android;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.zzour.android.base.BaseActivity;
import com.zzour.android.models.OrderSummary;
import com.zzour.android.models.dao.OrderDAO;
import com.zzour.android.utils.ActivityTool;
import com.zzour.android.views.adapters.OrderListItemsAdapter;

public class TodayOrderListActivity extends BaseActivity{
	
	private OrderListItemsAdapter mAdapter = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_order_list);
		
		mAdapter = new OrderListItemsAdapter(this, R.drawable.logo);
		OrderDAO dao = new OrderDAO(this);
		ArrayList<OrderSummary> orders = dao.getTodayOrders(6, null);
		Log.d(TAG, "get orders count " + orders.size());
		mAdapter.setItems(orders);
		ListView list = (ListView)findViewById(R.id.order_list);
		list.setAdapter(mAdapter);
		
		// TODO get image in new thread
		
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
    	    	Intent intent = new Intent(TodayOrderListActivity.this, OrderDetailActivity.class);
    	    	intent.putExtra("order_id", order.getId());
    	    	ActivityTool.startActivity(TodayOrderListActivity.this, OrderDetailActivity.class, intent);
			}
		});
	}
	
	@Override
	public void onBackPressed(){
		// back to main activity
		// TODO here have bugs.
		((MainActivity)this.getParent().getParent()).switchTab(0);
	}
}
