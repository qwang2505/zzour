package com.zzour.android.network.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.zzour.android.models.Address;
import com.zzour.android.models.Food;
import com.zzour.android.models.ShipMethod;
import com.zzour.android.models.SimpleOrder;
import com.zzour.android.models.User;
import com.zzour.android.network.api.results.AddAllResult;
import com.zzour.android.network.api.results.ApiResult;
import com.zzour.android.network.api.results.OrderFormResult;
import com.zzour.android.network.api.results.OrderResult;
import com.zzour.android.settings.GlobalSettings;

public class OrderApi {
	
	private static String addAllPath = "/index.php?app=cart&act=addAlls&method=ajax&ajax=1";
	private static String addPath = "/index.php?app=cart&act=add&method=ajax&ajax=1";
	private static String orderFormPath = "/index.php?app=order&goods=cart&method=ajax&ajax=1";
	private static String clearCartPath = "/index.php?app=cart&act=clearCart&ajax=1";
	private static String orderPath = "/index.php?app=order&goods=cart&method=ajax&ajax=1";
	private static String printOrderPath = "/index.php?app=sendgprstask";
	private static String removeFoodPath = "/index.php?app=cart&act=drop&method=ajax&ajax=1";
	private static String updateFoodPath = "/index.php?app=cart&act=update&ajax=1";
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
			JSONObject dataObj = new JSONObject(data);
			boolean success = dataObj.getBoolean("done");
			result.setSuccess(success);
			if (!success){
				String msg = null;
				try {
					msg = dataObj.getString("msg");
				} catch (Exception e){
					e.printStackTrace();
				}
				result.setMsg(msg);
				if (msg != null && msg.startsWith("您需要先登录")){
					result.setNeedLogin(true);
				}
				return result;
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
				e.printStackTrace();
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
			result.setSuccess(false);
			result.setMsg("解析返回结果错误");
			return result;
		}
	}
	
	public static AddAllResult addAll(ArrayList<Food> foods, User user){
		String data = postAddAll(foods, user.getSession());
		return parseResult(data);
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
	
	private static AddAllResult parseResult(String data){
		AddAllResult result = new AddAllResult();
		try {
			Log.e("ZZOUR", "add response data: " + data);
			JSONObject dataObj = new JSONObject(data);
			boolean success = dataObj.getBoolean("done");
			result.setSuccess(success);
			if (!success){
				String msg = null;
				try {
					msg = dataObj.getString("msg");
				} catch (Exception e){
					e.printStackTrace();
				}
				if (msg != null && msg.startsWith("您需要先登录")){
					result.setNeedLogin(true);
				}
				result.setMsg(msg);
			}
			return result;
		} catch (JSONException ex){
			Log.e("ZZOUR", "error in parse add all into cart result:　" + ex);
			ex.printStackTrace();
			result.setSuccess(false);
			result.setMsg("解析返回结果错误");
			return result;
		}
	}
	
	private static String postAddAll(ArrayList<Food> foods, String session){
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
	    	e.printStackTrace();
	    	Log.e("ZZOUR", "Add to cart failed with exception: " + e);
	    	return null;
	    } catch (IOException e) {
	    	e.printStackTrace();
	    	Log.e("ZZOUR", "Add to cart failed with exception: " + e);
	    	return null;
	    }
	}
	
	private static String buildClearCartUrl(){
		return GlobalSettings.getServerAddress() + clearCartPath;
	}
	
	private static ApiResult parseClearCartResult(String data){
		ApiResult result = new ApiResult();
		if (data == null || data.length() == 0){
			result.setSuccess(false);
			result.setMsg("返回结果为空，请检查网络设置");
			return result;
		}
		try {
			Log.e("ZZOUR", "add response data: " + data);
			JSONObject dataObj = new JSONObject(data);
			boolean success = dataObj.getBoolean("done");
			String msg = dataObj.getString("msg");
			result.setSuccess(success);
			result.setMsg(msg);
			return result;
		} catch (JSONException ex){
			Log.e("ZZOUR", "error in parse add all into cart result:　" + ex);
			ex.printStackTrace();
			result.setSuccess(false);
			result.setMsg("解析返回结果错误");
			return result;
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
		    e.printStackTrace();
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
	
	private static String buildPrintOrderUrl(int orderId){
		return GlobalSettings.getServerAddress() + printOrderPath + "&order_id=" + orderId;
	}
	
	public static void printOrder(int orderId){
		String src = buildPrintOrderUrl(orderId);
		Log.e("ZZOUR", "print order: " + src);
		try {
	        URL url = new URL(src);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream inStream = connection.getInputStream();
	        byte[] buf = readInputStream(inStream);
	        String ret = (new String(buf));
	        Log.e("ZZOUR", "print order result: " + ret);
	    } catch (Exception e) {
	        e.printStackTrace();
	        Log.e("ZZOUR", "print order error: " + e.toString());
	        return;
	    }
	}

	public static OrderResult order(SimpleOrder order, User user){
	    // Create a new HttpClient and Post Header
		DefaultHttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(buildOrderApi(order.getShopId()));
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(10);
	        nameValuePairs.add(new BasicNameValuePair("postscript", order.getMessage()));
	        nameValuePairs.add(new BasicNameValuePair("send_time", order.getSendTime()));
	        nameValuePairs.add(new BasicNameValuePair("consignee", order.getConsignee()));
	        nameValuePairs.add(new BasicNameValuePair("region_id", order.getRegionId() + ""));
	        nameValuePairs.add(new BasicNameValuePair("region_name", order.getRegionName()));
	        nameValuePairs.add(new BasicNameValuePair("address", order.getAddress()));
	        nameValuePairs.add(new BasicNameValuePair("phone_mob", order.getPhone()));
	        nameValuePairs.add(new BasicNameValuePair("phone_tel", ""));
	        // get shipping id from order, and selected by user
	        nameValuePairs.add(new BasicNameValuePair("shipping_id", order.getShipId() + ""));
	        int saveAddr = 0;
	        if (order.isNewAddr()){
	        	saveAddr = 1;
	        }
	        nameValuePairs.add(new BasicNameValuePair("save_address", saveAddr + ""));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
	        CookieStore cookieStore = httpclient.getCookieStore();
	        BasicClientCookie cookie = new BasicClientCookie(sessionName, user.getSession());
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
	
	private static OrderResult parseOrderResult(String data){
		OrderResult result = new OrderResult();
		if (data == null || data.length() == 0){
			result.setSuccess(false);
			result.setMsg("返回结果为空，请检查网络设置");
			return result;
		}
		try {
			Log.e("ZZOUR", "order response data: " + data);
			JSONObject dataObj = new JSONObject(data);
			boolean success = dataObj.getBoolean("done");
			String msg = dataObj.getString("msg");
			if (msg != null && msg.startsWith("您需要先登录")){
				result.setNeedLogin(true);
			}
			result.setMsg(msg);
			int orderId = dataObj.getInt("retval");
			result.setOrderId(orderId);
			result.setSuccess(success);
			return result;
		} catch (JSONException ex){
			Log.e("ZZOUR", "error in parse add all into cart result:　" + ex);
			ex.printStackTrace();
			result.setSuccess(false);
			result.setMsg("解析返回结果错误");
			return result;
		}
	}
	
	private static ApiResult parseRemoveResult(String data){
		ApiResult result = new ApiResult();
		if (data == null || data.length() == 0){
			Log.e("ZZOUR", "remove food response is null or empty: " + data);
			result.setSuccess(false);
			result.setMsg("返回结果为空，请检查网络设置");
			return result;
		}
		try {
			Log.e("ZZOUR", "remove food response data: " + data);
			JSONObject dataObj = new JSONObject(data);
			boolean success = dataObj.getBoolean("done");
			String msg = dataObj.getString("msg");
			if (msg != null && msg.startsWith("您需要先登录")){
				result.setNeedLogin(true);
			}
			result.setMsg(msg);
			result.setSuccess(success);
			return result;
		} catch (JSONException ex){
			ex.printStackTrace();
			Log.e("ZZOUR", "error in parse remove food result:　" + ex);
			result.setSuccess(false);
			result.setMsg("解析返回结果错误");
			return result;
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
		ApiResult result = new ApiResult();
		if (data == null || data.length() == 0){
			Log.e("ZZOUR", "update food response is null or empty: " + data);
			result.setSuccess(false);
			result.setMsg("返回结果为空，请检查网络设置");
			return result;
		}
		try {
			Log.e("ZZOUR", "update food response data: " + data);
			JSONObject dataObj = new JSONObject(data);
			boolean success = dataObj.getBoolean("done");
			String msg = dataObj.getString("msg");
			if (msg != null && msg.startsWith("您需要先登录")){
				result.setNeedLogin(true);
			}
			result.setMsg(msg);
			result.setSuccess(success);
			return result;
		} catch (JSONException ex){
			ex.printStackTrace();
			Log.e("ZZOUR", "error in parse remove food result:　" + ex);
			result.setSuccess(false);
			result.setMsg("解析返回结果错误");
			return result;
		}
	}
}