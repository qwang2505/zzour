package com.zzour.android;

import com.zzour.andoird.base.BaseActivity;
import com.zzour.android.views.tab.MyTabHostProvider;
import com.zzour.android.views.tab.TabHostProvider;
import com.zzour.android.views.tab.TabView;

import android.os.Bundle;

public class OrderListActivity extends BaseActivity{
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		TabHostProvider tabProvider = new MyTabHostProvider(OrderListActivity.this);
		TabView tabView = tabProvider.getTabHost(getResources().getString(R.string.order_list_title));
		tabView.setCurrentView(R.layout.order_list);
		// TODO defin id in resource file
		setContentView(tabView.render(2));
	}
}
