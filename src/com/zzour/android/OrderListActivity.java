package com.zzour.android;

import com.zzour.android.base.BaseActivity;
import com.zzour.android.interfaces.OnTabActivityResultListener;
import com.zzour.android.settings.LocalPreferences;
import com.zzour.android.utils.ActivityTool;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class OrderListActivity extends BaseActivity implements OnTabActivityResultListener{
	
	private TabHost mTabHost = null;
	private LocalActivityManager mlam;
	
	private static final int LOGIN_REQUEST_CODE = 100;
	private static final int index = 2;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_list);
		init(savedInstanceState);
		if (!LocalPreferences.authed(this)){
			Intent intent2 = new Intent(OrderListActivity.this.getParent(), LoginActivity.class);
			ActivityTool.startActivityForResult(OrderListActivity.this.getParent(), LoginActivity.class, LOGIN_REQUEST_CODE, intent2);
			Toast.makeText(this, "请先登陆", Toast.LENGTH_SHORT).show();
			return;
		}
		// TODO add refresh button to refresh data inside tab
	}

	private void init(Bundle savedInstanceState) {
		mTabHost = (TabHost)findViewById(R.id.distributionTabhost);
		mlam = new LocalActivityManager(this, false);
		mlam.dispatchCreate(savedInstanceState);
	    mTabHost.setup(mlam);
	    
	    mTabHost.getTabWidget().setStripEnabled(false);
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.empty_devider);
		
	    mTabHost.addTab(createTab(mTabHost, UnFinishedOrderListActivity.class, "unfinished", "待完成"));
	    mTabHost.addTab(createTab(mTabHost, FinishedOrderListActivity.class, "finished", "已完成"));
	    mTabHost.setCurrentTab(0);
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    try {
	        mlam.dispatchPause(isFinishing());
	    } catch (Exception e) {}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (ActivityTool.getMainActivity().getCurrentTabIndex() != OrderListActivity.index){
			return;
		}
	    if (!LocalPreferences.authed(this)){
	    	Toast.makeText(this, "请先登陆", Toast.LENGTH_SHORT).show();
			return;
		}
	    try {
	        mlam.dispatchResume();
	    } catch (Exception e) {}
	}

	private TabSpec createTab(TabHost tabHost, final Class<?> intentClass, final String tag, final String title)
    {
        final Intent intent = new Intent().setClass(this, intentClass);

        final View tab = LayoutInflater.from(tabHost.getContext()).inflate(R.layout.order_list_tab, null);
        ((TextView)tab.findViewById(R.id.tab_text)).setText(title);

        return tabHost.newTabSpec(tag).setIndicator(tab).setContent(intent);
    }

	@Override
	public void onTabActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK){
	        switch (requestCode) {
	        case LOGIN_REQUEST_CODE:
	        	Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show();
	            break;
	        default:
	            break;
	        }
		} else if (resultCode == RESULT_CANCELED){
	        switch (requestCode) {
	        case LOGIN_REQUEST_CODE:
	        	this.onBackPressed();
	            break;
	        default:
	            break;
	        }
		}
	}
}
