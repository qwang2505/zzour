package com.zzour.android.network.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

import com.zzour.android.models.LoginResult;
import com.zzour.android.models.RegisterResult;
import com.zzour.android.models.User;
import com.zzour.android.models.UserAccount;
import com.zzour.android.settings.GlobalSettings;
import com.zzour.android.settings.LocalPreferences;

public class AcountApi {
	
	private static final String loginPath = "/index.php?app=member&act=login&method=ajax";
	private static final String registerPath = "/index.php?app=member&act=register&method=ajax";
	private static final String accountDetailPath = "/index.php?app=member&act=profile&method=ajax";
	
	private static final String sessionName = "ECM_ID";
	
	public static LoginResult loginNormal(String user, String pwd, User.AuthType authType, Activity activity){
		LoginResult result;
		// md5 password
		//pwd = MD5Hash.md5(pwd);
		// call server api to log in
		ApiResult apiResult = postLogin(user, pwd, authType.ordinal());
		if (apiResult == null || apiResult.session.length() == 0){
			result = new LoginResult();
			result.setSuccess(false);
			result.setMsg("连接服务器错误，请检查网络设置！");
			return result;
		}
		result = parseResult(apiResult.data);
		if (result == null){
			result = new LoginResult();
			result.setSuccess(false);
			result.setMsg("连接服务器错误，请检查网络设置！");
		} else if (result.isSuccess()){
			User u = new User(user, pwd, apiResult.session, authType);
			LocalPreferences.setUser(u, activity);
		}
		return result;
	}
	
	public static RegisterResult register(String user, String pwd, String mail, Activity activity){
		RegisterResult result;
		//pwd = MD5Hash.md5(pwd);
		// call server api to log in
		ApiResult apiResult = postRegister(user, pwd, mail);
		if (apiResult == null || apiResult.session.length() == 0){
			result = new RegisterResult();
			result.setSuccess(false);
			result.setMsg("连接服务器错误，请检查网络设置！");
			return result;
		}
		result = parseRegisterResult(apiResult.data);
		if (result == null){
			result = new RegisterResult();
			result.setSuccess(false);
			result.setMsg("连接服务器错误，请检查网络设置！");
		} else if (result.isSuccess()){
			// login after register success
			User u = new User(user, pwd, apiResult.session, User.AuthType.NORMAL);
			LocalPreferences.setUser(u, activity);
		}
		return result;
	}
	
	public static UserAccount getUserAcountDetail(User user){
		String data = getAccountDetail(user.getSession());
		if (data == null){
			return null;
		}
		return parseAccountDetail(data);
	}
	
	public static UserAccount parseAccountDetail(String data){
		try {
			Log.e("zzour", "login data: " + data);
			//JSONTokener jsonObj = new JSONTokener(data);
			//JSONObject dataObj = (JSONObject)jsonObj.nextValue();
			JSONObject dataObj = new JSONObject(data);
			boolean success = dataObj.getBoolean("done");
			if (!success){
				return null;
			}
			JSONObject retObj = dataObj.getJSONObject("retval");
			UserAccount account = new UserAccount();
			account.setUserName(retObj.getString("user_name"));
			account.setEmail(retObj.getString("email"));
			account.setIntegral(retObj.getInt("integral"));
			Log.e("ZZOUR", "over parse account data");
			return account;
		} catch (JSONException ex){
			Log.e("ZZOUR", "error in parse login result:　" + ex);
			return null;
		}
	}
	
	public static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {  
            outStream.write(buffer, 0, len);  
        }  
        inStream.close();  
        return outStream.toByteArray();  
    }
	
	public static String getAccountDetail(String session){
		String src = buildAccountDetailUrl();
		Log.e("ZZOUR", "get data from " + src);
		InputStream content = null;
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
		    CookieStore cookieStore = httpclient.getCookieStore();
		    BasicClientCookie cookie = new BasicClientCookie(sessionName, session);
		    cookie.setDomain(GlobalSettings.server);
		    cookie.setPath("/");
		    cookieStore.addCookie(cookie);
		    httpclient.setCookieStore(cookieStore);
		    HttpResponse response = httpclient.execute(new HttpGet(src));
		    content = response.getEntity().getContent();
		    byte[] buf = readInputStream(content);
		    return (new String(buf));
		} catch (Exception e) {
		    Log.e("ZZOUR", "Network exception", e);
		    return null;
		}
	}
	
	public static LoginResult parseResult(String data){
		try {
			Log.e("zzour", "login data: " + data);
			//JSONTokener jsonObj = new JSONTokener(data);
			//JSONObject dataObj = (JSONObject)jsonObj.nextValue();
			JSONObject dataObj = new JSONObject(data);
			boolean success = dataObj.getBoolean("done");
			// TODO add expire
			//String expire = dataObj.optString("expire", "");
			int retval = dataObj.getInt("retval");
			LoginResult result = new LoginResult();
			result.setMsg(retval);
			result.setSuccess(success);
			return result;
		} catch (JSONException ex){
			Log.e("ZZOUR", "error in parse login result:　" + ex);
			return null;
		}
	}
	
	public static RegisterResult parseRegisterResult(String data){
		try {
			Log.e("zzour", "data: " + data);
			//JSONTokener jsonObj = new JSONTokener(data);
			//JSONObject dataObj = (JSONObject)jsonObj.nextValue();
			JSONObject dataObj = new JSONObject(data);
			boolean success = dataObj.getBoolean("done");
			int retval = dataObj.getInt("retval");
			// TODO add expire
			//String expire = dataObj.optString("expire", "");
			RegisterResult result = new RegisterResult();
			result.setMsg(retval);
			result.setSuccess(success);
			return result;
		} catch (JSONException ex){
			Log.e("ZZOUR", "error in parse register result:　" + ex);
			return null;
		}
	}
	
	public static ApiResult postRegister(String name, String password, String mail){
		// Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(buildRegisterUrl());
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
	        nameValuePairs.add(new BasicNameValuePair("user_name", name));
	        nameValuePairs.add(new BasicNameValuePair("password", password));
	        nameValuePairs.add(new BasicNameValuePair("email", mail));
	        nameValuePairs.add(new BasicNameValuePair("password_confirm", password));
	        nameValuePairs.add(new BasicNameValuePair("agree", "1"));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        List<Cookie> cookie = ((AbstractHttpClient) httpclient)
	        		.getCookieStore().getCookies();
	        ApiResult result = new ApiResult();
	        if (response != null){
	        	result.data = EntityUtils.toString(response.getEntity());
	        } else {
	        	return null;
	        }
	        if (cookie.size() <= 0){
	        	Log.e("zzour", "no cookie");
	        	result.data = EntityUtils.toString(response.getEntity());
	        	result.session = "";
	        	return result;
	        }
	        if (cookie.size() > 1){
	        	Log.e("zzour", "cookie size more than one, use the first one as session id");
	        }
	        result.session = cookie.get(0).getValue();
	        return result;

	    } catch (ClientProtocolException e) {
	        // Auto-generated catch block
	    	return null;
	    } catch (IOException e) {
	        // Auto-generated catch block
	    	return null;
	    }
	}
	
	public static ApiResult postLogin(String name, String password, int type) {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(buildLoginUrl());
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("user_name", name));
	        nameValuePairs.add(new BasicNameValuePair("password", password));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        List<Cookie> cookie = ((AbstractHttpClient) httpclient)
	        		.getCookieStore().getCookies();
	        ApiResult result = new ApiResult();
	        if (response != null){
	        	result.data = EntityUtils.toString(response.getEntity());
	        } else {
	        	return null;
	        }
	        if (cookie.size() <= 0){
	        	Log.e("zzour", "no cookie");
	        	result.data = EntityUtils.toString(response.getEntity());
	        	result.session = "";
	        	return result;
	        }
	        if (cookie.size() > 1){
	        	Log.e("zzour", "cookie size more than one, use the first one as session id");
	        }
	        result.session = cookie.get(0).getValue();
	        return result;
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
	
	private static String buildAccountDetailUrl(){
		return GlobalSettings.getServerAddress() + accountDetailPath;
	}
	
	private static class ApiResult{
		public String data;
		public String session;
	}
}
