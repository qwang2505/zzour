package com.zzour.android.models.dao;

import com.zzour.android.models.Order;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OrderDAO extends CustomSqliteHelper {
	
	private static final String DATABASE_NAME = "zzour";
	private static final int DATABASE_VERSION = 1;
	
	private static final String TABLE_NAME = "order";
	
	private static final String FIELD_ID = "id";
	private static final String FIELD_FOODS = "foods";
	private static final String FIELD_TOTAL_BOX_PRICE = "tbp";
	private static final String FIELD_TOTAL_PRICE = "tp";
	private static final String FIELD_ADDR = "addr";
	private static final String FIELD_SEND_TIME = "st";
	private static final String FIELD_MESSAGE = "msg";
	private static final String FIELD_RESULT_MESSAGE = "rmsg";
	
	public OrderDAO(Context context){
		super(context, DATABASE_NAME, DATABASE_VERSION);
	}
	
	public void clean(){
		this.getWritableDatabase().execSQL("DELETE FROM " + TABLE_NAME);
	}
	
	public void clean(String id){
		// clean food by shop id.
		this.getWritableDatabase().execSQL("DELETE FROM " + TABLE_NAME + "WHERE id = ?", new Object[]{id});
	}
	
	public void insert(Order order){
		
	}
}
