package com.zzour.android.models.dao;

import java.util.ArrayList;

import com.zzour.android.models.Address;
import com.zzour.android.models.ShopSummaryContent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AddressDAO extends SQLiteOpenHelper {
	
	// TODO database name and version should in settings file, so if upgrade, will read and re-craete table.
	private static final String DATABASE_NAME = "zzour";
	private static final int DATABASE_VERSION = 1;
	
	private static final String TABLE_NAME = "address";
	
	private static final String FIELD_NAME = "name";
	private static final String FIELD_ADDR = "addr";
	private static final String FIELD_PHONE = "phone";
	
	public AddressDAO(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE "+TABLE_NAME +" ( "+ 
				FIELD_NAME +" TEXT, " +
				FIELD_ADDR + " TEXT, " +
				FIELD_PHONE + " TEXT " +
			");";  
		db.execSQL(sql);  
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		this.onCreate(db);
	}
	
	public void insert(Address address){
		ContentValues cv = new ContentValues();
		cv.put(FIELD_NAME, address.getName());
		cv.put(FIELD_ADDR, address.getAddr());
		cv.put(FIELD_PHONE, address.getPhone());
		this.getWritableDatabase().insert(TABLE_NAME, null, cv);
		this.getWritableDatabase().close();
	}
	
	public void delete(Address address){
		String where = FIELD_NAME + " = ? and " + FIELD_ADDR + " = ? and " + FIELD_PHONE + " = ?";
		String[] values = new String[]{address.getName(), address.getAddr(), address.getPhone()};
		this.getWritableDatabase().delete(TABLE_NAME, where, values);
		this.getWritableDatabase().close();
	}
	
	public ArrayList<Address> get(){
		ArrayList<Address> addrs = new ArrayList<Address>();
		Cursor cursor = this.getReadableDatabase().query(TABLE_NAME, null, null, null, null, null, null, null);
		while (cursor.moveToNext()){
			String name = cursor.getString(cursor.getColumnIndex(FIELD_NAME));
			String addr = cursor.getString(cursor.getColumnIndex(FIELD_ADDR));
			String phone = cursor.getString(cursor.getColumnIndex(FIELD_PHONE));
			Address address = new Address(name, phone, addr);
			addrs.add(address);
		}
		return addrs;
	}
}
