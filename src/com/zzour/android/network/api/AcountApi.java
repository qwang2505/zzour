package com.zzour.android.network.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.util.Log;

import com.zzour.android.models.LoginResult;
import com.zzour.android.models.RegisterResult;
import com.zzour.android.models.User;
import com.zzour.android.settings.GlobalSettings;
import com.zzour.android.settings.LocalPreferences;
import com.zzour.android.utils.MD5Hash;

public class AcountApi {
	
	private static final String loginPath = "/user/login";
	private static final String registerPath = "/user/register";
	
	public static LoginResult loginNormal(String user, String pwd, User.AuthType authType, Activity activity){
		LoginResult result;
		// md5 password
		pwd = MD5Hash.md5(pwd);
		// call server api to log in
		String data = postLogin(user, pwd, authType.ordinal());
		if (data == null){
			result = new LoginResult();
			result.setSuccess(false);
			result.setMsg("连接服务器错误，请检查网络设置！");
			return result;
		}
		result = parseResult(data);
		if (result == null){
			result = new LoginResult();
			result.setSuccess(false);
			result.setMsg("连接服务器错误，请检查网络设置！");
		} else if (result.isSuccess()){
			User u = new User(user, pwd, authType);
			LocalPreferences.setUser(u, activity);
		}
		return result;
	}
	
	public static RegisterResult register(String user, String pwd, String mail, Activity activity){
		RegisterResult result;
		pwd = MD5Hash.md5(pwd);
		// call server api to log in
		String data = postRegister(user, pwd, mail);
		if (data == null){
			result = new RegisterResult();
			result.setSuccess(false);
			result.setMsg("连接服务器错误，请检查网络设置！");
			return result;
		}
		result = (RegisterResult)parseRegisterResult(data);
		if (result == null){
			result = new RegisterResult();
			result.setSuccess(false);
			result.setMsg("连接服务器错误，请检查网络设置！");
		} else if (result.isSuccess()){
			// login after register success
			User u = new User(user, pwd, User.AuthType.NORMAL);
			LocalPreferences.setUser(u, activity);
		}
		return result;
	}
	
	public static LoginResult parseResult(String data){
		try {
			JSONTokener jsonObj = new JSONTokener(data);
			JSONObject dataObj = (JSONObject)jsonObj.nextValue();
			boolean success = dataObj.getBoolean("sta");
			String msg = dataObj.getString("msg");
			// TODO add expire
			String expire = dataObj.optString("expire", "");
			LoginResult result = new LoginResult();
			result.setMsg(msg);
			result.setSuccess(success);
			return result;
		} catch (JSONException ex){
			Log.e("ZZOUR", "error in parse login result:　" + ex);
			return null;
		}
	}
	
	public static RegisterResult parseRegisterResult(String data){
		try {
			JSONTokener jsonObj = new JSONTokener(data);
			JSONObject dataObj = (JSONObject)jsonObj.nextValue();
			boolean success = dataObj.getBoolean("sta");
			String msg = dataObj.getString("msg");
			// TODO add expire
			String expire = dataObj.optString("expire", "");
			RegisterResult result = new RegisterResult();
			result.setMsg(msg);
			result.setSuccess(success);
			return result;
		} catch (JSONException ex){
			Log.e("ZZOUR", "error in parse login result:　" + ex);
			return null;
		}
	}
	
	public static String postRegister(String name, String password, String mail){
		// Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(buildRegisterUrl());
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
	        nameValuePairs.add(new BasicNameValuePair("name", name));
	        nameValuePairs.add(new BasicNameValuePair("pwd", password));
	        nameValuePairs.add(new BasicNameValuePair("mail", mail));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        if (response != null){
	        	return EntityUtils.toString(response.getEntity());
	        } else {
	        	return null;
	        }
	    } catch (ClientProtocolException e) {
	        // Auto-generated catch block
	    	return null;
	    } catch (IOException e) {
	        // Auto-generated catch block
	    	return null;
	    }
	}
	
	public static String postLogin(String name, String password, int type) {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(buildLoginUrl());
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
	        nameValuePairs.add(new BasicNameValuePair("name", name));
	        nameValuePairs.add(new BasicNameValuePair("pwd", password));
	        nameValuePairs.add(new BasicNameValuePair("t", String.valueOf(type)));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        if (response != null){
	        	return EntityUtils.toString(response.getEntity());
	        } else {
	        	return null;
	        }
	    } catch (ClientProtocolException e) {
	        // Auto-generated catch block
	    	return null;
	    } catch (IOException e) {
	        // Auto-generated catch block
	    	return null;
	    }
	}
	
	private static String buildRegisterUrl(){
		return GlobalSettings.getServerAddress() + registerPath;
	}
	
	private static String buildLoginUrl(){
		return GlobalSettings.getServerAddress() + loginPath;
	}
}
