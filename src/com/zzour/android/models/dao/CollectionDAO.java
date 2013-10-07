package com.zzour.android.models.dao;

import java.util.ArrayList;
import com.zzour.android.models.ShopDetailContent;
import com.zzour.android.models.ShopSummaryContent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class CollectionDAO  extends CustomSqliteHelper{
	
	private static final String DATABASE_NAME = "zzour";
	private static final int DATABASE_VERSION = 1;
	
	private static final String TABLE_NAME = "shop_collection";
	
	private static final String FIELD_ID = "id";
	private static final String FIELD_SHOP_ID = "shop_id";
	private static final String FIELD_SHOP_NAME = "shop_name";
	private static final String FIELD_SHOP_IMAGE = "shop_image";
	private static final String FIELD_SHOP_RATING = "shop_rating";
	private static final String FIELD_SHOP_CREDIT = "shop_credit";

	public CollectionDAO(Context context){
		super(context, DATABASE_NAME, DATABASE_VERSION);
	}
	
	public void delete(int shopId){
		// clean food by shop id.
		this.getWritableDatabase().execSQL("DELETE FROM " + TABLE_NAME + " WHERE shop_id = ?", new Object[]{shopId});
		this.getWritableDatabase().close();
	}
	
	public void insert(ShopDetailContent shop){
		ContentValues cv = new ContentValues();
		cv.put(FIELD_SHOP_ID, shop.getId());
		cv.put(FIELD_SHOP_NAME, shop.getName());
		cv.put(FIELD_SHOP_IMAGE, shop.getBanner());
		cv.put(FIELD_SHOP_RATING, shop.getGrade());
		cv.put(FIELD_SHOP_CREDIT, shop.getCreditValue());
		this.getWritableDatabase().insert(TABLE_NAME, null, cv);
		this.getWritableDatabase().close();
	}
	
	public boolean exists(int shopId){
		Cursor c = this.getReadableDatabase().query(TABLE_NAME, null, "shop_id = ?",
				new String[]{String.valueOf(shopId)}, null, null, null, "1");
		int count = c.getCount();
		this.getReadableDatabase().close();
		return count > 0;
	}
	
	public ArrayList<ShopSummaryContent> get(int maxId, int count){
		ArrayList<ShopSummaryContent> shops = new ArrayList<ShopSummaryContent>();
		Cursor cursor;
		if (maxId < 0){
			cursor = this.getReadableDatabase().query(TABLE_NAME, null, null, null, 
					null, null, "id DESC", String.valueOf(count));
		} else {
			cursor = this.getReadableDatabase().query(TABLE_NAME, null, "id < ?", new String[]{String.valueOf(maxId)}, 
				null, null, "id DESC", String.valueOf(count));
		}
		while (cursor.moveToNext()){
			ShopSummaryContent shop = new ShopSummaryContent(-1, false, null, null, null, 0);
			shop.setOrder(cursor.getInt(cursor.getColumnIndex(FIELD_ID)));
			shop.setId(cursor.getInt(cursor.getColumnIndex(FIELD_SHOP_ID)));
			shop.setName(cursor.getString(cursor.getColumnIndex(FIELD_SHOP_NAME)));
			shop.setGrade(cursor.getFloat(cursor.getColumnIndex(FIELD_SHOP_RATING)));
			shop.setCreditValue(cursor.getInt(cursor.getColumnIndex(FIELD_SHOP_CREDIT)));
			shop.setLogo(cursor.getString(cursor.getColumnIndex(FIELD_SHOP_IMAGE)));
			shops.add(shop);
		}
		this.getReadableDatabase().close();
		return shops;
	}
}
