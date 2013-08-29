package com.zzour.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zzour.android.base.BaseActivity;
import com.zzour.android.models.User;
import com.zzour.android.models.UserAccount;
import com.zzour.android.network.api.AcountApi;
import com.zzour.android.settings.LocalPreferences;

public class AccountManagerActivity extends BaseActivity{
	
	private User user = null;
	private UserAccount account = null;
	private Handler mToastHandler = new Handler();
	
	private Button logout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_manager);
		user = LocalPreferences.getUser(this);
		if (user == null){
			return;
		}
		new LoadingTask(this).execute();
	}
	
	public void setAccountResult(UserAccount account){
		this.account = account;
	}
	
	public void show(){
		if (this.account == null){
			mToastHandler.post(new Runnable(){
	    		   public void run(){
	    			  Toast.makeText(AccountManagerActivity.this, "获取账号信息失败！", Toast.LENGTH_SHORT).show();
	    		   }
	    	   });
			return;
		}
		
		TextView name = (TextView)findViewById(R.id.account_name_text);
		name.setText(account.getUserName());
		TextView email = (TextView)findViewById(R.id.account_email_text);
		email.setText(account.getEmail());
		TextView integral = (TextView)findViewById(R.id.account_integral_value);
		integral.setText(account.getIntegral() + "");
		// TODO add onclick listener for change password and email
		logout = (Button)findViewById(R.id.logout);
		// logout
		logout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				showLogoutDialog();
			}
		});
	}
	
	public void logout(){
		// logout
		LocalPreferences.logout(this);
		Toast.makeText(this, "退出登录成功", Toast.LENGTH_SHORT).show();
		// back
		super.onBackPressed();
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
		        	   AccountManagerActivity.this.logout();
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
	
	private class LoadingTask extends AsyncTask<String, Void, Boolean> {
		
		private AccountManagerActivity activity = null;
		private ProgressDialog mDialog = null;

		public LoadingTask(AccountManagerActivity activity) {
	        this.activity = activity;
	        this.mDialog = new ProgressDialog(activity);
	    }
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			UserAccount account = AcountApi.getUserAcountDetail(user);
			this.activity.setAccountResult(account);
			return true;
		}
		
		protected void onPreExecute() {
	        this.mDialog.setMessage(this.activity.getResources().getString(R.string.loading_progress_text));
	        this.mDialog.show();
	    }

	        @Override
	    protected void onPostExecute(final Boolean success) {
	        if (mDialog.isShowing()) {
	        	mDialog.dismiss();
	        }
	        this.activity.show();
	    }
	}
}
