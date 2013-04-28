package com.zzour.android.network.api;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.zzour.android.models.Food;
import com.zzour.android.models.Order;
import com.zzour.android.models.School;
import com.zzour.android.models.SchoolArea;
import com.zzour.android.models.ShopDetailContent;
import com.zzour.android.models.ShopList;
import com.zzour.android.models.ShopSummaryContent;
import com.zzour.android.models.dao.ShopSummaryDAO;
import com.zzour.android.settings.GlobalSettings;
import com.zzour.android.settings.LocalPreferences;

/*
 * Data api, for get data that need to be displayed in client.
 */
public class ShopListApi {
	private static final String TAG = "ZZOUR";
	
	private static ShopList mShopList;
	
	private static String path = "/shop/list.json";
	
	public static ShopList getShopList(Activity activity){
		return getShopList(activity, 0, 6);
	}

	public static ShopList getShopList(Activity activity, int sinceOrder, int count){
		// get json/xml data from server, and format into a list content.
		// For demo, return some mocked data.
		// if shop list in memory and not expired, return directly.
		if (mShopList != null && !mShopList.expired()){
			return mShopList;
		}
		// else if do not in memory, load from cache, and if cache not expired, return directly.
		// else, get list from server, and return
		// get from cache, and check if expired.
		// first should load last request time in preference, to decide whether the data
		// 		is expired. if not expired, load from local database.
		// same api have different parameters, can't just decide expired by api path, need
		//      to consider parameters too.
		String key = LocalPreferences.SHOP_LIST_API + "so=" + sinceOrder +
				"&rc=" + count;
		if (!LocalPreferences.isApiDataExpired(key, activity)){
			// since data not expired, get shop list from local database
			ShopSummaryDAO m = new ShopSummaryDAO(activity);
			// read search keyword from local storage. if nothing, use default
			String sk = LocalPreferences.getSearchKeyword(activity);
			// read banners from local storage
			String[] banners = LocalPreferences.getHomePageBanners(activity);
			// TODO apply parameters to get from database
			ShopList sl = new ShopList(sk, banners, m.get());
			return sl;
		}
		Log.d(TAG, "data expired, get from server again");
		// get data from server
		long lastAccess = LocalPreferences.getLastAccess(key, activity);
		String data = getShopListFromServer(sinceOrder, count, lastAccess);
		if (data == null){
			return null;
		}
		// For demo, get from fake data.
		//String data = FakeData.getFackShopList(0, 0, 1, 15);
		// save data to local database and settings
		ShopList sl = ShopListApi.parseShopListData(data);
		if (sl != null){
			saveShopList(activity, sl, key);
		}
		return sl;
	}
	
	private static String getShopListFromServer(int sinceOrder, int count, long lastAccess){
		String src = buildUrl(sinceOrder, count, lastAccess);
		try {
	        URL url = new URL(src);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        DataInputStream input = new DataInputStream(connection.getInputStream());
	        byte[] buf = new byte[input.available()];
	        input.readFully(buf);
	        return (new String(buf));
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	private static String buildUrl(int sinceOrder, int count, long lastAccess){
		return GlobalSettings.getServerAddress() + path + "?sinceOrder=" + sinceOrder +
				"&rc=" + count + "&la=" + lastAccess;
	}
	
	private static void saveShopList(Activity activity, ShopList shopList, String key){
		LocalPreferences.setSearchKeyword(activity, shopList.getmSearchKeyword());
		String banner = "";
		Iterator<String> it = shopList.getmBanners().iterator();
		while (it.hasNext()){
			if (banner.length() != 0){
				banner += ";";
			}
			banner += it.next();
		}
		LocalPreferences.setHomePageBanners(activity, banner);
		// save shop summary to database
		ShopSummaryDAO m = new ShopSummaryDAO(activity);
		Iterator<ShopSummaryContent> it1 = shopList.getmShops().iterator();
		int order = 0;
		// TODO here can't just start with order 0, might need to do some merge or other actions.
		// TODO like, if just get two new shops, we need to increase order of exists shops, and 
		//       then insert new shops into database.
		while (it1.hasNext()){
			m.insert(it1.next(), order);
			order++;
		}
		LocalPreferences.recordApiCall(key, activity);
		Log.d(TAG, "save shop list api data into local storage and database");
	}
	
	private static ShopList parseShopListData(String data){
		try{
			JSONTokener jsonObj = new JSONTokener(data);
			JSONObject dataObj = (JSONObject)jsonObj.nextValue();
			// get default search key, optional
			String dsk = dataObj.optString("dsk");
			// get banners, optional
			JSONArray bs = dataObj.optJSONArray("banners");
			ArrayList<String> banners = new ArrayList<String>();
			for (int i=0; i < bs.length(); i++){
				banners.add((String)bs.opt(i));
			}
			// get shops
			JSONArray shopObjs = dataObj.getJSONArray("shops");
			ArrayList<ShopSummaryContent> shops = new ArrayList<ShopSummaryContent>();
			for (int i=0; i < shopObjs.length(); i++){
				JSONObject obj = (JSONObject)shopObjs.opt(i);
				int id = obj.getInt("id");
				String name = obj.getString("name");
				String desc = obj.optString("desc", "");
				String image = obj.getString("image");
				float rate = (float)obj.optDouble("rate", 4.0);
				boolean isNew = obj.optBoolean("new", false);
				
				ShopSummaryContent shop = new ShopSummaryContent(id, isNew, image, name, desc, rate);
				shops.add(shop);
			}
			mShopList = null;
			mShopList = new ShopList(dsk, banners, shops);
		} catch (JSONException ex){
			Log.e("ZZOUR", "error in parse result data: " + ex.toString());
			return null;
		}
		return mShopList;
	}
}
