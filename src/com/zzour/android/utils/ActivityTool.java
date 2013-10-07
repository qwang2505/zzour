package com.zzour.android.utils;

import java.util.ArrayList;
import java.util.HashMap;

import com.zzour.android.MainActivity;
import com.zzour.android.R;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.util.Log;

public class ActivityTool {
	
	private static final String TAG = "ZZOUR";
	private static MainActivity mainActivity = null;
	
	private static final int UNKNOW = -1;
	private static final int LEFT = 0;
	private static final int RIGHT = 1;
	
	private static final String pkgName = "com.zzour.android.";
	
	private static int nextDirection = UNKNOW;

	private static final HashMap<String, HashMap<String, Integer>> directions = new HashMap<String, HashMap<String, Integer>>(){{
		// start activity from MainActivity
		HashMap<String, Integer> main = new HashMap<String, Integer>();
		main.put("ShopDetailActivity", RIGHT);
		main.put("CollectionActivity", RIGHT);
		main.put("OrderListActivity", RIGHT);
		main.put("UnFinishedOrderListActivity", RIGHT);
		main.put("FinishedOrderListActivity", RIGHT);
		main.put("MoreActivity", RIGHT);
		put("MainActivity", main);
		
		// start activity from ShopDetailActivity
		HashMap<String, Integer> shopDetail = new HashMap<String, Integer>();
		shopDetail.put("ShoppingCartActivity", RIGHT);
		shopDetail.put("ShoppingDialActivity", RIGHT);
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
		orderList.put("CollectionActivity", LEFT);
		orderList.put("MoreActivity", RIGHT);
		orderList.put("LoginActivity", RIGHT);
		put("OrderListActivity", orderList);
		
		// start activity from CollectionActivity
		HashMap<String, Integer> store = new HashMap<String, Integer>();
		store.put("OrderListActivity", RIGHT);
		store.put("MoreActivity", RIGHT);
		put("CollectionActivity", store);
		
		// start activity from MoreActivity
		HashMap<String, Integer> more = new HashMap<String, Integer>();
		more.put("LoginActivity", RIGHT);
		more.put("RegisterActivity", RIGHT);
		more.put("SettingsActivity", RIGHT);
		more.put("AboutActivity", RIGHT);
		more.put("FeedbackActivity", RIGHT);
		more.put("CollectionActivity", LEFT);
		more.put("OrderListActivity", LEFT);
		put("MoreActivity", more);
		
		// TODO add others
	}};
	
	private static final ArrayList<String> backToMainActivities = new ArrayList<String>(){{
		add("CollectionActivity");
		add("OrderListActivity");
		add("MoreActivity");
		add("FinishedOrderListActivity");
		add("UnFinishedOrderListActivity");
	}};
	
	public static boolean shouldBackToMain(Activity activity){
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
	
	public static void startActivityForResult(Activity from, Class<?> cls, int requestCode){
		Intent intent = new Intent(from, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		from.startActivityForResult(intent, requestCode);
		overridePendingTransition(from, cls);
	}
	
	public static void startActivityForResult(Activity from, Class<?> cls, int requestCode, Intent intent){
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		from.startActivityForResult(intent, requestCode);
		overridePendingTransition(from, cls);
	}
	
	public static void startActivity(Activity from, Class<?> cls){
		Intent intent = new Intent(from, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		from.startActivity(intent);
		overridePendingTransition(from, cls);
	}
	
	public static void startActivity(Activity from, Class<?> cls, Intent intent){
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		boolean toMain = intent.getBooleanExtra("main", false);
		int tab = intent.getIntExtra("tab", 0);
		if (toMain && tab != 0){
			mainActivity.switchTab(tab);
		}
		from.startActivity(intent);
		overridePendingTransition(from, cls);
	}
	
	public static void setMainActivity(MainActivity activity){
		mainActivity = activity;
	}
	
	public static void backToMain(Activity from, Intent intent){
		if (mainActivity == null){
			Log.e("ZZOUR", "what happened? forget to set main activity?");
			return;
		}
		if (shouldBackToMain(from)){
			mainActivity.switchTab(0);
			Log.e("ZZOUR", "back to main by switch: " + from.getClass().getName());
			return;
		}
		if (intent == null){
			intent = new Intent(from, mainActivity.getClass());
		}
		Log.e("ZZOUR", "back to main by new: " + from.getClass().getName());
		intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		from.startActivity(intent);
		from.overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
		//mainActivity.switchTab(0);
	}
	
	public static MainActivity getMainActivity(){
		return mainActivity;
	}
}
