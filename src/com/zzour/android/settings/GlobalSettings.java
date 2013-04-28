package com.zzour.android.settings;

import java.text.SimpleDateFormat;

import android.os.Build;

public class GlobalSettings {
	
	/*
	 * Read ini config file, and hold all settings in this class.
	 */
	
	public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static String schema = "http://";

	public static long getApiExpireTime(){
		// TODO read from settings
		return 300000;
	}
	
	public static String getServerAddress(){
		// TODO read from settings
		return schema + "10.2.9.0";
	}
	
	public static int getApiLevel(){
		return Build.VERSION.SDK_INT;
	}
}
