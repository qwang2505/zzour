package com.zzour.android;

import com.zzour.android.utils.ActivityAnimations;
import com.zzour.android.utils.ActivityTool;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity{
	
	private int index = 0;
	private View mView = null;
	private TabHost mTabHost = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_main);
		ActivityTool.setMainActivity(this);
 
		mTabHost = getTabHost();
		mTabHost.getTabWidget().setStripEnabled(false);
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.empty_devider);
 
		// add all tabs 
		mTabHost.addTab(createTab(HomeActivity.class, 
                "home", "订餐", R.drawable.tab_home_selector));
		mTabHost.addTab(createTab(StoreActivity.class, 
                "store", "便利店", R.drawable.tab_my_order_selector));
		mTabHost.addTab(createTab(OrderListActivity.class, 
                "orderList", "我的订单", R.drawable.tab_my_collect_selector)); 
		mTabHost.addTab(createTab(MoreActivity.class, 
                "more", "更多", R.drawable.tab_more_selector));
 
		//set Windows tab as default (zero based)
		Intent intent = this.getIntent();
		index = intent.getIntExtra("tab_index", 0);
		mTabHost.setCurrentTab(index);
		mView = mTabHost.getCurrentView();
		
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				View currentView = getTabHost().getCurrentView();
	            if (getTabHost().getCurrentTab() > index)
	            {
	            	mView.setAnimation(ActivityAnimations.outToLeftAnimation());
	                currentView.setAnimation(ActivityAnimations.inFromRightAnimation());
	            }
	            else
	            {
	            	mView.setAnimation(ActivityAnimations.outToRightAnimation());
	                currentView.setAnimation(ActivityAnimations.inFromLeftAnimation());
	            }

	            index = getTabHost().getCurrentTab();
	            mView = mTabHost.getCurrentView();
			}
		});
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
	    setIntent(intent);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		int tab = this.getIntent().getIntExtra("tab", index);
		this.switchTab(tab);
		this.getIntent().removeExtra("tab");
	}
	
	public void switchTab(int i){
		index = i;
		mTabHost.setCurrentTab(i);
		mView = mTabHost.getCurrentView();
	}

	private TabSpec createTab(final Class<?> intentClass, final String tag, 
            final String title, final int drawable)
    {
        final Intent intent = new Intent().setClass(this, intentClass);

        final View tab = LayoutInflater.from(getTabHost().getContext()).
            inflate(R.layout.tab, null);
        //((TextView)tab.findViewById(R.id.tab_text)).setText(title);
        ((ImageView)tab.findViewById(R.id.tab_icon)).setImageResource(drawable);

        return getTabHost().newTabSpec(tag).setIndicator(tab).setContent(intent);
    }
}
