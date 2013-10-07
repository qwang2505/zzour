package com.zzour.android.network.api;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.zzour.android.models.Region;
import com.zzour.android.settings.GlobalSettings;
import com.zzour.android.settings.LocalPreferences;

public class RegionApi {
	private static String regionPath = "/index.php?app=mlselection&type=region";
	
	private static String buildRegionUrl(int parentId){
		return GlobalSettings.getServerAddress() + regionPath + "&pid=" + parentId;
	}
	
	public static ArrayList<Region> getRegions(int pid){
		String src = buildRegionUrl(pid);
		Log.e("ZZOUR", "get data from " + src);
		InputStream content = null;
		String data = null;
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
		    HttpResponse response = httpclient.execute(new HttpGet(src));
		    content = response.getEntity().getContent();
		    byte[] buf = readInputStream(content);
		    data = (new String(buf));
		} catch (Exception e) {
		    Log.e("ZZOUR", "Network exception", e);
		    data = null;
		}
		if (data == null){
			Log.e("ZZOUR", "get order form from server faield");
			return null;
		}
		return parseRegionResult(data);
	}
	
	private static ArrayList<Region> parseRegionResult(String data){
		try {
			Log.e("ZZOUR", "add response data: " + data);
			JSONObject dataObj = new JSONObject(data);
			boolean success = dataObj.getBoolean("done");
			if (!success){
				Log.e("ZZOUR", "load regions failed, success is false");
				return null;
			}
			JSONArray regionObjs = dataObj.getJSONArray("retval");
			ArrayList<Region> regions = new ArrayList<Region>();
			for (int i=0; i < regionObjs.length(); i++){
				JSONObject regionObj = regionObjs.getJSONObject(i);
				Region r = new Region();
				r.setId(regionObj.getInt("region_id"));
				r.setName(regionObj.getString("region_name"));
				r.setPid(regionObj.getInt("parent_id"));
				r.setOrder(regionObj.getInt("sort_order"));
				regions.add(r);
			}
			return regions;
		} catch (JSONException ex){
			Log.e("ZZOUR", "error in parse add all into cart result:¡¡" + ex);
			return null;
		}
	}
	
	private static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {  
            outStream.write(buffer, 0, len);  
        }  
        inStream.close();  
        return outStream.toByteArray();  
    }

}
