package com.zzour.android.network.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.zzour.android.models.Address;
import com.zzour.android.models.Food;
import com.zzour.android.models.Order;
import com.zzour.android.models.OrderFormResult;
import com.zzour.android.models.ApiResult;
import com.zzour.android.models.ShipMethod;
import com.zzour.android.models.SimpleOrder;
import com.zzour.android.models.User;
import com.zzour.android.models.dao.OrderDAO;
import com.zzour.android.settings.GlobalSettings;

public class OrderApi {
	
	private static String addAllPath = "/index.php?app=cart&act=addAlls&method=ajax";
	private static String addPath = "/index.php?app=cart&act=add&method=ajax";
	private static String orderFormPath = "/index.php?app=order&goods=cart&method=ajax";
	private static String clearCartPath = "/index.php?app=cart&act=clearCart";
	private static String orderPath = "/index.php?app=order&goods=cart&method=ajax";
	private static String removeFoodPath = "/index.php?app=cart&act=drop&method=ajax";
	private static String updateFoodPath = "/index.php?app=cart&act=update";
	private static final String sessionName = "ECM_ID";
	
	private static String buildOrderFormUrl(int shopId){
		return GlobalSettings.getServerAddress() + orderFormPath + "&store_id=" + shopId;
	}
	
	public static OrderFormResult getOrderForm(int storeId, User user){
		String src = buildOrderFormUrl(storeId);
		Log.e("ZZOUR", "get data from " + src);
		InputStream content = null;
		String data = null;
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
		    CookieStore cookieStore = httpclient.getCookieStore();
		    BasicClientCookie cookie = new BasicClientCookie(sessionName, user.getSession());
		    cookie.setDomain(GlobalSettings.server);
		    cookie.setPath("/");
		    cookieStore.addCookie(cookie);
		    httpclient.setCookieStore(cookieStore);
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
		return parseOrderFormResult(data);
	}
	
	private static OrderFormResult parseOrderFormResult(String data){
		OrderFormResult result = new OrderFormResult();
		Log.e("ZZOUR", "order form response data: " + data);
		try {
			//JSONTokener jsonObj = new JSONTokener(data);
			//JSONObject dataObj = (JSONObject)jsonObj.nextValue();
			JSONObject dataObj = new JSONObject(data);
			boolean success = dataObj.getBoolean("done");
			if (!success){
				Log.e("ZZOUR", "get order form failed: success is false");
				return null;
			}
			JSONObject retval = dataObj.getJSONObject("retval");
			JSONObject goodsInfo = retval.getJSONObject("goods_info");
			JSONObject items = goodsInfo.getJSONObject("items");
			JSONArray foodIds = items.names();
			ArrayList<Food> foods = new ArrayList<Food>();
			for (int i=0; i < foodIds.length(); i++){
				JSONObject foodObj = (JSONObject)items.get(foodIds.getString(i));
				Food food = new Food();
				food.setId(foodObj.getInt("goods_id"));
				food.setRecId(foodObj.getInt("rec_id"));
				food.setSpecId(foodObj.getInt("spec_id"));
				food.setName(foodObj.getString("goods_name"));
				food.setPrice((float)foodObj.getDouble("price"));
				food.setBuyCount(foodObj.getInt("quantity"));
				foods.add(food);
			}
			ArrayList<Address> addrs = new ArrayList<Address>();
			JSONObject form = retval.getJSONObject("form");
			try {
				JSONObject addrObjs = form.getJSONObject("my_address");
				JSONArray addrIds = addrObjs.names();
				for (int i=0; i < addrIds.length(); i++){
					JSONObject addrObj = (JSONObject)addrObjs.get(addrIds.getString(i));
					String name = addrObj.getString("consignee");
					String addrStr = addrObj.getString("region_name") + addrObj.getString("address");
					String phone = addrObj.getString("phone_mob");
					int regionId = addrObj.getInt("region_id");
					Address addr = new Address();
					addr.setAddr(addrStr);
					addr.setName(name);
					addr.setPhone(phone);
					addr.setRegionId(regionId);
					addrs.add(addr);
				}
			} catch (Exception e){
				Log.e("ZZOUR", "get address failed");
			}
			JSONObject shipMethodsObj = form.getJSONObject("shipping_methods");
			JSONArray methodsIds = shipMethodsObj.names();
			ArrayList<ShipMethod> methods = new ArrayList<ShipMethod>();
			for (int i=0; i < methodsIds.length(); i++){
				ShipMethod method = new ShipMethod();
				JSONObject methodObj = (JSONObject)shipMethodsObj.get(methodsIds.getString(i));
				method.setId(methodObj.getInt("shipping_id"));
				method.setName(methodObj.getString("shipping_name"));
				methods.add(method);
			}
			result.setAddrs(addrs);
			result.setFoods(foods);
			result.setShipMethods(methods);
			return result;
		} catch (JSONException ex){
			Log.e("ZZOUR", "error in parse add all into cart result:　" + ex);
			return null;
		}
	}
	
	public static boolean addAll(ArrayList<Food> foods, User user){
		Iterator<Food> it = foods.iterator();
		String specIds = "{";
		String count = "{";
		boolean first = true;
		while (it.hasNext()){
			Food food = it.next();
			if (first){
				specIds += food.getSpecId();
				count += food.getBuyCount();
				first = false;
			} else {
				specIds += "," + food.getSpecId();
				count += "," + food.getBuyCount();
			}
		}
		specIds += "}";
		count += "}";
		//String data = postAddAll(specIds, count, user.getSession());
		String data = postAddAll2(foods, user.getSession());
		return parseResult(data);
	}
	
	public static boolean add(ArrayList<Food> foods, User user){
		Iterator<Food> it = foods.iterator();
		int specId = 0;
		int count = 0;
		while (it.hasNext()){
			Food food = it.next();
			specId = food.getSpecId();
			count = food.getBuyCount();
			break;
		}
		String data = null;
		if (count != 0){
			data = postAdd(specId, count, user.getSession());
		}
		return parseResult(data);
	}
	
	public static ApiResult add(Food food, User user){
		int specId = food.getSpecId();
		int count = food.getBuyCount();
		String data = null;
		if (count != 0){
			data = postAdd(specId, count, user.getSession());
		}
		return parseAddResult(data);
	}
	
	private static ApiResult parseAddResult(String data){
		if (data == null || data.length() == 0){
			return null;
		}
		try {
			Log.e("ZZOUR", "remove food response data: " + data);
			JSONObject dataObj = new JSONObject(data);
			boolean success = dataObj.getBoolean("done");
			String msg = dataObj.getString("msg");
			ApiResult result = new ApiResult();
			result.setMsg(msg);
			result.setSuccess(success);
			return result;
		} catch (JSONException ex){
			Log.e("ZZOUR", "error in parse remove food result:　" + ex);
			return null;
		}
	}
	
	private static String buildAddUrl(int specId, int count){
		return GlobalSettings.getServerAddress() + addPath + "&spec_id=" + specId + "&quantity=" + count;
	}
	
	private static String postAdd(int specId, int count, String session){
		String src = buildAddUrl(specId, count);
		Log.e("ZZOUR", "get data from " + src);
		InputStream content = null;
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
		    CookieStore cookieStore = httpclient.getCookieStore();
		    BasicClientCookie cookie = new BasicClientCookie(sessionName, session);
		    cookie.setDomain(GlobalSettings.server);
		    cookie.setPath("/");
		    cookieStore.addCookie(cookie);
		    httpclient.setCookieStore(cookieStore);
		    HttpResponse response = httpclient.execute(new HttpGet(src));
		    content = response.getEntity().getContent();
		    byte[] buf = readInputStream(content);
		    return (new String(buf));
		} catch (Exception e) {
		    Log.e("ZZOUR", "Network exception", e);
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
	
	private static boolean parseResult(String data){
		try {
			Log.e("ZZOUR", "add response data: " + data);
			//JSONTokener jsonObj = new JSONTokener(data);
			//JSONObject dataObj = (JSONObject)jsonObj.nextValue();
			JSONObject dataObj = new JSONObject(data);
			boolean success = dataObj.getBoolean("done");
			return success;
		} catch (JSONException ex){
			Log.e("ZZOUR", "error in parse add all into cart result:　" + ex);
			return false;
		}
	}
	
	private static String postAddAll2(ArrayList<Food> foods, String session){
	    // Create a new HttpClient and Post Header
		DefaultHttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(buildAddAllUrl());
	    try {
	        // Add your data
	    	int count = foods.size() * 2;
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(count);
	        for (int i=0; i < foods.size(); i++){
	        	Log.e("ZZOUR", "add params");
		        nameValuePairs.add(new BasicNameValuePair("ids[]", foods.get(i).getSpecId() + ""));
		        nameValuePairs.add(new BasicNameValuePair("quantitys[]", foods.get(i).getBuyCount() + ""));
	        }
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        CookieStore cookieStore = httpclient.getCookieStore();
	        BasicClientCookie cookie = new BasicClientCookie(sessionName, session);
		    cookie.setDomain(GlobalSettings.server);
		    cookie.setPath("/");
		    cookieStore.addCookie(cookie);
		    httpclient.setCookieStore(cookieStore);

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        if (response != null){
	        	return EntityUtils.toString(response.getEntity());
	        } else {
	        	Log.e("ZZOUR", "response is null");
	        	return null;
	        }
	    } catch (ClientProtocolException e) {
	        // Auto-generated catch block
	    	Log.e("ZZOUR", "Add to cart failed with exception: " + e);
	    	return null;
	    } catch (IOException e) {
	        // Auto-generated catch block
	    	Log.e("ZZOUR", "Add to cart failed with exception: " + e);
	    	return null;
	    }
	}
	
	private static String buildClearCartUrl(){
		return GlobalSettings.getServerAddress() + clearCartPath;
	}
	
	private static ApiResult parseClearCartResult(String data){
		if (data == null || data.length() == 0){
			return null;
		}
		try {
			Log.e("ZZOUR", "add response data: " + data);
			JSONObject dataObj = new JSONObject(data);
			boolean success = dataObj.getBoolean("done");
			String msg = dataObj.getString("msg");
			ApiResult result = new ApiResult();
			result.setSuccess(success);
			result.setMsg(msg);
			return result;
		} catch (JSONException ex){
			Log.e("ZZOUR", "error in parse add all into cart result:　" + ex);
			return null;
		}
	}
	
	public static ApiResult clearCart(User user){
		String src = buildClearCartUrl();
		Log.e("ZZOUR", "get data from " + src);
		InputStream content = null;
		String data = null;
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
		    CookieStore cookieStore = httpclient.getCookieStore();
		    BasicClientCookie cookie = new BasicClientCookie(sessionName, user.getSession());
		    cookie.setDomain(GlobalSettings.server);
		    cookie.setPath("/");
		    cookieStore.addCookie(cookie);
		    httpclient.setCookieStore(cookieStore);
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
		return parseClearCartResult(data);
	}
	    
	private static String buildAddAllUrl(){
		return GlobalSettings.getServerAddress() + addAllPath;
	}
	
	private static String buildOrderApi(int shopId){
		return GlobalSettings.getServerAddress() + orderPath + "&store_id=" + shopId;
	}

	public static ApiResult order(SimpleOrder order, User user){
	    // Create a new HttpClient and Post Header
		DefaultHttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(buildOrderApi(order.getShopId()));
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8);
	        nameValuePairs.add(new BasicNameValuePair("postscript", order.getMessage()));
	        nameValuePairs.add(new BasicNameValuePair("send_time", order.getSendTime()));
	        nameValuePairs.add(new BasicNameValuePair("consignee", order.getConsignee()));
	        nameValuePairs.add(new BasicNameValuePair("region_id", order.getRegionId() + ""));
	        nameValuePairs.add(new BasicNameValuePair("address", order.getAddress()));
	        nameValuePairs.add(new BasicNameValuePair("phone_mob", order.getPhone()));
	        nameValuePairs.add(new BasicNameValuePair("phone_tel", ""));
	        // TODO get shipping id from order, and selected by user
	        nameValuePairs.add(new BasicNameValuePair("shipping_id", order.getShipId() + ""));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        CookieStore cookieStore = httpclient.getCookieStore();
	        BasicClientCookie cookie = new BasicClientCookie(sessionName, user.getSession());
	        Log.e("ZZOUR", sessionName);
	        Log.e("ZZOUR", user.getSession());
		    cookie.setDomain(GlobalSettings.server);
		    cookie.setPath("/");
		    cookieStore.addCookie(cookie);
		    httpclient.setCookieStore(cookieStore);

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        if (response != null){
	        	return parseOrderResult(EntityUtils.toString(response.getEntity()));
	        } else {
	        	return null;
	        }
	    } catch (ClientProtocolException e) {
	        // Auto-generated catch block
	    	Log.e("ZZOUR", "make order failed with exception: " + e);
	    	return null;
	    } catch (IOException e) {
	        // Auto-generated catch block
	    	Log.e("ZZOUR", "make order failed with exception: " + e);
	    	return null;
	    }
	}
	
	private static ApiResult parseOrderResult(String data){
		if (data == null || data.length() == 0){
			return null;
		}
		try {
			Log.e("ZZOUR", "order response data: " + data);
			JSONObject dataObj = new JSONObject(data);
			boolean success = dataObj.getBoolean("done");
			String msg = dataObj.getString("msg");
			ApiResult result = new ApiResult();
			result.setMsg(msg);
			result.setSuccess(success);
			return result;
		} catch (JSONException ex){
			Log.e("ZZOUR", "error in parse add all into cart result:　" + ex);
			return null;
		}
	}
	
	private static ApiResult parseRemoveResult(String data){
		if (data == null || data.length() == 0){
			Log.e("ZZOUR", "remove food response is null or empty: " + data);
			if (data == null){
				Log.e("ZZOUR", "data is null");
			}
			return null;
		}
		try {
			Log.e("ZZOUR", "remove food response data: " + data);
			JSONObject dataObj = new JSONObject(data);
			boolean success = dataObj.getBoolean("done");
			String msg = dataObj.getString("msg");
			ApiResult result = new ApiResult();
			result.setMsg(msg);
			result.setSuccess(success);
			return result;
		} catch (JSONException ex){
			Log.e("ZZOUR", "error in parse remove food result:　" + ex);
			return null;
		}
	}
	
	private static String buildRemoveFoodUrl(Food food){
		return GlobalSettings.getServerAddress() + removeFoodPath + "&rec_id=" + food.getRecId();
	}
	
	public static ApiResult removeFood(Food food, User user){
		String src = buildRemoveFoodUrl(food);
		Log.e("ZZOUR", "get data from " + src);
		InputStream content = null;
		String data = null;
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
		    CookieStore cookieStore = httpclient.getCookieStore();
		    BasicClientCookie cookie = new BasicClientCookie(sessionName, user.getSession());
		    cookie.setDomain(GlobalSettings.server);
		    cookie.setPath("/");
		    cookieStore.addCookie(cookie);
		    httpclient.setCookieStore(cookieStore);
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
		return parseRemoveResult(data);
	}
	
	private static String buildUpdateFoodUrl(Food food){
		return GlobalSettings.getServerAddress() + updateFoodPath + "&spec_id=" + food.getSpecId() + "&quantity=" + food.getBuyCount();
	}
	
	public static ApiResult updateCount(Food food, User user){
		String src = buildUpdateFoodUrl(food);
		Log.e("ZZOUR", "get data from " + src);
		InputStream content = null;
		String data = null;
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
		    CookieStore cookieStore = httpclient.getCookieStore();
		    BasicClientCookie cookie = new BasicClientCookie(sessionName, user.getSession());
		    cookie.setDomain(GlobalSettings.server);
		    cookie.setPath("/");
		    cookieStore.addCookie(cookie);
		    httpclient.setCookieStore(cookieStore);
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
		return parseUpdateResult(data);
	}
	
	private static ApiResult parseUpdateResult(String data){
		if (data == null || data.length() == 0){
			Log.e("ZZOUR", "update food response is null or empty: " + data);
			if (data == null){
				Log.e("ZZOUR", "data is null");
			}
			return null;
		}
		try {
			Log.e("ZZOUR", "update food response data: " + data);
			JSONObject dataObj = new JSONObject(data);
			boolean success = dataObj.getBoolean("done");
			String msg = dataObj.getString("msg");
			ApiResult result = new ApiResult();
			result.setMsg(msg);
			result.setSuccess(success);
			return result;
		} catch (JSONException ex){
			Log.e("ZZOUR", "error in parse remove food result:　" + ex);
			return null;
		}
	}
}