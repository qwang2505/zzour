package com.zzour.android;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TabHost;

import com.zzour.android.base.BaseActivity;
import com.zzour.android.models.OrderSummary;
import com.zzour.android.models.dao.OrderDAO;
import com.zzour.android.views.adapters.OrderListItemsAdapter;

public class HistoryOrderListActivity extends BaseActivity{
	
	private OrderListItemsAdapter mAdapter = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_order_list);
		
		// TODO does it need to load from server?
		// load order list from local storage, and display
		mAdapter = new OrderListItemsAdapter(this, R.drawable.logo);
		OrderDAO dao = new OrderDAO(this);
		ArrayList<OrderSummary> orders = dao.getTodayOrders(6, null);
		Log.d(TAG, "get orders count " + orders.size());
		mAdapter.setItems(orders);
		ListView list = (ListView)findViewById(R.id.order_list);
		list.setAdapter(mAdapter);
		
		// TODO get image in new thread
	}
	
	@Override
	public void onBackPressed(){
		// back to main activity
		((MainActivity)this.getParent().getParent()).switchTab(0);
	}
}