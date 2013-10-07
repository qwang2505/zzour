package com.zzour.android.base;

import com.zzour.android.R;
import com.zzour.android.utils.ActivityTool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class BaseActivity extends Activity{
	
	protected static final String TAG = "ZZOUR";
	protected boolean backToMain = false;
	
	@Override
	public void onBackPressed(){
		// not all back to main
		if (!ActivityTool.shouldBackToMain(this) && !backToMain){
			Log.e(TAG, "on back pressed");
			super.onBackPressed();
			overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
			return;
		}
		Log.e(TAG, "back to main");
		ActivityTool.backToMain(this, null);
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
