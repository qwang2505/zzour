package com.zzour.android;

import com.zzour.android.base.BaseActivity;
import com.zzour.android.settings.GlobalSettings;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SplashActivity extends BaseActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
//		PackageManager pm = getPackageManager();
//		try {
//		    PackageInfo pi = pm.getPackageInfo("com.zzour.android", 0);
//		    TextView versionNumber = (TextView) findViewById(R.id.versionNumber);
//		    versionNumber.setText("Version " + pi.versionName);
//		} catch (NameNotFoundException e) {
//		    e.printStackTrace();
//		}

		new Handler().postDelayed(new Runnable(){
			@Override
			public void run() {
			    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
			    startActivity(intent);
			    SplashActivity.this.finish();
			}
		}, 2500);
	}
}
