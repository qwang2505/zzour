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
import android.widget.Toast;

import com.zzour.android.base.BaseActivity;
import com.zzour.android.settings.LocalPreferences;
import com.zzour.android.utils.ActivityTool;

public class MoreActivity extends BaseActivity 
{
	private static final String TAG = "ZZOUR";
	private final int selectedColor = Color.rgb(243, 174, 27);
	private final int disableColor = Color.rgb(204, 204, 204);
	
	private RelativeLayout login;
	private RelativeLayout register;
	private RelativeLayout settings;
	private RelativeLayout about;
	private RelativeLayout feedback;
	private RelativeLayout logout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more);
		
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
				} else if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_MOVE) {
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
				} else if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_MOVE) {
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
				} else if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_MOVE) {
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
		
		logout = (RelativeLayout)findViewById(R.id.more_logout);
		// logout
		logout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Log.d(TAG, "logout clicked");
				showLogoutDialog();
			}
		});
		logout.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View view, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					logout.setBackgroundColor(selectedColor);
				} else if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_MOVE) {
					logout.setBackgroundColor(0x00000000);
				}
				return false;
			}
		});
		
		// set button status
		if (LocalPreferences.authed(this)){
			// disable login button
			login.setClickable(false);
			login.setBackgroundColor(disableColor);
			login.setOnTouchListener(null);
		} else {
			// disable logout button
			logout.setClickable(false);
			logout.setBackgroundColor(disableColor);
			logout.setOnTouchListener(null);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (LocalPreferences.authed(this)){
			login.setClickable(false);
			login.setBackgroundColor(disableColor);
			login.setOnTouchListener(null);
			logout.setClickable(true);
			logout.setBackgroundColor(0x00000000);
			logout.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View view, MotionEvent e) {
					if (e.getAction() == MotionEvent.ACTION_DOWN) {
						logout.setBackgroundColor(selectedColor);
					} else if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_MOVE) {
						logout.setBackgroundColor(0x00000000);
					}
					return false;
				}
			});
		} else {
			logout.setClickable(false);
			logout.setBackgroundColor(disableColor);
			logout.setOnTouchListener(null);
			login.setClickable(true);
			login.setBackgroundColor(0x00000000);
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
		}
	}



	public void logout(){
		// logout
		LocalPreferences.logout(this);
		Toast.makeText(this, "注销成功", Toast.LENGTH_SHORT).show();
		// reset logout and login status
		login.setClickable(true);
		login.setBackgroundColor(0x00000000);
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
		logout.setClickable(false);
		logout.setBackgroundColor(disableColor);
		logout.setOnTouchListener(null);
	}
	
	public void showLogoutDialog(){
		if (!LocalPreferences.authed(this)){
			Toast.makeText(this, "用户没有登陆", Toast.LENGTH_SHORT).show();
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("确定注销账户？")
		       .setPositiveButton("确定", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                MoreActivity.this.logout();
		           }
		       })
		       .setNegativeButton("取消", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
}