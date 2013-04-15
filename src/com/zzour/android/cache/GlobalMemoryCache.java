package com.zzour.android.cache;

import java.util.HashMap;

public class GlobalMemoryCache {

	private static HashMap<String, Object> cache = new HashMap<String, Object>();

	public static void put(String key, Object value){
		cache.put(key, value);
	}
	
	public static Object get(String key){
		if (!cache.containsKey(key)){
			return null;
		}
		return cache.get(key);
	}
	
	public static void clearMemory(){
		cache.clear();
	}
}
