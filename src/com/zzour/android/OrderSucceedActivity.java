package com.zzour.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.zzour.android.base.BaseActivity;
import com.zzour.android.utils.ActivityTool;

public class OrderSucceedActivity extends BaseActivity{
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_succeed);
		
		Button b = (Button)findViewById(R.id.order_succeed_continut_btn);
		b.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				// to main activity
				ActivityTool.startActivity(OrderSucceedActivity.this, MainActivity.class);
			}
		});
		b = (Button)findViewById(R.id.order_succeed_list_btn);
		b.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// to order list activity
				Intent intent = new Intent(OrderSucceedActivity.this, MainActivity.class);
				intent.putExtra("tab", 2);
				ActivityTool.startActivity(OrderSucceedActivity.this, MainActivity.class, intent);
			}
		});
	}
	
	@Override
	public void onBackPressed(){
		ActivityTool.startActivity(this, MainActivity.class);
	}
}
