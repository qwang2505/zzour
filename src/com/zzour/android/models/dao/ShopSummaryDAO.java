package com.zzour.android.models.dao;

import java.util.ArrayList;

import com.zzour.android.models.ShopSummaryContent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ShopSummaryDAO extends SQLiteOpenHelper {
	
	// TODO database name and version should in settings file, so if upgrade, will read and re-craete table.
	private static final String DATABASE_NAME = "zzour";
	private static final int DATABASE_VERSION = 1;
	
	private static final String TABLE_NAME = "shop_summary";
	
	private static final String FIELD_ID = "id";
	private static final String FIELD_NAME = "name";
	private static final String FIELD_IS_NEW = "new";
	private static final String FIELD_IMAGE = "image";
	private static final String FIELD_DESC = "desc";
	private static final String FIELD_RATE = "rate";
	private static final String FIELD_ORDER = "o";
	
	public ShopSummaryDAO(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE "+TABLE_NAME +" ( "+ 
						FIELD_ID +" INT, "+ 
						FIELD_NAME +" TEXT, " +
						FIELD_IS_NEW + " INT, " +
						FIELD_IMAGE + " TEXT, " +
						FIELD_DESC + " TEXT, " +
						FIELD_RATE + " INT, " +
						FIELD_ORDER + " INT " +
					");";  
        db.execSQL(sql);  
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		this.onCreate(db);
	}
	
	public void insert(ShopSummaryContent summary, int order){
		ContentValues cv = new ContentValues();
		cv.put(FIELD_ID, summary.getId());
		cv.put(FIELD_NAME, summary.getName());
		cv.put(FIELD_IS_NEW, summary.isNew() ? 1: 0);
		cv.put(FIELD_IMAGE, summary.getImage());
		cv.put(FIELD_DESC, summary.getDescription());
		cv.put(FIELD_RATE, summary.getRate());
		cv.put(FIELD_ORDER, order);
		this.getWritableDatabase().insert(TABLE_NAME, null, cv);
		this.getWritableDatabase().close();
	}
	
	/*
	 * This should not been used
	 */
	public void delete(String[] ids){
		String where = FIELD_ID + " = ?";
		String[] values = ids;
		this.getWritableDatabase().delete(TABLE_NAME, where, values);
		this.getWritableDatabase().close();
	}
	
	public void updateOrder(int increase){
		String sql = "UPDATE " + TABLE_NAME + " SET " + FIELD_ORDER + " = " + FIELD_ORDER + " + " + increase;
		this.getWritableDatabase().execSQL(sql);
		this.getWritableDatabase().close();
	}
	
	public void clean(){
		this.getWritableDatabase().execSQL("DELETE FROM " + TABLE_NAME);
	}
	
	public ArrayList<ShopSummaryContent> get(){
		return this.get(-1, 15);
	}
	
	public ArrayList<ShopSummaryContent> get(int count){
		return this.get(-1, count);
	}
	
	public ArrayList<ShopSummaryContent> get(int minOrder, int count){
		ArrayList<ShopSummaryContent> shops = new ArrayList<ShopSummaryContent>();
		Cursor cursor;
		if (minOrder < 0){
			cursor = this.getReadableDatabase().query(TABLE_NAME, null, null, null, 
					null, null, "o ASC", String.valueOf(count));
		} else {
			cursor = this.getReadableDatabase().query(TABLE_NAME, null, "o > ?", new String[]{String.valueOf(minOrder)}, 
				null, null, "o ASC", String.valueOf(count));
		}
		while (cursor.moveToNext()){
			int id = cursor.getInt(cursor.getColumnIndex(FIELD_ID));
			String name = cursor.getString(cursor.getColumnIndex(FIELD_NAME));
			boolean isNew = cursor.getInt(cursor.getColumnIndex(FIELD_IS_NEW)) == 1 ? true : false;
			String image = cursor.getString(cursor.getColumnIndex(FIELD_IMAGE));
			String desc = cursor.getString(cursor.getColumnIndex(FIELD_DESC));
			int rate = cursor.getInt(cursor.getColumnIndex(FIELD_RATE));
			int order = cursor.getInt(cursor.getColumnIndex("o"));
			ShopSummaryContent shop = new ShopSummaryContent(id, isNew, image, name, desc, rate);
			shops.add(shop);
		}
		return shops;
	}
}
