package com.zzour.android;

import android.app.Activity;
import android.os.Bundle;

import com.zzour.andoird.base.BaseActivity;
import com.zzour.android.views.tab.MyTabHostProvider;
import com.zzour.android.views.tab.TabHostProvider;
import com.zzour.android.views.tab.TabView;

public class HomeActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO dig on to tab host provider
		TabHostProvider tabProvider = new MyTabHostProvider(HomeActivity.this);
		TabView tabView = tabProvider.getTabHost("Home");
		tabView.setCurrentView(R.layout.home);
		setContentView(tabView.render(0));
	}
}