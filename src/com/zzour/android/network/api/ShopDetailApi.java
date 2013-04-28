package com.zzour.android.network.api;

import java.io.DataInputStream;
import java.io.IOException;
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
import android.util.Log;

import com.zzour.android.models.Food;
import com.zzour.android.models.ShopDetailContent;
import com.zzour.android.models.ShopSummaryContent;
import com.zzour.android.models.dao.ShopSummaryDAO;
import com.zzour.android.settings.GlobalSettings;

public class ShopDetailApi {
	
	private static HashMap<Integer, ShopDetailContent> mShopDetailMap = new HashMap<Integer, ShopDetailContent>();
	
	private static String path = "/shop/detail.json";
	
	public static ShopDetailContent getShopDetailById(int id, Activity activity){
		// if record in detail map, just return.
		if (mShopDetailMap.containsKey(id)){
			return mShopDetailMap.get(id);
		}
		
		// TODO if not in detail map, check cache
		
		// if not in local cache, get from server
		//String data = FakeData.getFakeShopDetailById(id);
		String data = getShopDetailFromServer(id, 0);
		if (data == null){
			return null;
		}
		ShopSummaryDAO m = new ShopSummaryDAO(activity);
		ShopSummaryContent shopSummary = m.getById(id);
		// TODO save cache data to database
		return parseShopDetailData(data, shopSummary);
	}
	
	private static String getShopDetailFromServer(int shopId, long lastAccess){
		String src = buildUrl(shopId, lastAccess);
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
	
	private static String buildUrl(int shopId, long lastAccess){
		return GlobalSettings.getServerAddress() + path + "?id=" + shopId  + "&la=" + lastAccess;
	}
	
	private static ShopDetailContent parseShopDetailData(String data, ShopSummaryContent shopSummary){
		ShopDetailContent shop = new ShopDetailContent();
		shop.setDesc(shopSummary.getDescription());
		shop.setId(shopSummary.getId());
		shop.setName(shopSummary.getName());
		shop.setRate(shopSummary.getRate());
		try {
			JSONTokener jsonObj = new JSONTokener(data);
			JSONObject dataObj = (JSONObject)jsonObj.nextValue();
			// basic information
			shop.setAddress(dataObj.getString("addr"));
			shop.setBanner(dataObj.getString("banner"));
			// recommends food
			JSONArray recommendsObj = dataObj.getJSONArray("rcmds");
			ArrayList<Food> recommends = new ArrayList<Food>();
			for (int i=0; i < recommendsObj.length(); i++){
				JSONObject obj = (JSONObject)recommendsObj.opt(i);
				Food f = new Food();
				f.setId(obj.getInt("id"));
				f.setName(obj.getString("nm"));
				f.setImage(obj.getString("img"));
				f.setShopId(shop.getId());
				recommends.add(f);
			}
			shop.setRecommends(recommends);
			JSONObject foodObj = dataObj.getJSONObject("foods");
			Iterator<Object> categories = foodObj.keys();
			HashMap<String, ArrayList<Food> > foodsMap = new HashMap<String,  ArrayList<Food> >();
			while (categories.hasNext()){
				String cat = String.valueOf(categories.next());
				JSONArray foodsObj = foodObj.getJSONArray(cat);
				ArrayList<Food> foods = new ArrayList<Food>();
				for (int i=0; i < foodsObj.length(); i++){
					Food food = new Food();
					JSONObject obj = (JSONObject)foodsObj.opt(i);
					food.setId(obj.getInt("id"));
					food.setName(obj.getString("nm"));
					food.setPrice((float)obj.getDouble("price"));
					food.setSoldCount(obj.getInt("sc"));
					food.setShopId(shop.getId());
					food.setBoxPrice((float)obj.optDouble("bp", 0.0));
					food.setCategory(cat);
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

}
