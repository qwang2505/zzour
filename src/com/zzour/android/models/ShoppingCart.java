package com.zzour.android.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/*
 * This class is for save and get shopping cart information. Also include save cart information
 * to storage, and load from storage.
 */
// TODO add pending foods information into local storage logic.
public class ShoppingCart {

	private static HashMap<Integer, HashMap<Integer, Food>> mProductMap = new HashMap<Integer, HashMap<Integer, Food>>();
	private static HashMap<Integer, String> mShopNames = new HashMap<Integer, String>();
	private static HashMap<Integer, String> mShopImages = new HashMap<Integer, String>();
	
	public static void saveFoods(ShopDetailContent shop, ArrayList<Food> foods){
		// save name
		if (!mShopNames.containsKey(shop.getId())){
			mShopNames.put(shop.getId(), shop.getName());
		}
		if (!mShopImages.containsKey(shop.getId())){
			mShopImages.put(shop.getId(), shop.getBanner());
		}
		// save foods
		Iterator<Food> it = foods.iterator();
		while (it.hasNext()){
			Food f = it.next();
			HashMap<Integer, Food> fs = mProductMap.get(shop.getId());
			if (fs == null){
				fs = new HashMap<Integer, Food>();
				mProductMap.put(shop.getId(), fs);
			}
			if (fs.containsKey(f.getId())){
				// if already in map, reset buy count
				Food f1 = fs.get(f.getId());
				f1.setBuyCount(f.getBuyCount());
			} else {
				fs.put(f.getId(), f);
			}
		}
	}

	public static Iterator<Integer> getShops(){
		return mProductMap.keySet().iterator();
	}
	
	public static Iterator<Integer> getFoods(int shopId){
		return mProductMap.get(shopId).keySet().iterator();
	}
	
	public static Food getFood(int shopId, int foodId){
		return mProductMap.get(shopId).get(foodId);
	}
	
	public static String getShopName(int shopId){
		return mShopNames.get(shopId);
	}
	
	public static String getShopImage(int shopId){
		return mShopImages.get(shopId);
	}
	
	public static int getShopsCount(){
		return mProductMap.keySet().size();
	}
	
	public static String getStringId(int shopId, int foodId){
		return shopId + ";" + foodId;
	}
	
	public static boolean deleteFood(int shopId, int foodId){
		mProductMap.get(shopId).remove(foodId);
		if (mProductMap.get(shopId).isEmpty()){
			mProductMap.remove(shopId);
			return true;
		} else {
			return false;
		}
	}
	
	public static Food getFoodByStringId(String ids){
		String[] iids = ids.split(";");
		// assume ids must be right
		int shopId = Integer.valueOf(iids[0]);
		int foodId = Integer.valueOf(iids[1]);
		return mProductMap.get(shopId).get(foodId);
	}
	
	public static boolean deleteFood(String ids){
		String[] iids = ids.split(";");
		// assume ids must be right
		int shopId = Integer.valueOf(iids[0]);
		int foodId = Integer.valueOf(iids[1]);
		mProductMap.get(shopId).remove(foodId);
		if (mProductMap.get(shopId).isEmpty()){
			mProductMap.remove(shopId);
			return true;
		} else {
			return false;
		}
	}
	
	public static void setBuyCount(int shopId, int foodId, int count){
		mProductMap.get(shopId).get(foodId).setBuyCount(count);
	}
	
	public static void clear(){
		// clear shopping cart
		mShopNames.clear();
		mShopImages.clear();
		mProductMap.clear();
	}
}
