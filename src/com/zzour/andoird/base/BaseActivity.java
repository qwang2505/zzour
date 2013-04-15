package com.zzour.andoird.base;

import com.zzour.android.MainActivity;
import com.zzour.android.R;
import com.zzour.android.cache.GlobalMemoryCache;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class BaseActivity extends Activity{
	
	protected static final String TAG = "ZZOUR";
	
	@Override
	public void onBackPressed(){
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		this.startActivity(intent);
		overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);          
	    SysApplication.getInstance().addActivity(this);
	}
}
