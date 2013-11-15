package com.zzour.android.settings;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import android.os.Build;

public class GlobalSettings {
	
	/*
	 * Read ini config file, and hold all settings in this class.
	 */
	
	public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static String schema = "http://";
	public static String server = "www.zzour.com";
	public static final String PHONE = "18850547787";
	public static final String WEBSITE = "http://www.zzour.com/";
	
	// for third party account
	public static final String TENCENT_APP_ID = "100406214";
	public static final String TENCENT_APP_KEY = "ee25c4009fb5accc30951da82df8b4fd";
	public static final String TENCENT_APP_SCOPE = "get_user_info,get_simple_userinfo,get_user_profile,get_app_friends,add_share";
	
	public static final String RENREN_APP_ID = "230935";
	public static final String RENREN_API_KEY = "e44ec3d54a56421f9e07ccc83fa65125";
	public static final String RENREN_API_SECRET = "6e590838a2304222a41fe588375b13c3";
	public static final String RENREN_APP_SCOPE = "publish_share send_invitation";
	
	public static class AcccountTypes{
		public static String TENCENT = "tencent";
		public static String RENREN = "renren";
		public static String NORMAL = "zhaizhai";
	};
	
	private static HashMap<Integer, String> statusMap = new HashMap<Integer, String>(){{
		put(0, "已取消|订单状态：订单已取消");
		put(20, "待确认|订单状态：订单待确认");
		put(30, "已确认，待发货|订单状态：商家已确认订单，将及时为您配送");
		put(40, "已完成|订单状态：交易已完成");
		put(50, "退单中|订单状态：申请退单中");
		put(51, "待完成|订单状态：商家不确认退单，等待完成订单");
	}};
	
	public static String getStatusShortDesc(int id){
		if (!statusMap.containsKey(id)){
			return "未知状态";
		}
		String desc = statusMap.get(id);
		return desc.split("\\|")[0];
	}
	
	public static boolean canFinishOrder(int status){
		return status == 30;
	}
	
	public static boolean canPushOrder(int status){
		return status == 30;
	}
	
	public static boolean canCancelOrder(int status){
		return status == 30;
	}
	
	public static boolean canForceCancelOrder(int status){
		return status == 51;
	}
	
	public static String getStatusLongDesc(int id){
		if (!statusMap.containsKey(id)){
			return "订单状态：未知";
		}
		String desc = statusMap.get(id);
		return desc.split("\\|")[1];
	}

	public static long getApiExpireTime(){
		// TODO read from settings
		return 0;
	}
	
	public static String getServerAddress(){
		// TODO read from settings
		return schema + server;
	}
	
	public static int getApiLevel(){
		return Build.VERSION.SDK_INT;
	}
}
