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

public class ShopDetailApi {
	
	private static HashMap<Integer, ShopDetailContent> mShopDetailMap = new HashMap<Integer, ShopDetailContent>();
	
	public static ShopDetailContent getShopDetailById(int id){
		// if record in detail map, just return.
		if (mShopDetailMap.containsKey(id)){
			return mShopDetailMap.get(id);
		}
		
		// TODO if not in detail map, check cache
		
		// if not in local cache, get from server
		String data = FakeData.getFakeShopDetailById(id);
		return parseShopDetailData(data);
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
				f.setShopId(shop.getId());
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
					food.setShopId(shop.getId());
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
