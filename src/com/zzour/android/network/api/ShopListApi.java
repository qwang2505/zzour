package com.zzour.android.network.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.util.Log;

import com.zzour.android.models.Food;
import com.zzour.android.models.Order;
import com.zzour.android.models.School;
import com.zzour.android.models.SchoolArea;
import com.zzour.android.models.ShopDetailContent;
import com.zzour.android.models.ShopList;
import com.zzour.android.models.ShopSummaryContent;
import com.zzour.android.models.dao.ShopSummaryDAO;
import com.zzour.android.settings.LocalStorage;

/*
 * Data api, for get data that need to be displayed in client.
 */
public class ShopListApi {
	private static final String TAG = "ZZOUR";
	
	private static ShopList mShopList;
	
	public static ShopList getShopList(Activity activity){
		return getShopList(activity, -1, -1, 15);
	}

	public static ShopList getShopList(Activity activity, int sinceOrder, int maxOrder, int count){
		// TODO get json/xml data from server, and format into a list content.
		// For demo, return some mocked data.
		// if shop list in memory and not expired, return directly.
		if (mShopList != null && !mShopList.expired()){
			Log.d("ZZOUR", "shop list in memory and not expired.");
			return mShopList;
		}
		// else if do not in memory, load from cache, and if cache not expired, return directly.
		// else, get list from server, and return
		// get from cache, and check if expired.
		// first should load last request time in preference, to decide whether the data
		// 		is expired. if not expired, load from local database.
		// same api have different parameters, can't just decide expired by api path, need
		//      to consider parameters too.
		String key = LocalStorage.SHOP_LIST_API + "so=" + sinceOrder + "&mo=" + maxOrder +
				"&rc=" + count;
		if (!LocalStorage.isApiDataExpired(key, activity)){
			// since data not expired, get shop list from local database
			ShopSummaryDAO m = new ShopSummaryDAO(activity);
			// read search keyword from local storage. if nothing, use default
			String sk = LocalStorage.getSearchKeyword(activity);
			// read banners from local storage
			String[] banners = LocalStorage.getHomePageBanners(activity);
			// TODO apply parameters to get from database
			ShopList sl = new ShopList(sk, banners, m.get());
			Log.d(TAG, "get shop list from local database since it is not expired");
			return sl;
		}
		Log.d(TAG, "data expired, get from server again");
		// TODO get data from server
		// For demo, get from fake data.
		String data = FakeData.getFackShopList(0, 0, 1, 15);
		// TODO save data to local database and settings
		ShopList sl = ShopListApi.parseShopListData(data);
		saveShopList(activity, sl, key);
		return sl;
	}
	
	private static void saveShopList(Activity activity, ShopList shopList, String key){
		LocalStorage.setSearchKeyword(activity, shopList.getmSearchKeyword());
		String banner = "";
		Iterator<String> it = shopList.getmBanners().iterator();
		while (it.hasNext()){
			if (banner.length() != 0){
				banner += ";";
			}
			banner += it.next();
		}
		LocalStorage.setHomePageBanners(activity, banner);
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
		LocalStorage.recordApiCall(key, activity);
		Log.d(TAG, "save shop list api data into local storage and database");
	}
	
	private static ShopList parseShopListData(String data){
		try{
			JSONTokener jsonObj = new JSONTokener(data);
			JSONObject dataObj = (JSONObject)jsonObj.nextValue();
			// get default search key, optional
			String dsk = dataObj.optString("dsk");
			// get banners, optional
			JSONArray bs = dataObj.optJSONArray("bs");
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
				String image = obj.getString("img");
				float rate = (float)obj.optDouble("rate", 0.0);
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