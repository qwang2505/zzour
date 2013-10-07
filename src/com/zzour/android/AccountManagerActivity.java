package com.zzour.android;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzour.android.base.BaseActivity;
import com.zzour.android.models.User;
import com.zzour.android.models.UserAccount;
import com.zzour.android.network.api.AcountApi;
import com.zzour.android.network.api.results.ApiResult;
import com.zzour.android.network.api.results.UserAccountResult;
import com.zzour.android.settings.LocalPreferences;

public class AccountManagerActivity extends BaseActivity{
	
	private User user = null;
	private UserAccount account = null;
	private Handler mToastHandler = new Handler();
	
	private ButtonHandler bHandler;
	
	private AlertDialog mChangePwdDialog = null;
	private AlertDialog mChangeMailDialog = null;
	
	private Button logout;
	
	
	class  ButtonHandler  extends  Handler { 

         private  WeakReference<DialogInterface> mDialog; 

         public ButtonHandler(DialogInterface dialog) { 
            mDialog = new WeakReference<DialogInterface>(dialog); 
         } 

         public void handleMessage(Message msg) { 
             switch (msg.what) { 
                 case DialogInterface.BUTTON_POSITIVE: 
                 case DialogInterface.BUTTON_NEGATIVE: 
                 case DialogInterface.BUTTON_NEUTRAL: 
                    ((DialogInterface.OnClickListener) msg.obj).onClick(mDialog.get(), msg.what); 
                     break ; 
            } 
        } 
    } 
	
	private void popUpDialog(AlertDialog dialog) { 
        /* 
         * alert dialog's default handler will always close dialog whenever user 
         * clicks on which button. we have to replace default handler with our 
         * own handler for blocking close action. 
         * Reflection helps a lot. 
         */ 
        try { 
            Field field = dialog.getClass().getDeclaredField("mAlert"); 
            field.setAccessible(true); 
            
            //retrieve mAlert value 
            Object obj = field.get(dialog); 
            field = obj.getClass().getDeclaredField("mHandler"); 
            field.setAccessible(true); 
            //replace mHandler with our own handler 
            bHandler = new ButtonHandler(dialog);
            field.set(obj, bHandler); 
        } catch (SecurityException e) { 
            Log.e(TAG, e.getMessage()); 
        } catch (NoSuchFieldException e) { 
        	Log.e(TAG,e.getMessage()); 
        } catch (IllegalArgumentException e) { 
        	Log.e(TAG,e.getMessage()); 
        } catch (IllegalAccessException e) { 
        	Log.e(TAG,e.getMessage()); 
        } 
        
        //we can show this dialog now. 
        dialog.show(); 
    } 
	
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
		logout = (Button)findViewById(R.id.logout);
		// logout
		logout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				showLogoutDialog();
			}
		});
		if (this.account == null){
			mToastHandler.post(new Runnable(){
	    		   public void run(){
	    			  Toast.makeText(AccountManagerActivity.this, "获取账号信息失败，请尝试注销后重新登录", Toast.LENGTH_SHORT).show();
	    		   }
	    	   });
			return;
		}
		
		TextView name = (TextView)findViewById(R.id.account_name_text);
		name.setText(account.getNickName());
		TextView email = (TextView)findViewById(R.id.account_email_info);
		email.setText(account.getEmail());
		TextView integral = (TextView)findViewById(R.id.account_integral_value);
		integral.setText(account.getIntegral() + "");
		// add onclick listener for change password and email
		RelativeLayout changePwdBtn = (RelativeLayout)findViewById(R.id.account_change_password);
		RelativeLayout changeMailBtn = (RelativeLayout)findViewById(R.id.account_change_email);
		User user = LocalPreferences.getUser(this);
		if (user.getType() != User.AuthType.NORMAL){
			// for third party login, can not change password and email address
			changePwdBtn.setVisibility(Button.GONE);
			changeMailBtn.setVisibility(Button.GONE);
		} else {
			changePwdBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showChangePwdDialog();
				}
			});
			changeMailBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showChangeMailDialog();
				}
			});
		}
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
			UserAccountResult result = AcountApi.getUserAcountDetail(user);
			if (result == null){
				// toast
				return true;
			}
			UserAccount account = result.getAccount();
			if (result != null && result.isNeedLogin() && !result.isSuccess()){
				AcountApi.loginNormal(user.getUserName(), user.getPwd(), user.getType(), activity);
				result = AcountApi.getUserAcountDetail(user);
				account = result.getAccount();
			}
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
	
	public void showChangePwdDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// valid input text and save.
				TextView oldPwd = (TextView)mChangePwdDialog.findViewById(R.id.pwd_old);
				String oldPwdText = oldPwd.getText().toString();
				if (oldPwdText.length() <= 0){
					Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				TextView newPwd = (TextView)mChangePwdDialog.findViewById(R.id.pwd_new);
				String newPwdText = newPwd.getText().toString();
				if (newPwdText.length() <= 0){
					Toast.makeText(getApplicationContext(), "新密码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				TextView newPwd2 = (TextView)mChangePwdDialog.findViewById(R.id.pwd_new_again);
				String newPwdText2 = newPwd2.getText().toString();
				if (!newPwdText2.equals(newPwdText)){
					Toast.makeText(getApplicationContext(), "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
					return;
				}
				if (newPwdText.equals(oldPwdText)){
					Toast.makeText(getApplicationContext(), "新密码不能喝旧密码相同", Toast.LENGTH_SHORT).show();
					return;
				}
				if (newPwd.length() > 20 || newPwd.length() < 6){
					Toast.makeText(getApplicationContext(), "新密码必须在6到20位之间", Toast.LENGTH_SHORT).show();
					return;
				}
				new ChangePwdTask(AccountManagerActivity.this, dialog).execute(oldPwdText, newPwdText, newPwdText2);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// on cancel, do nothing
				dialog.dismiss();
			}
		});
		builder.setTitle("修改密码");
		// init dialog view TODO init dialog view
		View view = LayoutInflater.from(this).inflate(R.layout.change_password, null);
		builder.setView(view);
		// get first level regions names
		mChangePwdDialog = builder.create();
		popUpDialog(mChangePwdDialog);
	}
	
	private class ChangePwdTask extends AsyncTask<String, Void, Boolean> {
		
		private AccountManagerActivity activity = null;
		private ProgressDialog mDialog = null;
		private DialogInterface dialog = null;
		private ApiResult result = null;
		private String password = null;

		public ChangePwdTask(AccountManagerActivity activity, DialogInterface dialog) {
	        this.activity = activity;
	        this.dialog = dialog;
	        this.mDialog = new ProgressDialog(activity);
	    }
		
		@Override
		protected Boolean doInBackground(String... args) {
			// call api to change password
			if (args.length != 3){
				return true;
			}
			User user = LocalPreferences.getUser(activity);
			password = args[1];
			result = AcountApi.changePwd(args[0], args[1], args[2], user);
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
	        // if change succeed, dismiss dialog
	        // else, toast error and return;
	        if (result == null){
	        	Toast.makeText(getApplicationContext(), "修改密码失败，请检查网络设置", Toast.LENGTH_SHORT).show();
	        	return;
	        }
	        if (result.isSuccess()){
	        	dialog.dismiss();
	        	// update local cache to update password
	        	User user = LocalPreferences.getUser(activity);
	        	user.setPwd(password);
	        	LocalPreferences.setUser(user, activity);
	        	Toast.makeText(getApplicationContext(), "修改密码成功", Toast.LENGTH_SHORT).show();
	        	return;
	        } else {
	        	if (result.getMsg() != null && result.getMsg().length() > 0){
	        		Toast.makeText(getApplicationContext(), result.getMsg(), Toast.LENGTH_SHORT).show();
	        		return;
	        	} else {
	        		Toast.makeText(getApplicationContext(), "修改密码失败，请重新尝试", Toast.LENGTH_SHORT).show();
	        		return;
	        	}
	        }
	    }
	}
	
	public void showChangeMailDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// valid input text and save.
				String pwd = ((TextView)mChangeMailDialog.findViewById(R.id.pwd)).getText().toString();
				if (pwd.length() <= 0){
					Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				String mail = ((TextView)mChangeMailDialog.findViewById(R.id.new_mail)).getText().toString();
				if (mail.length() <= 0){
					Toast.makeText(getApplicationContext(), "邮箱不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				new ChangeMailTask(AccountManagerActivity.this, dialog).execute(pwd, mail);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// on cancel, do nothing
				dialog.dismiss();
			}
		});
		builder.setTitle("修改邮箱");
		// init dialog view TODO init dialog view
		View view = LayoutInflater.from(this).inflate(R.layout.change_mail, null);
		builder.setView(view);
		// get first level regions names
		mChangeMailDialog = builder.create();
		popUpDialog(mChangeMailDialog);
	}
	
private class ChangeMailTask extends AsyncTask<String, Void, Boolean> {
		
		private AccountManagerActivity activity = null;
		private ProgressDialog mDialog = null;
		private DialogInterface dialog = null;
		private ApiResult result = null;
		private String password = null;

		public ChangeMailTask(AccountManagerActivity activity, DialogInterface dialog) {
	        this.activity = activity;
	        this.dialog = dialog;
	        this.mDialog = new ProgressDialog(activity);
	    }
		
		@Override
		protected Boolean doInBackground(String... args) {
			// call api to change password
			if (args.length != 2){
				return true;
			}
			User user = LocalPreferences.getUser(activity);
			result = AcountApi.changeMail(args[0], args[1], user);
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
	        // if change succeed, dismiss dialog
	        // else, toast error and return;
	        if (result == null){
	        	Toast.makeText(getApplicationContext(), "修改邮箱失败，请检查网络设置", Toast.LENGTH_SHORT).show();
	        	return;
	        }
	        if (result.isSuccess()){
	        	dialog.dismiss();
	        	Toast.makeText(getApplicationContext(), "修改邮箱成功", Toast.LENGTH_SHORT).show();
	        	return;
	        } else {
	        	if (result.getMsg() != null && result.getMsg().length() > 0){
	        		Toast.makeText(getApplicationContext(), result.getMsg(), Toast.LENGTH_SHORT).show();
	        		return;
	        	} else {
	        		Toast.makeText(getApplicationContext(), "修改邮箱失败，请重新尝试", Toast.LENGTH_SHORT).show();
	        		return;
	        	}
	        }
	    }
	}
}
