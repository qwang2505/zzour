package com.zzour.android;

import android.app.Activity;
import android.os.Bundle;

import com.zzour.andoird.base.BaseActivity;
import com.zzour.android.views.tab.MyTabHostProvider;
import com.zzour.android.views.tab.TabHostProvider;
import com.zzour.android.views.tab.TabView;

public class ShareActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TabHostProvider tabProvider = new MyTabHostProvider(ShareActivity.this);
		TabView tabView = tabProvider.getTabHost("Share");
		tabView.setCurrentView(R.layout.share);
		setContentView(tabView.render(2));
	}
}