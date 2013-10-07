package com.zzour.android;

import com.zzour.android.interfaces.OnTabActivityResultListener;
import com.zzour.android.models.User;
import com.zzour.android.network.api.AcountApi;
import com.zzour.android.settings.LocalPreferences;
import com.zzour.android.utils.ActivityAnimations;
import com.zzour.android.utils.ActivityTool;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

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
		
		// start background task to login
		new LoginTask(this).execute();
 
		mTabHost = getTabHost();
		mlam = new LocalActivityManager(this, true);
		mlam.dispatchCreate(savedInstanceState);
	    mTabHost.setup(mlam);
		mTabHost.getTabWidget().setStripEnabled(false);
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.empty_devider);
 
		// add all tabs 
		mTabHost.addTab(createTab(HomeActivity.class, 
                "home", "订餐", R.drawable.tab_home_selector));
		mTabHost.addTab(createTab(CollectionActivity.class, 
                "store", "我的收藏", R.drawable.tab_my_collect_selector));
		mTabHost.addTab(createTab(OrderListActivity.class, 
                "orderList", "我的订单", R.drawable.tab_my_order_selector));
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
	
	
	private class LoginTask extends AsyncTask<String, Void, Boolean> {
		
		private MainActivity activity = null;

		public LoginTask(MainActivity activity) {
	        this.activity = activity;
	    }
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			boolean authed = LocalPreferences.localAuthed(activity);
			if (!authed){
				Log.d("ZZOUR", "not local authed, exit login task");
				return true;
			}
			try {
				User user = LocalPreferences.getUser(activity);
				AcountApi.loginNormal(user.getUserName(), user.getPwd(), user.getType(), activity);
			} catch (Exception e){
				e.printStackTrace();
				Log.e("ZZOUR", "background login failed");
			}
			return true;
		}
		
		protected void onPreExecute() {
	    }

	        @Override
	    protected void onPostExecute(final Boolean success) {
	        Log.d("ZZOUR", "background login finished, success: " + LocalPreferences.authed(activity));
	    }
	}
}
