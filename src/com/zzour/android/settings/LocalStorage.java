package com.zzour.android.settings;

import com.zzour.android.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class LocalStorage {
	
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
}
