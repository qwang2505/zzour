package com.zzour.android;

import android.app.Activity;
import android.os.Bundle;

import com.zzour.andoird.base.BaseActivity;
import com.zzour.android.views.tab.MyTabHostProvider;
import com.zzour.android.views.tab.TabHostProvider;
import com.zzour.android.views.tab.TabView;

public class ContactActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TabHostProvider tabProvider = new MyTabHostProvider(ContactActivity.this);
		TabView tabView = tabProvider.getTabHost("±ãÀûµê");
		tabView.setCurrentView(R.layout.contact);
		setContentView(tabView.render(1));
	}
	
}