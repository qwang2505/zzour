package com.zzour.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzour.android.base.BaseActivity;
import com.zzour.android.models.User;
import com.zzour.android.settings.LocalPreferences;
import com.zzour.android.utils.ActivityTool;

public class MoreActivity extends BaseActivity 
{
	private static final String TAG = "ZZOUR";
	private final int selectedColor = Color.rgb(243, 174, 27);
	private final int disableColor = Color.rgb(204, 204, 204);
	
	private RelativeLayout login;
	private RelativeLayout register;
	private RelativeLayout userLayout;
	//private RelativeLayout settings;
	private RelativeLayout about;
	private RelativeLayout feedback;
	//private RelativeLayout logout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more);
		userLayout = (RelativeLayout)findViewById(R.id.more_user);
		login = (RelativeLayout)findViewById(R.id.more_login);
		register = (RelativeLayout)findViewById(R.id.more_register);
		userLayout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Log.e("zzour", "click accout");
				ActivityTool.startActivity(MoreActivity.this, AccountManagerActivity.class);
			}
		});
		userLayout.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View view, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					userLayout.setBackgroundColor(selectedColor);
				} else if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_MOVE) {
					userLayout.setBackgroundColor(0x00000000);
				}
				return false;
			}
		});
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
				} else if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_MOVE) {
					login.setBackgroundColor(0x00000000);
				}
				return false;
			}
		});
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
				} else if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_MOVE) {
					register.setBackgroundColor(0x00000000);
				}
				return false;
			}
		});
		if (LocalPreferences.authed(this)){
			// disable login button
			login.setVisibility(RelativeLayout.GONE);
			register.setVisibility(RelativeLayout.GONE);
			userLayout.setVisibility(RelativeLayout.VISIBLE);
			TextView userName = (TextView)userLayout.findViewById(R.id.more_user_text);
			User user = LocalPreferences.getUser(this);
			if (user == null){
				Log.e("ZZOUR", "authed but no user name? what happened?");
			}
			userName.setText(user.getUserName());
		} else {
			userLayout.setVisibility(RelativeLayout.GONE);
			login.setVisibility(RelativeLayout.VISIBLE);
			register.setVisibility(RelativeLayout.VISIBLE);
		}
		
/*		settings = (RelativeLayout)findViewById(R.id.more_settings);
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
				} else if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_MOVE) {
					settings.setBackgroundColor(0x00000000);
				}
				return false;
			}
		});*/
		
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
				} else if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_MOVE) {
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
				} else if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_MOVE) {
					feedback.setBackgroundColor(0x00000000);
				}
				return false;
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (userLayout == null){
			userLayout = (RelativeLayout)findViewById(R.id.more_user);
		}
		if (login == null) {
			login = (RelativeLayout) findViewById(R.id.more_login);
		}
		if (register == null) {
			register = (RelativeLayout)findViewById(R.id.more_register);
		} 
		if (LocalPreferences.authed(this)){
			// disable login button
			login.setVisibility(RelativeLayout.GONE);
			register.setVisibility(RelativeLayout.GONE);
			userLayout.setVisibility(RelativeLayout.VISIBLE);
			TextView userName = (TextView)userLayout.findViewById(R.id.more_user_text);
			User user = LocalPreferences.getUser(this);
			if (user == null){
				Log.e("ZZOUR", "authed but no user name? what happened?");
			}
			userName.setText(user.getUserName());
		} else {
			userLayout.setVisibility(RelativeLayout.GONE);
			login.setVisibility(RelativeLayout.VISIBLE);
			register.setVisibility(RelativeLayout.VISIBLE);
		}
	}
}