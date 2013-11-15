package com.zzour.android.network.api;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

import com.zzour.android.models.Food;
import com.zzour.android.models.ShopDetailContent;
import com.zzour.android.settings.GlobalSettings;

public class ShopDetailApi {
	
	private static HashMap<Integer, ShopDetailContent> mShopDetailMap = new HashMap<Integer, ShopDetailContent>();
	
	private static String path = "/index.php?app=store&method=ajax&";
	
	public static ShopDetailContent getShopDetailById(int id, Activity activity){
		// if record in detail map, just return.
		// TODO read in memory
		//if (mShopDetailMap.containsKey(id)){
		//	return mShopDetailMap.get(id);
		//}
		
		// TODO if not in detail map, check cache
		
		// if not in local cache, get from server
		//String data = FakeData.getFakeShopDetailById(id);
		String data = getShopDetailFromServer(id, 0);
		if (data == null){
			return null;
		}
		//ShopSummaryDAO m = new ShopSummaryDAO(activity);
		//ShopSummaryContent shopSummary = m.getById(id);
		// TODO save cache data to database
		return parseShopDetailData(data);
	}
	
	private static String getShopDetailFromServer(int shopId, long lastAccess){
		String src = buildUrl(shopId, lastAccess);
		Log.e("ZZOUR", "get data from: " + src);
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
	
	private static String buildUrl(int shopId, long lastAccess){
		return GlobalSettings.getServerAddress() + path + "&la=" + lastAccess  + "&id=" + shopId;
	}
	
	private static ShopDetailContent parseShopDetailData(String data){
		ShopDetailContent shop = new ShopDetailContent();
		try {
			JSONObject dataObj = new JSONObject(data);
			String error = dataObj.getString("msg");
			boolean done = dataObj.getBoolean("done");
			if (!done){
				// TODO log error and show error message
				return null;
			}
			// get store info
			JSONObject retValueObj = dataObj.getJSONObject("retval");
			JSONObject storeObj = retValueObj.getJSONObject("store");
			shop.setDesc(storeObj.getString("description"));
			shop.setId(storeObj.getInt("store_id"));
			shop.setName(storeObj.getString("store_name"));
			shop.setOnlineOrder(storeObj.getInt("onlineOrder") == 1);
			shop.setSendTime(storeObj.getString("send_time"));
			shop.setGoodsCount(storeObj.getInt("goods_count"));
			shop.setPraiseRate((float)storeObj.getDouble("praise_rate"));
			shop.setCreditValue(storeObj.getInt("recommand"));
			shop.setLive(storeObj.getInt("if_live") == 1);
			shop.setShopHours(storeObj.getString("shop_hours"));
			shop.setTelephone(storeObj.getString("tel"));
			shop.setNotice(storeObj.getString("notice"));
			
			int headcount = storeObj.getInt("headcount");
			if (headcount <= 0){
				headcount = 1;
			}
			shop.setScore((float)storeObj.getDouble("score"));
			shop.setDeliciousRate((float)storeObj.getDouble("delicious") / headcount);
			shop.setServiceRate((float)storeObj.getDouble("service") / headcount);
			shop.setSpeed(storeObj.getInt("speed") / headcount);
			
			//shop.setGrade((float)storeObj.getDouble("sgrade"));
			shop.setGrade(4);
			// basic information
			shop.setAddress(storeObj.getString("address"));
			// TODO format image correctly
			shop.setBanner("http://www.zzour.com/" + storeObj.getString("store_logo"));
			// get food info
			JSONObject categoryObjs = retValueObj.getJSONObject("categoryList");
			JSONArray categoryIds = categoryObjs.names();
			ArrayList<Category> cats = new ArrayList<Category>();
			for (int j=0; j < categoryIds.length(); j++){
				Category cat = new Category();
				JSONObject categoryObj = (JSONObject)categoryObjs.get(categoryIds.getString(j));
				cat.id = Integer.parseInt(categoryIds.getString(j));
				cat.order = categoryObj.getInt("sort_order");
				cats.add(cat);
			}
			// order cats by sort order
			Collections.sort(cats, comparator);
			LinkedHashMap<String, ArrayList<Food> > foodsMap = new LinkedHashMap<String,  ArrayList<Food> >();
			for (int j=0; j < cats.size(); j++){
				JSONObject categoryObj = (JSONObject)categoryObjs.get(cats.get(j).id + "");
				String catName = categoryObj.getString("cate_name");
				JSONObject foodObjs = categoryObj.getJSONObject("goods");
				JSONArray foodIds = foodObjs.names();
				ArrayList<Food> foods = new ArrayList<Food>();
				for (int i=0; i < foodIds.length(); i++){
					Food food = new Food();
					JSONObject obj = (JSONObject)foodObjs.get(foodIds.getString(i));;
					food.setId(obj.getInt("goods_id"));
					food.setSpecId(obj.getInt("default_spec"));
					food.setName(obj.getString("goods_name"));
					food.setPrice((float)obj.getDouble("price"));
					food.setSoldCount(obj.getInt("sales"));
					food.setShopId(shop.getId());
					food.setBoxPrice((float)obj.optDouble("bp", 0.0));
					food.setCategory(catName);
					foods.add(food);
				}
				foodsMap.put(catName, foods);
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
	
	private static class Category{
		public Integer id;
		public Integer order;
	}
	
	private static Comparator<Category> comparator = new Comparator<Category>(){
		@Override
		public int compare(Category c1, Category c2) {
			return c1.order - c2.order;
		}
	};
}
