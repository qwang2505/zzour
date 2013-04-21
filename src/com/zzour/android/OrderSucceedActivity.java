package com.zzour.android;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.zzour.andoird.base.BaseActivity;
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
				ActivityTool.startActivity(OrderSucceedActivity.this, OrderListActivity.class);
			}
		});
	}
}
