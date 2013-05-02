package com.zzour.android.utils;

import java.util.ArrayList;
import java.util.HashMap;

import com.zzour.android.R;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

public class ActivityTool {
	
	private static final String TAG = "ZZOUR";
	
	private static final int UNKNOW = -1;
	private static final int LEFT = 0;
	private static final int RIGHT = 1;
	
	private static final String pkgName = "com.zzour.android.";
	
	private static int nextDirection = UNKNOW;

	private static final HashMap<String, HashMap<String, Integer>> directions = new HashMap<String, HashMap<String, Integer>>(){{
		// start activity from MainActivity
		HashMap<String, Integer> main = new HashMap<String, Integer>();
		main.put("ShopDetailActivity", RIGHT);
		main.put("StoreActivity", RIGHT);
		main.put("OrderListActivity", RIGHT);
		main.put("MoreActivity", RIGHT);
		put("MainActivity", main);
		
		// start activity from ShopDetailActivity
		HashMap<String, Integer> shopDetail = new HashMap<String, Integer>();
		shopDetail.put("ShoppingCartActivity", RIGHT);
		put("ShopDetailActivity", shopDetail);
		
		// start activity from ShoppingCartActivity
		HashMap<String, Integer> shoppingCart = new HashMap<String, Integer>();
		shoppingCart.put("OrderSucceedActivity", RIGHT);
		put("ShoppingCartActivity", shoppingCart);
		
		// start activity from OrderSucceedActivity
		HashMap<String, Integer> orderSucceed = new HashMap<String, Integer>();
		orderSucceed.put("OrderListActivity", RIGHT);
		orderSucceed.put("MainActivity", LEFT);
		put("OrderSucceedActivity", orderSucceed);
		
		// start activity from OrderListActivity
		HashMap<String, Integer> orderList = new HashMap<String, Integer>();
		orderList.put("StoreActivity", LEFT);
		orderList.put("MoreActivity", RIGHT);
		put("OrderListActivity", orderList);
		
		// start activity from StoreActivity
		HashMap<String, Integer> store = new HashMap<String, Integer>();
		store.put("OrderListActivity", RIGHT);
		store.put("MoreActivity", RIGHT);
		put("StoreActivity", store);
		
		// start activity from MoreActivity
		HashMap<String, Integer> more = new HashMap<String, Integer>();
		more.put("LoginActivity", RIGHT);
		more.put("RegisterActivity", RIGHT);
		more.put("SettingsActivity", RIGHT);
		more.put("AboutActivity", RIGHT);
		more.put("FeedbackActivity", RIGHT);
		more.put("StoreActivity", LEFT);
		more.put("OrderListActivity", LEFT);
		put("MoreActivity", more);
		
		// TODO add others
	}};
	
	private static final ArrayList<String> backToMainActivities = new ArrayList<String>(){{
		add("StoreActivity");
		add("OrderListActivity");
		add("MoreActivity");
	}};
	
	public static boolean shouldBackToMain(Activity activity){
		Log.d(TAG, "check activity should back to main: " + activity.getClass().getName());
		String name = activity.getClass().getName().replace(pkgName, "");
		if (backToMainActivities.contains(name)){
			return true;
		}
		return false;
	}
	
	public static void overridePendingTransition(Activity from, Class<?> cls){
		String fromName = from.getClass().getName().replace(pkgName, "");
		String clsName = cls.getName().replace(pkgName, "");
		if (directions.get(fromName) == null){
			return;
		}
		Integer direction = directions.get(fromName).get(clsName);
		if (direction == null){
			return;
		} else if (direction == RIGHT){
			from.overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
		} else if (direction == LEFT){
			from.overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
		}
		nextDirection = direction;
	}
	
	public static void overridePendingTransition(Activity activity){
		if (nextDirection == UNKNOW){
			return;
		} else if (nextDirection == RIGHT){
			activity.overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
		} else if (nextDirection == LEFT){
			activity.overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
		}
		nextDirection = UNKNOW;
	}
	
	public static void startActivity(Activity from, Class<?> cls){
		Intent intent = new Intent(from, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		from.startActivity(intent);
		overridePendingTransition(from, cls);
	}
	
	public static void startActivity(Activity from, Class<?> cls, Intent intent){
		Log.d(TAG, "start activity " + from.getClass().getName() + " from " + cls.getName());
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		from.startActivity(intent);
		overridePendingTransition(from, cls);
	}
}
