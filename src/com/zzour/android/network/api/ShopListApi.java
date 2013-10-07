package com.zzour.android.network.api;

import java.io.ByteArrayOutputStream;
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
	
	//private static String path = "/shop/list.json";
	private static String path = "/index.php?app=search&act=store&method=ajax";
	
	public static ShopList getShopList(Activity activity){
		return getShopList(activity, 0, 6);
	}

	public static ShopList getShopList(Activity activity, int pageNum, int count){
		// get json/xml data from server, and format into a list content.
		// For demo, return some mocked data.
		// if shop list in memory and not expired, return directly.
		//if (mShopList != null && !mShopList.expired()){
		//	return mShopList;
		//}
		// else if do not in memory, load from cache, and if cache not expired, return directly.
		// else, get list from server, and return
		// get from cache, and check if expired.
		// first should load last request time in preference, to decide whether the data
		// 		is expired. if not expired, load from local database.
		// same api have different parameters, can't just decide expired by api path, need
		//      to consider parameters too.
		// TODO do not query from local cache
		//String key = LocalPreferences.SHOP_LIST_API + "so=" + pageNum + "&rc=" + count;
		//if (!LocalPreferences.isApiDataExpired(key, activity)){
		//	// since data not expired, get shop list from local database
		//	ShopSummaryDAO m = new ShopSummaryDAO(activity);
		//	// read search keyword from local storage. if nothing, use default
		//	String sk = LocalPreferences.getSearchKeyword(activity);
		//	// read banners from local storage
		//	String[] banners = LocalPreferences.getHomePageBanners(activity);
		//	// TODO apply parameters to get from database
		//	ShopList sl = new ShopList(sk, banners, m.get());
		//	return sl;
		//}
		//Log.d(TAG, "data expired, get from server again");
		// get data from server
		// TODO get last access timestamp
		//long lastAccess = LocalPreferences.getLastAccess(key, activity);
		String data = getShopListFromServer(pageNum, count, 0);
		if (data == null){
			// TODO log error, and remind user
			return null;
		}
		// For demo, get from fake data.
		//String data = FakeData.getFackShopList(0, 0, 1, 15);
		// save data to local database and settings
		ShopList sl = ShopListApi.parseShopListData(data);
		// TODO save to local cache
		//if (sl != null){
		//	saveShopList(activity, sl, key);
		//}
		return sl;
	}
	
	private static String getShopListFromServer(int pageNum, int count, long lastAccess){
		String src = buildUrl(pageNum, count, lastAccess);
		Log.e("ZZOUR", "get data from " + src);
		try {
	        URL url = new URL(src);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream inStream = connection.getInputStream();
	        byte[] buf = readInputStream(inStream);
	        return (new String(buf));
	    } catch (Exception e) {
	        e.printStackTrace();
	        Log.e("ZZOUR", "get data from server error: " + e.toString());
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
	
	private static String buildUrl(int pageNum, int count, long lastAccess){
		return GlobalSettings.getServerAddress() + path + "&la=" + lastAccess +
				"&page=" + pageNum + "&page_per=" + count;
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
			//JSONTokener jsonObj = new JSONTokener(data);
			//JSONObject dataObj = (JSONObject)jsonObj.nextValue();
			JSONObject dataObj = new JSONObject(data);
			String error = dataObj.getString("msg");
			String done = dataObj.getString("done");
			if (done != "true"){
				// TODO log error, show error message to user.
				return null;
			}
			// TODO get default search key, optional
			//String dsk = dataObj.optString("dsk");
			// get banners, optional
			//JSONArray bs = dataObj.optJSONArray("banners");
			//ArrayList<String> banners = new ArrayList<String>();
			//for (int i=0; i < bs.length(); i++){
			//	banners.add((String)bs.opt(i));
			//}
			// get shops
			JSONObject shopObjs = dataObj.getJSONObject("retval");
			ArrayList<ShopSummaryContent> shops = new ArrayList<ShopSummaryContent>();
			JSONArray shopIds = shopObjs.names();
			for (int i=0; i < shopIds.length(); i++){
				JSONObject obj = (JSONObject)shopObjs.get(shopIds.getString(i));
				int id = obj.getInt("store_id");
				String name = obj.getString("store_name");
				// TODO get description
				String desc = obj.getString("description");
				String image = obj.getString("store_logo");
				// TODO format image correctly
				image = "http://www.zzour.com/" + image;
				float rate = (float)obj.getDouble("sgrade");
				boolean isNew = obj.optBoolean("new", false);
				
				ShopSummaryContent shop = new ShopSummaryContent(id, isNew, image, name, desc, rate);
				shop.setCreditValue(obj.getInt("credit_value"));
				shop.setGrade((float)obj.getDouble("sgrade"));
				shop.setOnlineOrder(obj.getInt("onlineOrder") == 1);
				// TODO for test
				if (id % 2 == 0){
					shop.setOnlineOrder(true);
				}
				shop.setAlive(obj.getInt("if_live") == 1);
				shops.add(shop);
			}
			mShopList = null;
			mShopList = new ShopList(shops);
		} catch (JSONException ex){
			Log.e("ZZOUR", "error in parse result data: " + ex.toString());
			return null;
		} catch (Exception ex){
			Log.e("ZZOUR", "error in parse result data 2: " + ex.toString());
			return null;
		}
		return mShopList;
	}
}
