package com.zzour.android;

import com.zzour.android.base.BaseActivity;
import com.zzour.android.settings.GlobalSettings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class AboutActivity extends BaseActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		((Button)findViewById(R.id.back_btn)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				AboutActivity.this.onBackPressed();
				return;
			}
		});
		
		Button callBtn = (Button)findViewById(R.id.about_call);
		callBtn.setText("呼叫宅宅客服：" + GlobalSettings.PHONE);
		callBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + GlobalSettings.PHONE));
                startActivity(intent);
			}
		});
		
		Button webBtn = (Button)findViewById(R.id.about_website);
		webBtn.setText("平台网址：" + GlobalSettings.server);
		webBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setData(Uri.parse(GlobalSettings.WEBSITE));
				intent.setAction(Intent.ACTION_VIEW);
				startActivity(intent);
			}
		});
		
		RelativeLayout updateVersion = (RelativeLayout)findViewById(R.id.about_update_version_block);
		updateVersion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "已经是最新版本", Toast.LENGTH_SHORT).show();
				return;
			}
		});
	}
}
