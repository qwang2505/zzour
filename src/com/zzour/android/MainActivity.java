package com.zzour.android;

import com.zzour.android.interfaces.OnTabActivityResultListener;
import com.zzour.android.utils.ActivityAnimations;
import com.zzour.android.utils.ActivityTool;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
	
	private LocalActivityManager mlam;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_main);
		ActivityTool.setMainActivity(this);
 
		mTabHost = getTabHost();
		mlam = new LocalActivityManager(this, true);
		mlam.dispatchCreate(savedInstanceState);
	    mTabHost.setup(mlam);
		mTabHost.getTabWidget().setStripEnabled(false);
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.empty_devider);
 
		// add all tabs 
		mTabHost.addTab(createTab(HomeActivity.class, 
                "home", "����", R.drawable.tab_home_selector));
		mTabHost.addTab(createTab(CollectionActivity.class, 
                "store", "�ҵ��ղ�", R.drawable.tab_my_collect_selector));
		mTabHost.addTab(createTab(OrderListActivity.class, 
                "orderList", "�ҵĶ���", R.drawable.tab_my_order_selector));
		mTabHost.addTab(createTab(MoreActivity.class, 
                "more", "����", R.drawable.tab_more_selector));
 
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
//	            if (tabId.equals("orderList")){
//	            	try {
//						mlam.dispatchResume();
//						Log.e("ZZOUR", "trigger dispatch resume finished for orderList");
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//	            }
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
		Log.e("ZZOUR", "main activity on resume");
		int tab = this.getIntent().getIntExtra("tab", index);
		this.switchTab(tab);
		super.onResume();
		this.getIntent().removeExtra("tab");
	    try {
	        mlam.dispatchResume();
	    } catch (Exception e) {}
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    try {
	        mlam.dispatchPause(isFinishing());
	    } catch (Exception e) {}
	}
	
	public void switchTab(int i){
		index = i;
		mTabHost.setCurrentTab(i);
		mView = mTabHost.getCurrentView();
	}
	
	public int getCurrentTabIndex(){
		return mTabHost.getCurrentTab();
	}

	private TabSpec createTab(final Class<?> intentClass, final String tag, 
            final String title, final int drawable)
    {
        final Intent intent = new Intent().setClass(this, intentClass);

        final View tab = LayoutInflater.from(getTabHost().getContext()).
            inflate(R.layout.tab, null);
        ImageView image = (ImageView)tab.findViewById(R.id.tab_icon);
        image.setImageResource(drawable);

        return getTabHost().newTabSpec(tag).setIndicator(tab).setContent(intent);
    }
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("ZZOUR", "on main actiivity result");
        // get current active sub activity
		String tabId = this.mTabHost.getCurrentTabTag();
        Activity subActivity = this.mlam.getActivity(tabId);
        // is implements interface
        if (subActivity instanceof OnTabActivityResultListener) {
            //get interface
            OnTabActivityResultListener listener = (OnTabActivityResultListener) subActivity;
            //call callback method
            listener.onTabActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
