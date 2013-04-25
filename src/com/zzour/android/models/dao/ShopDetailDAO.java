package com.zzour.android.models.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.zzour.android.models.Food;
import com.zzour.android.models.ShopDetailContent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ShopDetailDAO  extends CustomSqliteHelper{
	
	// TODO database name and version should in settings file, so if upgrade, will read and re-craete table.
	private static final String DATABASE_NAME = "zzour";
	private static final int DATABASE_VERSION = 1;
	
	private static final String TABLE_NAME = "shop_detail";
	
	private static final String FIELD_ID = "id";
	private static final String FIELD_NAME = "name";
	private static final String FIELD_BANNER = "banner";
	private static final String FIELD_RATE = "rate";
	private static final String FIELD_ADDR = "address";
	private static final String FIELD_DESC = "description";
	private static final String FIELD_CATS = "cats";
	
	private Context context = null;
	
	public ShopDetailDAO(Context context){
		super(context, DATABASE_NAME, DATABASE_VERSION);
		this.context = context;
	}
	
	public void clean(int id){
		// clean food by shop id.
		this.getWritableDatabase().execSQL("DELETE FROM " + TABLE_NAME + "WHERE id = ?", new Object[]{id});
	}
	
	public void insert(ShopDetailContent shop){
		// insert recommend food
		Iterator<Food> rit = shop.getRecommends().iterator();
		RecommendFoodDAO rdao = new RecommendFoodDAO(this.context);
		int order = 0;
		while (rit.hasNext()){
			rdao.insert(rit.next(), order);
			order += 1;
		}
		// insert food
		FoodDAO fdao = new FoodDAO(this.context);
		Iterator<ArrayList<Food>> fit = shop.getFoods().values().iterator();
		order = 0;
		while (fit.hasNext()){
			Iterator<Food> ffit = fit.next().iterator();
			while (ffit.hasNext()){
				fdao.insert(ffit.next(), order);
				order += 1;
			}
		}
		ContentValues cv = new ContentValues();
		cv.put(FIELD_ID, shop.getId());
		cv.put(FIELD_NAME, shop.getName());
		cv.put(FIELD_BANNER, shop.getBanner());
		cv.put(FIELD_RATE, shop.getRate());
		cv.put(FIELD_ADDR, shop.getAddress());
		cv.put(FIELD_DESC, shop.getDesc());
		Iterator<String> it = shop.getFoods().keySet().iterator();
		String cats = "";
		while (it.hasNext()){
			if (cats.length() > 0){
				cats += ";";
			}
			cats += it.next();
		}
		cv.put(FIELD_CATS, cats);
		this.getWritableDatabase().replace(TABLE_NAME, null, cv);
		this.getWritableDatabase().close();
	}
	
	public ShopDetailContent getById(int id){
		// get recommends food
		RecommendFoodDAO rdao = new RecommendFoodDAO(this.context);
		ArrayList<Food> rfoods = rdao.getByShop(id);
		ShopDetailContent shop = new ShopDetailContent();
		shop.setRecommends(rfoods);
		
		// get shop detail from database
		Cursor c = this.getWritableDatabase().query(TABLE_NAME, null, "id = ?", 
				new String[]{String.valueOf(id)}, null, null, null, "1");
		while (c.moveToNext()){
			String name = c.getString(c.getColumnIndex(FIELD_NAME));
			String banner = c.getString(c.getColumnIndex(FIELD_BANNER));
			float rate = c.getFloat(c.getColumnIndex(FIELD_RATE));
			String addr = c.getString(c.getColumnIndex(FIELD_ADDR));
			String desc = c.getString(c.getColumnIndex(FIELD_DESC));
			String cats = c.getString(c.getColumnIndex(FIELD_CATS));
			String[] categories = cats.split(";");
			HashMap<String, ArrayList<Food> > foods = new HashMap<String, ArrayList<Food>>();
			FoodDAO fdao = new FoodDAO(this.context);
			// get foods by category
			for (int i=0; i < categories.length; i++){
				foods.put(categories[i], fdao.getByShopAndCategory(id, categories[i]));
			}
			shop.setFoods(foods);
			shop.setAddress(addr);
			shop.setBanner(banner);
			shop.setDesc(desc);
			shop.setId(id);
			shop.setName(name);
			shop.setRate(rate);
		}
		return shop;
	}
}
