package com.zzour.android;

import com.zzour.android.base.BaseActivity;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class OrderListActivity extends BaseActivity{
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_list);
		
		TabHost tabHost = (TabHost)findViewById(R.id.distributionTabhost);
		LocalActivityManager mLocalActivityManager = new LocalActivityManager(this, false);
	    mLocalActivityManager.dispatchCreate(savedInstanceState);
		tabHost.setup(mLocalActivityManager);
		
		tabHost.addTab(createTab(tabHost, TodayOrderListActivity.class, "today", "今日订单"));
		tabHost.addTab(createTab(tabHost, HistoryOrderListActivity.class, "history", "往日订单"));
		tabHost.setCurrentTab(0);
	}
	
	private TabSpec createTab(TabHost tabHost, final Class<?> intentClass, final String tag, final String title)
    {
        final Intent intent = new Intent().setClass(this, intentClass);

        final View tab = LayoutInflater.from(tabHost.getContext()).inflate(R.layout.order_list_tab, null);
        ((TextView)tab.findViewById(R.id.tab_text)).setText(title);

        return tabHost.newTabSpec(tag).setIndicator(tab).setContent(intent);
    }
}
