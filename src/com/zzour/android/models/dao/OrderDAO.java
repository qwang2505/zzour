package com.zzour.android.models.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.zzour.android.models.Address;
import com.zzour.android.models.Food;
import com.zzour.android.models.Order;
import com.zzour.android.models.OrderSummary;
import com.zzour.android.settings.GlobalSettings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OrderDAO extends CustomSqliteHelper {
	
	private static final String DATABASE_NAME = "zzour";
	private static final int DATABASE_VERSION = 1;
	
	private static final String TABLE_NAME = "orderTable";
	
	private static final String FIELD_ID = "id";
	private static final String FIELD_FOODS = "foods";
	private static final String FIELD_IMAGE = "image";
	private static final String FIELD_NAMES = "shopNames";
	private static final String FIELD_TIME = "t";
	private static final String FIELD_TOTAL_BOX_PRICE = "tbp";
	private static final String FIELD_TOTAL_PRICE = "bp";
	private static final String FIELD_ADDR = "addr";
	private static final String FIELD_SEND_TIME = "st";
	private static final String FIELD_MESSAGE = "msg";
	private static final String FIELD_RESULT_MESSAGE = "rmsg";
	
	public OrderDAO(Context context){
		super(context, DATABASE_NAME, DATABASE_VERSION);
	}
	
	public void clean(){
		this.getWritableDatabase().execSQL("DELETE FROM " + TABLE_NAME);
		this.getWritableDatabase().close();
	}
	
	public void clean(String id){
		// clean food by shop id.
		this.getWritableDatabase().execSQL("DELETE FROM " + TABLE_NAME + "WHERE id = ?", new Object[]{id});
		this.getWritableDatabase().close();
	}
	
	public void insert(Order order){
		ContentValues cv = new ContentValues();
		cv.put(FIELD_ID, order.getId());
		// put foods
		String foodStr = "";
		Iterator<Integer> shopIds = order.getShops();
		int firstShopId = -1;
		String names = "";
		while (shopIds.hasNext()){
			if (foodStr.length() > 0){
				// not first shop, add seperator
				foodStr += "|";
			}
			int shopId = shopIds.next();
			if (firstShopId == -1){
				firstShopId = shopId;
			}
			String name = order.getShopName(shopId);
			if (names.length() == 0){
				names += name;
			} else {
				names += ", " + name;
			}
			foodStr += shopId + ":" + name + ":";
			Iterator<Food> foods = order.getFoods(shopId);
			boolean first = true;
			while (foods.hasNext()){
				if (first){
					// first food, add seperator
					first = false;
				} else {
					foodStr += ";";
				}
				Food food = foods.next();
				foodStr += food.toString();
			}
		}
		cv.put(FIELD_FOODS, foodStr);
		cv.put(FIELD_IMAGE, order.getShopImage(firstShopId));
		cv.put(FIELD_NAMES, names);
		cv.put(FIELD_TIME, GlobalSettings.TIME_FORMAT.format(order.getTime()));
		cv.put(FIELD_TOTAL_BOX_PRICE, order.getTotalBoxPrice());
		cv.put(FIELD_TOTAL_PRICE, order.getTotalPrice());
		cv.put(FIELD_ADDR, order.getAddress().toString());
		cv.put(FIELD_SEND_TIME, order.getSendTime());
		cv.put(FIELD_MESSAGE, order.getMessage());
		cv.put(FIELD_RESULT_MESSAGE, order.getResultMsg());
		this.getWritableDatabase().replace(TABLE_NAME, null, cv);
		this.getWritableDatabase().close();
	}
	
	public ArrayList<OrderSummary> getTodayOrders(int count, String from){
		ArrayList<OrderSummary> orders = new ArrayList<OrderSummary>();
		Cursor cursor;
		Date now = new Date();
		now.setHours(0);
		now.setMinutes(0);
		now.setSeconds(0);
		String today = GlobalSettings.TIME_FORMAT.format(now);
		if (from == null){
			cursor = this.getReadableDatabase().query(TABLE_NAME, new String[]{FIELD_ID, FIELD_IMAGE, FIELD_NAMES, FIELD_TOTAL_PRICE, FIELD_TIME},
					"t >= ?", new String[]{today}, null, null, "t DESC", String.valueOf(count));
		} else {
			cursor = this.getReadableDatabase().query(TABLE_NAME, new String[]{FIELD_ID, FIELD_IMAGE, FIELD_NAMES, FIELD_TOTAL_PRICE, FIELD_TIME}, 
					"t < ? and t >= ?", new String[]{from, today}, null, null, "t DESC", String.valueOf(count));
		}
		while (cursor.moveToNext()){
			OrderSummary os = new OrderSummary();
			os.setId(cursor.getString(cursor.getColumnIndex(FIELD_ID)));
			os.setImage(cursor.getString(cursor.getColumnIndex(FIELD_IMAGE)));
			os.setPrice(cursor.getFloat(cursor.getColumnIndex(FIELD_TOTAL_PRICE)));
			os.setShopName(cursor.getString(cursor.getColumnIndex(FIELD_NAMES)));
			os.setTime(cursor.getString(cursor.getColumnIndex(FIELD_TIME)));
			orders.add(os);
		}
		this.getReadableDatabase().close();
		return orders;
	}
	
	public ArrayList<OrderSummary> getHistoryOrders(int count, String from){
		ArrayList<OrderSummary> orders = new ArrayList<OrderSummary>();
		if (from == null){
			Date now = new Date();
			now.setHours(0);
			now.setMinutes(0);
			now.setSeconds(0);
			from = GlobalSettings.TIME_FORMAT.format(now);
		}
		Cursor cursor = this.getReadableDatabase().query(TABLE_NAME, new String[]{FIELD_ID, FIELD_IMAGE, FIELD_NAMES, FIELD_TOTAL_PRICE, FIELD_TIME}, 
				"t < ?", new String[]{from}, null, null, "t DESC", String.valueOf(count));
		while (cursor.moveToNext()){
			OrderSummary os = new OrderSummary();
			os.setId(cursor.getString(cursor.getColumnIndex(FIELD_ID)));
			os.setImage(cursor.getString(cursor.getColumnIndex(FIELD_IMAGE)));
			os.setPrice(cursor.getFloat(cursor.getColumnIndex(FIELD_TOTAL_PRICE)));
			os.setShopName(cursor.getString(cursor.getColumnIndex(FIELD_NAMES)));
			os.setTime(cursor.getString(cursor.getColumnIndex(FIELD_TIME)));
			orders.add(os);
		}
		this.getReadableDatabase().close();
		return orders;
	}
	
	public Order getOrder(String id){
		Order order = new Order();
		Cursor cursor = this.getReadableDatabase().query(TABLE_NAME, null, "id = ?", new String[]{id}, null, null, null, String.valueOf(1));
		boolean found = false;
		while (cursor.moveToNext()){
			found = true;
			order.setTotalPrice(cursor.getFloat(cursor.getColumnIndex(FIELD_TOTAL_PRICE)));
			order.setAddress(new Address(cursor.getString(cursor.getColumnIndex(FIELD_ADDR))));
			order.setSendTime(cursor.getString(cursor.getColumnIndex(FIELD_SEND_TIME)));
			order.setMessage(cursor.getString(cursor.getColumnIndex(FIELD_MESSAGE)));
			order.setResultMsg(cursor.getString(cursor.getColumnIndex(FIELD_RESULT_MESSAGE)));
			// TODO set foods
			String foodStr = cursor.getString(cursor.getColumnIndex(FIELD_FOODS));
			String[] shops = foodStr.split("\\|");
			for (int i=0; i < shops.length; i++){
				String[] fs = shops[i].split(":");
				if (fs.length != 3){
					return null;
				}
				int shopId = Integer.valueOf(fs[0]);
				String shopName = fs[1];
				String[] foods = fs[2].split(";");
				for (int j=0; j < foods.length; j++){
					order.addFood(shopId, shopName, null, new Food(foods[j]));
				}
			}
		}
		this.getReadableDatabase().close();
		if (!found){
			return null;
		}
		return order;
	}
}
