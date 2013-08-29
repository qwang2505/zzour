package com.zzour.android.settings;

import com.zzour.android.R;
import com.zzour.android.models.User;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class LocalPreferences {
	
	public static final String SHOP_LIST_API = "shop_list";
	public static final String SHOP_DETAIL_API = "shop_detail";
	
	private static SharedPreferences prefs = null;
	
	private static void ensurePrefs(Activity activity){
		if (prefs == null){
			prefs = activity.getPreferences(Activity.MODE_PRIVATE);
		}
	}
	
	public static boolean isApiDataExpired(String api, Activity activity){
		ensurePrefs(activity);
		long now = System.currentTimeMillis();
		long t = prefs.getLong(api, 0);
		if (t == 0 || now - t > GlobalSettings.getApiExpireTime()){
			// no value in prefs or expired
			return true;
		}
		return false;
	}
	
	public static void recordApiCall(String api, Activity activity){
		ensurePrefs(activity);
		long now = System.currentTimeMillis();
		Editor editor = prefs.edit();
		editor.putLong(api, now);
		editor.commit();
	}
	
	public static long getLastAccess(String api, Activity activity){
		ensurePrefs(activity);
		return prefs.getLong(api, 0);
	}
	
	public static void setSearchKeyword(Activity activity, String value){
		ensurePrefs(activity);
		Editor editor = prefs.edit();
		editor.putString("search_keyword", value);
		editor.commit();
	}
	
	public static String getSearchKeyword(Activity activity){
		ensurePrefs(activity);
		String sk = prefs.getString("search_keyword", null);
		if (sk == null){
			sk = activity.getResources().getString(R.string.search_initial_text);
		}
		return sk;
	}
	
	public static void setHomePageBanners(Activity activity, String value){
		ensurePrefs(activity);
		Editor editor = prefs.edit();
		editor.putString("home_banners", value);
		editor.commit();
	}
	
	public static String[] getHomePageBanners(Activity activity){
		ensurePrefs(activity);
		String bs = prefs.getString("home_banners", "");
		return bs.split(";");
	}
	
	public static boolean authed(Activity activity){
		ensurePrefs(activity);
		return prefs.getBoolean("user_authed", false);
	}
	
	public static void setUser(User user, Activity activity){
		ensurePrefs(activity);
		Editor editor = prefs.edit();
		editor.putString("user_name", user.getUserName());
		editor.putString("user_pwd", user.getPwd());
		editor.putString("user_session", user.getSession());
		editor.putInt("user_auth_type", user.getType().ordinal());
		editor.putBoolean("user_authed", true);
		editor.commit();
	}
	
	public static User getUser(Activity activity){
		ensurePrefs(activity);
		if (!prefs.getBoolean("user_authed", false)){
			return null;
		}
		String name = prefs.getString("user_name", "");
		String pwd = prefs.getString("user_pwd", "");
		String session = prefs.getString("user_session", "");
		int type = prefs.getInt("user_auth_type", -1);
		User user = new User(name, pwd, session, User.AuthType.values()[type]);
		return user;
	}
	
	public static void logout(Activity activity){
		ensurePrefs(activity);
		Editor editor = prefs.edit();
		editor.remove("user_name");
		editor.remove("user_pwd");
		editor.remove("user_session");
		editor.remove("user_auth_type");
		editor.remove("user_authed");
		editor.commit();
	}
}
