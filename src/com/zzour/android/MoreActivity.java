package com.zzour.android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zzour.andoird.base.BaseActivity;
import com.zzour.android.utils.ActivityTool;
import com.zzour.android.views.tab.MyTabHostProvider;
import com.zzour.android.views.tab.TabHostProvider;
import com.zzour.android.views.tab.TabView;

public class MoreActivity extends BaseActivity 
{
	private static final String TAG = "ZZOUR";
	private final int selectedColor = Color.rgb(243, 174, 27);
	
	private RelativeLayout login;
	private RelativeLayout register;
	private RelativeLayout settings;
	private RelativeLayout about;
	private RelativeLayout feedback;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TabHostProvider tabProvider = new MyTabHostProvider(MoreActivity.this);
		TabView tabView = tabProvider.getTabHost(getResources().getString(R.string.more_title));
		tabView.setCurrentView(R.layout.more);
		setContentView(tabView.render(3));
		
		login = (RelativeLayout)findViewById(R.id.more_login);
		login.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ActivityTool.startActivity(MoreActivity.this, LoginActivity.class);
			}
		});
		login.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View view, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					login.setBackgroundColor(selectedColor);
				} else if (e.getAction() == MotionEvent.ACTION_UP) {
					login.setBackgroundColor(0x00000000);
				}
				return false;
			}
		});
		
		register = (RelativeLayout)findViewById(R.id.more_register);
		register.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ActivityTool.startActivity(MoreActivity.this, RegisterActivity.class);
			}
		});
		register.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View view, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					register.setBackgroundColor(selectedColor);
				} else if (e.getAction() == MotionEvent.ACTION_UP) {
					register.setBackgroundColor(0x00000000);
				}
				return false;
			}
		});
		
		settings = (RelativeLayout)findViewById(R.id.more_settings);
		settings.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ActivityTool.startActivity(MoreActivity.this, SettingsActivity.class);
			}
		});
		settings.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View view, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					settings.setBackgroundColor(selectedColor);
				} else if (e.getAction() == MotionEvent.ACTION_UP) {
					settings.setBackgroundColor(0x00000000);
				}
				return false;
			}
		});
		
		about = (RelativeLayout)findViewById(R.id.more_about);
		about.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ActivityTool.startActivity(MoreActivity.this, AboutActivity.class);
			}
		});
		about.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View view, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					about.setBackgroundColor(selectedColor);
				} else if (e.getAction() == MotionEvent.ACTION_UP) {
					about.setBackgroundColor(0x00000000);
				}
				return false;
			}
		});
		
		feedback = (RelativeLayout)findViewById(R.id.more_feedback);
		feedback.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ActivityTool.startActivity(MoreActivity.this, FeedbackActivity.class);
			}
		});
		feedback.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View view, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					feedback.setBackgroundColor(selectedColor);
				} else if (e.getAction() == MotionEvent.ACTION_UP) {
					feedback.setBackgroundColor(0x00000000);
				}
				return false;
			}
		});
	}
}