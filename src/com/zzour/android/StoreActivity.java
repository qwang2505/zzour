package com.zzour.android;

import android.os.Bundle;

import com.zzour.andoird.base.BaseActivity;
import com.zzour.android.views.tab.MyTabHostProvider;
import com.zzour.android.views.tab.TabHostProvider;
import com.zzour.android.views.tab.TabView;

public class StoreActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TabHostProvider tabProvider = new MyTabHostProvider(StoreActivity.this);
		TabView tabView = tabProvider.getTabHost(getResources().getString(R.string.store_tab_text));
		tabView.setCurrentView(R.layout.store);
		setContentView(tabView.render(1));
	}
	
}