package com.zzour.android.models.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CustomSqliteHelper extends SQLiteOpenHelper {

	public CustomSqliteHelper(Context context, String db_name, int db_version){
		super(context, db_name, null, db_version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// create address table
		//db.execSQL("CREATE TABLE IF NOT EXISTS address (name TEXT, addr TEXT, phone TEXT);");
		// create food table
		//db.execSQL("CREATE TABLE IF NOT EXISTS food (id INT PRIMARY KEY, shopId INT, category TEXT, name TEXT, price FLOAT, soldCount INT, image TEXT, boxPrice FLOAT, o INT);");
		// create order table
		//db.execSQL("CREATE TABLE IF NOT EXISTS orderTable (id TEXT PRIMARY KEY, foods TEXT, image TEXT, shopNames TEXT, t DATETIME, tbp FLOAT, bp FLOAT, addr TEXT, st TEXT, msg TEXT, rmsg TEXT);");
		// create recommends food table
		//db.execSQL("CREATE TABLE IF NOT EXISTS rcmd_food (id INT PRIMARY KEY, shopId INT, category TEXT, name TEXT, price FLOAT, soldCount INT, image TEXT, boxPrice FLOAT, o INT);");
		// create shop detail table
		//db.execSQL("CREATE TABLE IF NOT EXISTS shop_detail (id INT PRIMARY KEY, name TEXT, banner TEXT, rate FLOAT, address TEXT, description TEXT, cats TEXT);");
		// create shop summary table
		//db.execSQL("CREATE TABLE IF NOT EXISTS shop_summary (id INT PRIMARY KEY, name TEXT, new INT, image TEXT, desc TEXT, rate FLOAT, o INT);");
		
		// create table for shop collections
		db.execSQL("CREATE TABLE IF NOT EXISTS shop_collection (id INTEGER PRIMARY KEY AUTOINCREMENT, shop_id INT, shop_name TEXT, shop_image TEXT, shop_rating FLOAT, shop_credit INT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//db.execSQL("DROP TABLE IF EXISTS address");
		//db.execSQL("DROP TABLE IF EXISTS food");
		//db.execSQL("DROP TABLE IF EXISTS rcmd_food");
		//db.execSQL("DROP TABLE IF EXISTS shop_detail");
		//db.execSQL("DROP TABLE IF EXISTS shop_summary");
		db.execSQL("DROP TABLE IF EXISTS shop_collection");
		this.onCreate(db);
	}
}
