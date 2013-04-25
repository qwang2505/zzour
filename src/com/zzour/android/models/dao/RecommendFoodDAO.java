package com.zzour.android.models.dao;

import java.util.ArrayList;

import com.zzour.android.models.Food;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecommendFoodDAO  extends CustomSqliteHelper {
	
	// TODO database name and version should in settings file, so if upgrade, will read and re-craete table.
	private static final String DATABASE_NAME = "zzour";
	private static final int DATABASE_VERSION = 1;
	
	private static final String TABLE_NAME = "rcmd_food";
	
	private static final String FIELD_ID = "id";
	private static final String FIELD_SHOP_ID = "shopId";
	private static final String FIELD_CATEGORY = "category";
	private static final String FIELD_NAME = "name";
	private static final String FIELD_PRICE = "price";
	private static final String FIELD_SOLD_COUNT = "soldCount";
	private static final String FIELD_IMAGE = "image";
	private static final String FIELD_BOX_PRICE = "boxPrice";
	private static final String FIELD_ORDER = "o";
	
	public RecommendFoodDAO(Context context){
		super(context, DATABASE_NAME, DATABASE_VERSION);
	}
	
	public void clean(){
		this.getWritableDatabase().execSQL("DELETE FROM " + TABLE_NAME);
	}
	
	public void clean(int shopId){
		// clean food by shop id.
		this.getWritableDatabase().execSQL("DELETE FROM " + TABLE_NAME + "WHERE shopId = ?", new Object[]{shopId});
	}
	
	public void insert(Food food, int order){
		ContentValues cv = new ContentValues();
		cv.put(FIELD_ID, food.getId());
		cv.put(FIELD_SHOP_ID, food.getShopId());
		cv.put(FIELD_CATEGORY, food.getCategory());
		cv.put(FIELD_NAME, food.getName());
		cv.put(FIELD_PRICE, food.getPrice());
		cv.put(FIELD_SOLD_COUNT, food.getSoldCount());
		cv.put(FIELD_IMAGE, food.getImage());
		cv.put(FIELD_BOX_PRICE, food.getBoxPrice());
		cv.put(FIELD_ORDER, order);
		this.getWritableDatabase().replace(TABLE_NAME, null, cv);
		this.getWritableDatabase().close();
	}
	
	public Food getById(int id){
		Cursor c = this.getWritableDatabase().query(TABLE_NAME, null, "id = ?", 
				new String[]{String.valueOf(id)}, null, null, null, "1");
		Food food = null;
		while (c.moveToNext()){
			int foodId = c.getInt(c.getColumnIndex(FIELD_ID));
			int shopId = c.getInt(c.getColumnIndex(FIELD_SHOP_ID));
			String cat = c.getString(c.getColumnIndex(FIELD_CATEGORY));
			String name = c.getString(c.getColumnIndex(FIELD_NAME));
			float price = c.getFloat(c.getColumnIndex(FIELD_PRICE));
			int soldCount = c.getInt(c.getColumnIndex(FIELD_SOLD_COUNT));
			String image = c.getString(c.getColumnIndex(FIELD_IMAGE));
			float boxPrice = c.getFloat(c.getColumnIndex(FIELD_BOX_PRICE));
			food = new Food();
			food.setBoxPrice(boxPrice);
			food.setCategory(cat);
			food.setId(foodId);
			food.setImage(image);
			food.setName(name);
			food.setPrice(price);
			food.setShopId(shopId);
			food.setSoldCount(soldCount);
		}
		return food;
	}
	
	public ArrayList<Food> getByShop(int shopId){
		ArrayList<Food> foods = new ArrayList<Food>();
		Cursor c = this.getWritableDatabase().query(TABLE_NAME, null, "shopId = ?", 
				new String[]{String.valueOf(shopId)}, null, null, "o ASC", "1");
		while (c.moveToNext()){
			int foodId = c.getInt(c.getColumnIndex(FIELD_ID));
			String cat = c.getString(c.getColumnIndex(FIELD_CATEGORY));
			String name = c.getString(c.getColumnIndex(FIELD_NAME));
			float price = c.getFloat(c.getColumnIndex(FIELD_PRICE));
			int soldCount = c.getInt(c.getColumnIndex(FIELD_SOLD_COUNT));
			String image = c.getString(c.getColumnIndex(FIELD_IMAGE));
			float boxPrice = c.getFloat(c.getColumnIndex(FIELD_BOX_PRICE));
			Food food = new Food();
			food.setBoxPrice(boxPrice);
			food.setCategory(cat);
			food.setId(foodId);
			food.setImage(image);
			food.setName(name);
			food.setPrice(price);
			food.setShopId(shopId);
			food.setSoldCount(soldCount);
			foods.add(food);
		}
		return foods;
	}
}