package com.zzour.android;

import com.zzour.android.base.BaseActivity;
import com.zzour.android.settings.GlobalSettings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FeedbackActivity extends BaseActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);
		
		((Button)findViewById(R.id.back_btn)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				FeedbackActivity.this.onBackPressed();
				return;
			}
		});
		
		Button btn = (Button)findViewById(R.id.feedback_call);
		btn.setText(" ºô½ÐÕ¬Õ¬¿Í·þ£º" + GlobalSettings.PHONE);
		btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + GlobalSettings.PHONE));
                startActivity(intent);
			}
		});
	}
}
