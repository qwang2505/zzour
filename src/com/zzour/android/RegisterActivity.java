package com.zzour.android;

import com.zzour.android.base.BaseActivity;
import com.zzour.android.models.RegisterResult;
import com.zzour.android.network.api.AcountApi;
import com.zzour.android.utils.Validator;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends BaseActivity{
	
	private String mUser;
	private String mPwd;
	private String mMail;
	private RegisterResult registerResult;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		((Button)findViewById(R.id.back_btn)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				RegisterActivity.this.onBackPressed();
				return;
			}
		});
		
		((Button)findViewById(R.id.register_btn)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// check protocol agreement
				CheckBox protocol = (CheckBox)findViewById(R.id.register_agree_protocol);
				if (!protocol.isChecked()){
					Toast.makeText(RegisterActivity.this, "请先阅读并同意宅宅用户协议", Toast.LENGTH_SHORT).show();
					return;
				}
				// validate user name
				String name = ((EditText)findViewById(R.id.register_name)).getText().toString();
				Validator.ValidateResult result = Validator.validUserName(name);
				if (!result.isSuccess()){
					Toast.makeText(RegisterActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
					return;
				}
				// validate ensure password
				String pwd = ((EditText)findViewById(R.id.register_password)).getText().toString();
				String repeatPwd = ((EditText)findViewById(R.id.register_repeat_password)).getText().toString();
				if (pwd.compareTo(repeatPwd) != 0){
					Toast.makeText(RegisterActivity.this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
					return;
				}
				// validate password
				result = Validator.validPassword(pwd);
				if (!result.isSuccess()){
					Toast.makeText(RegisterActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
					return;
				}
				// validate mail address
				String mail = ((EditText)findViewById(R.id.register_mail)).getText().toString();
				result = Validator.validMailAddress(mail);
				if (!result.isSuccess()){
					Toast.makeText(RegisterActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
					return;
				}
				// TODO start async task to register
				mUser = name;
				mPwd = pwd;
				mMail = mail;
				new RegisterTask(RegisterActivity.this).execute();
			}
		});
	}
	
	public void setRegisterResult(RegisterResult result){
		registerResult = result;
	}
	
	public void finishRegister(){
		if (registerResult == null){
			Log.e(TAG, "what happend? register is null while finish register");
			return;
		}
		if (!registerResult.isSuccess()){
			Toast.makeText(RegisterActivity.this, registerResult.getMsg(), Toast.LENGTH_SHORT).show();
			return;
		} else {
			Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
			this.onBackPressed();
			return;
		}
	}
	
	private class RegisterTask extends AsyncTask<String, Void, Boolean> {
		
		private RegisterActivity activity = null;
		private ProgressDialog mDialog = null;

		public RegisterTask(RegisterActivity activity) {
	        this.activity = activity;
	        this.mDialog = new ProgressDialog(activity);
	    }
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			// call login api to login
			RegisterResult result = AcountApi.register(mUser, mPwd, mMail, activity);
			this.activity.setRegisterResult(result);
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
	        this.activity.finishRegister();
	    }
	}
}
