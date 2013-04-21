package com.zzour.andoird.base;

import com.zzour.android.MainActivity;
import com.zzour.android.R;
import com.zzour.android.cache.GlobalMemoryCache;
import com.zzour.android.utils.ActivityTool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class BaseActivity extends Activity{
	
	protected static final String TAG = "ZZOUR";
	
	@Override
	public void onBackPressed(){
		// not all back to main
		if (!ActivityTool.shouldBackToMain(this)){
			super.onBackPressed();
			overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
			return;
		}
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		this.startActivity(intent);
		overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
	    ActivityTool.overridePendingTransition(this);
	    //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);          
	    SysApplication.getInstance().addActivity(this);
	}
}
