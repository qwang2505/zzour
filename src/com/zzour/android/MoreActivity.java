package com.zzour.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.zzour.andoird.base.BaseActivity;
import com.zzour.android.views.tab.MyTabHostProvider;
import com.zzour.android.views.tab.TabHostProvider;
import com.zzour.android.views.tab.TabView;

public class MoreActivity extends BaseActivity 
{
	private final String TAG = MoreActivity.class.getSimpleName();
	private ListView moreList = null;
	private final String[] moreItems = { "Blog", "Specials", "Coupons", "Gift Certificate", "Table Reservation",
			"Pickup Orders", "Book Event", "Catering", "Calendar", "Barcode"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TabHostProvider tabProvider = new MyTabHostProvider(MoreActivity.this);
		TabView tabView = tabProvider.getTabHost("More");
		tabView.setCurrentView(R.layout.more);
		setContentView(tabView.render(3));
		
		moreList = (ListView) findViewById(R.id.moreactivity_list);
		moreList.setAdapter(new MoreListAdapter(MoreActivity.this, moreItems));
		moreList.setOnItemClickListener(new OnItemClickListener() 
    	{
			
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final String activity = moreList.getAdapter().getItem(position).toString().trim();
				Log.d(TAG, "Clicked Item: " + activity);
			}
		});
	}
}