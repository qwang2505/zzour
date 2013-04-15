package com.zzour.android.network.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.zzour.android.models.Food;
import com.zzour.android.models.ShopDetailContent;
import com.zzour.android.models.ShopList;
import com.zzour.android.models.ShopSummaryContent;

/*
 * Data api, for get data that need to be displayed in client.
 */
public class DataApi {
	
	private static ShopList mShopList;
	
	private static HashMap<Integer, ShopDetailContent> mShopDetailMap = new HashMap<Integer, ShopDetailContent>();

	public static ShopList getShopList(){
		// TODO get json/xml data from server, and format into a list content.
		// For demo, return some mocked data.
		// if shop list in memory and not expired, return directly.
		if (mShopList != null && !mShopList.expired()){
			Log.d("ZZOUR", "shop list in memory and not expired.");
			return mShopList;
		}
		// else if do not in memory, load from cache, and if cache not expired, return directly.
		// else, get list from server, and return
		// TODO get from cache, and check if expired.
		
		// TODO get data from server
		// For demo, get from fake data.
		String data = FakeData.getFackShopList(0, 0, 1, 15);
		return DataApi.parseShopListData(data);
	}
	
	public static ShopDetailContent getShopDetailById(int id){
		// if record in detail map, just return.
		if (mShopDetailMap.containsKey(id)){
			return mShopDetailMap.get(id);
		}
		
		// TODO if not in detail map, check cache
		
		// if not in local cache, get from server
		String data = FakeData.getFakeShopDetailById(id);
		return DataApi.parseShopDetailData(data);
	}
	
	private static ShopDetailContent parseShopDetailData(String data){
		ShopDetailContent shop = new ShopDetailContent();
		try {
			JSONTokener jsonObj = new JSONTokener(data);
			JSONObject dataObj = (JSONObject)jsonObj.nextValue();
			// basic information
			shop.setId(dataObj.getInt("id"));
			shop.setName(dataObj.getString("name"));
			shop.setDesc(dataObj.getString("desc"));
			shop.setAddress(dataObj.getString("addr"));
			shop.setBanner(dataObj.getString("banner"));
			shop.setRate((float)dataObj.getDouble("rate"));
			// recommends food
			JSONArray recommendsObj = dataObj.getJSONArray("rcmds");
			ArrayList<Food> recommends = new ArrayList<Food>();
			for (int i=0; i < recommendsObj.length(); i++){
				JSONObject obj = (JSONObject)recommendsObj.opt(i);
				Food f = new Food();
				f.setId(obj.getInt("id"));
				f.setName(obj.getString("name"));
				f.setImage(obj.getString("image"));
				recommends.add(f);
			}
			shop.setRecommends(recommends);
			JSONObject foodObj = dataObj.getJSONObject("foods");
			Iterator<Object> categories = foodObj.keys();
			HashMap<String, ArrayList<Food> > foodsMap = new HashMap<String,  ArrayList<Food> >();
			while (categories.hasNext()){
				String cat = String.valueOf(categories.next());
				Log.d("ZZOUR", cat);
				JSONArray foodsObj = foodObj.getJSONArray(cat);
				ArrayList<Food> foods = new ArrayList<Food>();
				for (int i=0; i < foodsObj.length(); i++){
					Food food = new Food();
					JSONObject obj = (JSONObject)foodsObj.opt(i);
					food.setId(obj.getInt("id"));
					food.setName(obj.getString("name"));
					food.setPrice((float)obj.getDouble("price"));
					food.setSoldCount(obj.getInt("soldCount"));
					foods.add(food);
				}
				foodsMap.put(cat, foods);
			}
			shop.setFoods(foodsMap);
			// foods by category
		} catch (JSONException ex){
			Log.e("ZZOUR", "error in parse shop detail content:¡¡" + ex);
			return null;
		}
		mShopDetailMap.put(shop.getId(), shop);
		return shop;
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
				int rate = obj.optInt("rate", 0);
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
		// TODO add data into cache
		return mShopList;
	}
}
