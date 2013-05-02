package com.zzour.android;

import com.zzour.android.base.SysApplication;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity{
	
	private int index = 0;
	private TabHost mTabHost = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_main);
 
		mTabHost = getTabHost();
		mTabHost.getTabWidget().setStripEnabled(false);
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.empty_devider);
 
		// add all tabs 
		mTabHost.addTab(createTab(HomeActivity.class, 
                "home", "订餐", R.drawable.home));
		mTabHost.addTab(createTab(StoreActivity.class, 
                "store", "便利店", R.drawable.home));
		mTabHost.addTab(createTab(OrderListActivity.class, 
                "orderList", "我的订单", R.drawable.home));
		mTabHost.addTab(createTab(MoreActivity.class, 
                "more", "更多", R.drawable.home));
 
		//set Windows tab as default (zero based)
		Intent intent = this.getIntent();
		index = intent.getIntExtra("tab_index", 0);
		mTabHost.setCurrentTab(index);
		
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				index = getTabHost().getCurrentTab();
			}
		});
	}
	
	public void switchTab(int i){
		index = i;
		mTabHost.setCurrentTab(i);
	}

	private TabSpec createTab(final Class<?> intentClass, final String tag, 
            final String title, final int drawable)
    {
        final Intent intent = new Intent().setClass(this, intentClass);

        final View tab = LayoutInflater.from(getTabHost().getContext()).
            inflate(R.layout.tab, null);
        ((TextView)tab.findViewById(R.id.tab_text)).setText(title);
        ((ImageView)tab.findViewById(R.id.tab_icon)).setImageResource(drawable);

        return getTabHost().newTabSpec(tag).setIndicator(tab).setContent(intent);
    }
}
