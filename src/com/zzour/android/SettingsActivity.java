package com.zzour.android;

import com.zzour.android.base.BaseActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SettingsActivity extends BaseActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		((Button)findViewById(R.id.back_btn)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				SettingsActivity.this.onBackPressed();
				return;
			}
		});
	}
}
