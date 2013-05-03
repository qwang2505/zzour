package com.zzour.android;

import com.zzour.android.base.BaseActivity;
import com.zzour.android.models.LoginResult;
import com.zzour.android.models.User;
import com.zzour.android.network.api.AcountApi;
import com.zzour.android.settings.LocalPreferences;
import com.zzour.android.utils.ActivityTool;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends BaseActivity{
	
	private String userName;
	private String password;
	private User.AuthType authType;
	
	private LoginResult loginResult;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		if (LocalPreferences.authed(this)){
			this.onBackPressed();
			return;
		}
		
		if (this.getIntent().getBooleanExtra("back_to_main", false)){
			backToMain = true;
		} else {
			backToMain = false;
		}
		
		((Button)findViewById(R.id.back_btn)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				LoginActivity.this.onBackPressed();
				return;
			}
		});
		
		((Button)findViewById(R.id.login_btn)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// check username and password
				String user = ((EditText)findViewById(R.id.login_user_name)).getText().toString();
				if (user == null || user.length() == 0){
					Toast.makeText(LoginActivity.this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
					return;
				}
				String pwd = ((EditText)findViewById(R.id.login_password)).getText().toString();
				if (pwd == null || pwd.length() == 0){
					Toast.makeText(LoginActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
					return;
				}
				userName = user;
				password = pwd;
				authType = User.AuthType.NORMAL;
				// call api to login in async task
				new LoginTask(LoginActivity.this).execute();
			}
		});
		((Button)findViewById(R.id.register_btn)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// go to register activity
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				ActivityTool.startActivity(LoginActivity.this, RegisterActivity.class);
			}
		});
	}
	
	@Override
	public void onResume(){
		if (this.getIntent().getBooleanExtra("back_to_main", false)){
			this.backToMain = true;
		} else {
			this.backToMain = false;
		}
		super.onResume();
		if (LocalPreferences.authed(this)){
			this.onBackPressed();
			return;
		}
	}

	public void setLoginResult(LoginResult result){
		this.loginResult = result;
	}
	
	public void finishLogin(){
		if (this.loginResult == null){
			Log.e(TAG, "something must happened, login result is null while finish login");
			return;
		} else if (!this.loginResult.isSuccess()){
			Toast.makeText(this, this.loginResult.getMsg(), Toast.LENGTH_SHORT).show();
			return;
		} else {
			Intent intent = new Intent();
			LoginActivity.this.setResult(RESULT_OK, intent);
			LoginActivity.this.finish();
		}
	}
	
	private class LoginTask extends AsyncTask<String, Void, Boolean> {
		
		private LoginActivity activity = null;
		private ProgressDialog mDialog = null;

		public LoginTask(LoginActivity activity) {
	        this.activity = activity;
	        this.mDialog = new ProgressDialog(activity);
	    }
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			// call login api to login
			LoginResult result = AcountApi.loginNormal(userName, password, authType, LoginActivity.this);
			this.activity.setLoginResult(result);
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
	        this.activity.finishLogin();
	    }
	}
}
