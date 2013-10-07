package com.zzour.android;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennClient.LoginListener;
import com.renn.rennsdk.RennExecutor.CallBack;
import com.renn.rennsdk.RennParam;
import com.renn.rennsdk.RennResponse;
import com.renn.rennsdk.exception.RennException;
import com.renn.rennsdk.param.GetUserParam;
import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zzour.android.base.BaseActivity;
import com.zzour.android.models.AccountInfo;
import com.zzour.android.models.LoginResult;
import com.zzour.android.models.RegisterResult;
import com.zzour.android.models.User;
import com.zzour.android.network.api.AcountApi;
import com.zzour.android.settings.GlobalSettings;
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
	
	private LoginResult loginResult;
	
	// third party account instance
	Tencent mTencent = null;
	RennClient mRenren = null;
	
	AccountInfo mAccount = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		if (LocalPreferences.authed(this)){
			this.onBackPressed();
			return;
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
				if (mAccount != null){
					mAccount = null;
				}
				mAccount = new AccountInfo();
				mAccount.setName(user);
				mAccount.setPassword(pwd);
				mAccount.setType(User.AuthType.NORMAL);
				mAccount.setSuccess(true);
				// call api to login in async task
				new LoginTask(LoginActivity.this, mAccount).execute();
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
		
		((Button)findViewById(R.id.login_qq_btn)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// log in to qq by sdk
				if (mTencent == null){
					mTencent = Tencent.createInstance(GlobalSettings.TENCENT_APP_ID, getApplicationContext());
				}
				mTencent.login(LoginActivity.this, GlobalSettings.TENCENT_APP_SCOPE, new TencentLoginCallbackListener());
			}
		});
		
		((Button)findViewById(R.id.login_renren_btn)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// log in to renren by sdk
				if (mRenren == null){
					mRenren = RennClient.getInstance(LoginActivity.this);
					mRenren.init(GlobalSettings.RENREN_APP_ID, GlobalSettings.RENREN_API_KEY, GlobalSettings.RENREN_API_SECRET);
					mRenren.setScope(GlobalSettings.RENREN_APP_SCOPE);
					mRenren.setTokenType("bearer");
					mRenren.setLoginListener(new LoginListener(){
						@Override
						public void onLoginCanceled() {
							Toast.makeText(getApplicationContext(), "取消登录", Toast.LENGTH_SHORT);
							return;
						}
						@Override
						public void onLoginSuccess() {
							// save token for later usage
							if (mAccount != null){
								mAccount = null;
							}
							RennClient client = LoginActivity.this.getRennClient();
							mAccount = new AccountInfo();
							mAccount.setAccessToken(client.getAccessToken().accessToken);
							mAccount.setExpire(System.currentTimeMillis() + client.getAccessToken().expiresIn * 1000);
							mAccount.setType(User.AuthType.RENREN);
							mAccount.setUid(String.valueOf(client.getUid()));
						    try {
						    	// get renren account info
								GetUserParam param = new GetUserParam();
							    param.setUserId(client.getUid());
						        client.getRennService().sendAsynRequest(param, new CallBack() {        
						            @Override
						            public void onSuccess(RennResponse response) {
						            	// get user info
						            	Log.e(TAG, "renren account info: " + response.toString());
						            	try {
											JSONObject dataObj = response.getResponseObject().getJSONObject("response");
											String name = dataObj.getString("name");
											JSONArray avatarsObj = dataObj.getJSONArray("avatar");
											for (int i=0; i < avatarsObj.length(); i++){
												JSONObject avatarObj = avatarsObj.getJSONObject(i);
												String size = avatarObj.getString("size");
												if (size == "MAIN"){
													String url = avatarObj.getString("url");
													mAccount.setNickName(name);
													mAccount.setProfileUrl(url);
													mAccount.setSuccess(true);
													// login to zhaizhai, or register
													thirdPartyLogin();
													return;
												}
											}
										} catch (JSONException e) {
											e.printStackTrace();
											Toast.makeText(getApplicationContext(), "解析账号信息出错", Toast.LENGTH_SHORT).show();
											return;
										}
						            	Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
						            }                     
						            @Override
						            public void onFailed(String errorCode, String errorMessage) {
						                Toast.makeText(getApplicationContext(), "获取账号信息失败", Toast.LENGTH_SHORT).show();
						            }
						        });
						    } catch (RennException e) {
						        e.printStackTrace();
						        Toast.makeText(getApplicationContext(), "获取账号信息失败", Toast.LENGTH_SHORT).show();
						    }
							return;
						}
					});
				}
				mRenren.login(LoginActivity.this);
			}
		});
	}
	
	@Override
	public void onBackPressed(){
		// back logic
		Intent intent = new Intent();
		this.setResult(RESULT_CANCELED, intent);
		this.finish();
	}
	
	public RennClient getRennClient(){
		return this.mRenren;
	}
	
	public Tencent getTencentClient(){
		return this.mTencent;
	}
	
	public class TencentLoginCallbackListener implements IUiListener{

		@Override
		public void onCancel() {
			Toast.makeText(getApplicationContext(), "取消登录", Toast.LENGTH_SHORT).show();
			return;
		}

		@Override
		public void onComplete(JSONObject result) {
			// construct account info
			if (mAccount != null){
				mAccount = null;
			}
			try {
				boolean success = result.getInt("ret") == 0;
				if (!success){
					Toast.makeText(getApplicationContext(), "登录失败，请重新尝试", Toast.LENGTH_SHORT).show();
					return;
				}
				mAccount = new AccountInfo();
				// set fields
				String openId = result.getString("openid");
				long expireIn = result.getLong("expires_in");
				// calculate expire time
				String token = result.getString("access_token");
				mAccount.setAccessToken(token);
				mAccount.setExpire(System.currentTimeMillis() + expireIn * 1000);
				mAccount.setType(User.AuthType.TENCENT);
				mAccount.setOpenId(openId);
				mTencent.requestAsync(Constants.GRAPH_SIMPLE_USER_INFO, null, Constants.HTTP_GET, new BaseApiListener("get_simple_userinfo", true), null);
				return;
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "登录失败，请重新尝试", Toast.LENGTH_SHORT).show();
				return;
			}
		}

		@Override
		public void onError(UiError arg0) {
			Toast.makeText(getApplicationContext(), "登录失败，请重新尝试", Toast.LENGTH_SHORT).show();
			return;
		}
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
			Toast.makeText(getApplicationContext(), this.loginResult.getMsg(), Toast.LENGTH_SHORT).show();
			return;
		} else {
			Intent intent = new Intent();
			LoginActivity.this.setResult(RESULT_OK, intent);
			LoginActivity.this.finish();
		}
	}
	
	public void finishThirdPartyLogin(){
		Intent intent = new Intent();
		LoginActivity.this.setResult(RESULT_OK, intent);
		LoginActivity.this.finish();
	}
	
	private class LoginTask extends AsyncTask<String, Void, Boolean> {
		
		private LoginActivity activity = null;
		private AccountInfo account = null;
		private ProgressDialog mDialog = null;

		public LoginTask(LoginActivity activity, AccountInfo account) {
	        this.activity = activity;
	        this.account = account;
	        this.mDialog = new ProgressDialog(activity);
	    }
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			// call login api to login
			LoginResult result = AcountApi.login(account, activity);
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
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mTencent != null){
			mTencent.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	public void thirdPartyLogin(){
		Log.e("ZZOUR", "third party login");
		if (mAccount == null || !mAccount.isSuccess()){
			Toast.makeText(getApplicationContext(), "登录失败，请重新尝试", Toast.LENGTH_SHORT).show();
			return;
		}
		// login or register in async task
		Log.e(TAG, "start task to login");
		new ThirdPartyLoginTask(LoginActivity.this, mAccount).execute();
	}
	
	private class ThirdPartyLoginTask extends AsyncTask<String, Void, Boolean> {
		
		private LoginActivity activity = null;
		private AccountInfo account = null;
		private boolean success = false;
		private String msg = "";
		//private ProgressDialog mDialog = null;

		public ThirdPartyLoginTask(LoginActivity activity, AccountInfo account) {
			Log.e(TAG, "in third party login task init");
	        this.activity = activity;
	        this.account = account;
	        //this.mDialog = new ProgressDialog(activity);
	    }
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			// first, check if user valid
			Log.e(TAG, "check if user valid");
			boolean valid = AcountApi.isValidUser(this.account);
			Log.e(TAG, "user valid: " + valid);
			if (!valid){
				// register third party account
				RegisterResult result = AcountApi.registerThirdParty(account, activity);
				if (result == null || !result.isSuccess()){
					this.msg = "注册第三方账号失败，请重新尝试";
					return true;
				}
			} else {
				LoginResult result = AcountApi.login(account, activity);
				if (result == null || !result.isSuccess()){
					this.msg = "使用第三方账号登录失败，请稍后重试";
					return true;
				}
			}
			this.success = true;
			return true;
		}
		
		protected void onPreExecute() {
	        //this.mDialog.setMessage(this.activity.getResources().getString(R.string.loading_progress_text));
	        //this.mDialog.show();
	    }

	        @Override
	    protected void onPostExecute(final Boolean success) {
	        //if (mDialog.isShowing()) {
	        //	mDialog.dismiss();
	        //}
	        // finish third party login
	        if (this.success){
	        	this.activity.finishThirdPartyLogin();
	        } else {
	        	Toast.makeText(getApplicationContext(), this.msg, Toast.LENGTH_SHORT).show();
	        }
	    }
	}
	
	/*
	 * call back for qq get user info
	 */
	private class BaseApiListener implements IRequestListener {
        private String mScope = "all";
        private Boolean mNeedReAuth = false;

        public BaseApiListener(String scope, boolean needReAuth) {
            mScope = scope;
            mNeedReAuth = needReAuth;
        }

        @Override
        public void onComplete(final JSONObject response, Object state) {
        	Log.e(TAG, "get user info response: " + response.toString());
        	// save response
        	try {
				String name = response.getString("nickname");
				String url = response.getString("figureurl");
				mAccount.setNickName(name);
				mAccount.setProfileUrl(url);
				mAccount.setSuccess(true);
				// log in to zhaizhai, or register
				thirdPartyLogin();
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "解析登录信息失败", Toast.LENGTH_SHORT).show();
				return;
			}
        }

        @Override
        public void onIOException(final IOException e, Object state) {
        	e.printStackTrace();
        }

        @Override
        public void onMalformedURLException(final MalformedURLException e,
                Object state) {
        	e.printStackTrace();
        }

        @Override
        public void onJSONException(final JSONException e, Object state) {
        	e.printStackTrace();
        }

        @Override
        public void onConnectTimeoutException(ConnectTimeoutException arg0,
                Object arg1) {
        	arg0.printStackTrace();
        }

        @Override
        public void onSocketTimeoutException(SocketTimeoutException arg0,
                Object arg1) {
        	arg0.printStackTrace();
        }

        @Override
        public void onUnknowException(Exception arg0, Object arg1) {
        	arg0.printStackTrace();
        }

        @Override
        public void onHttpStatusException(HttpStatusException arg0, Object arg1) {
        	arg0.printStackTrace();
        }

        @Override
        public void onNetworkUnavailableException(NetworkUnavailableException arg0, Object arg1) {
        	arg0.printStackTrace();
        }
    }
}
