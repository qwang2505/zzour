package com.zzour.android.network.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.zzour.android.models.Address;
import com.zzour.android.models.Food;
import com.zzour.android.models.OrderDetail;
import com.zzour.android.models.OrderLog;
import com.zzour.android.models.OrderSummary;
import com.zzour.android.models.User;
import com.zzour.android.network.api.results.ApiResult;
import com.zzour.android.network.api.results.OrderDetailResult;
import com.zzour.android.network.api.results.OrderListResult;
import com.zzour.android.settings.GlobalSettings;

public class MyOrderApi {
	
	private static String myOrdersPath = "/index.php?app=buyer_order&act=index&method=ajax&ajax=1";
	private static String orderDetailPath = "/index.php?app=buyer_order&act=view&method=ajax&ajax=1";
	private static String finishOrderPath = "/index.php?app=buyer_order&act=confirm_order&ajax=1";
	
	private static final String sessionName = "ECM_ID";
	
	private static String buildMyOrdersUrl(String timeFrom, String timeTo, String type, int page, int count){
		String baseUrl = GlobalSettings.getServerAddress() + myOrdersPath;
		if (timeFrom != null){
			baseUrl += "&add_time_from=" + timeFrom;
		}
		if (timeTo != null){
			baseUrl += "&add_time_to=" + timeTo;
		}
		if (type != null){
			baseUrl += "&type=" + type;
		} else{
			baseUrl += "&type=all";
		}
		if (page >= 1){
			baseUrl += "&page=" + page;
		} else{
			baseUrl += "&page=1";
		}
		if (count >= 1){
			baseUrl += "&page_per=" + count;
		} else {
			baseUrl += "&page_per=12";
		}
		return baseUrl;
	}
	
	public static OrderListResult getOrdersByType(String type, int page, int count, User user){
		return getOrders(user.getSession(), null, null, type, page, count);
	}
	
	private static OrderListResult getOrders(String session, String timeFrom, String timeTo, String type, int page, int count){
		String src = buildMyOrdersUrl(timeFrom, timeTo, type, page, count);
		Log.e("ZZOUR", "get data from " + src);
		InputStream content = null;
		String data = null;
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
		return parseMyOrderListResult(data);
	}
	
	private static OrderListResult parseMyOrderListResult(String data){
		ArrayList<OrderSummary> orders = new ArrayList<OrderSummary>();
		OrderListResult result = new OrderListResult();
		try {
			Log.e("ZZOUR", "my order list response data: " + data);
			JSONObject dataObj = new JSONObject(data);
			boolean success = dataObj.getBoolean("done");
			result.setSuccess(success);
			if (!success){
				String msg = null;
				try{
					msg = dataObj.getString("msg");
				} catch (Exception e){
					e.printStackTrace();
				}
				if (msg != null && msg.startsWith("您需要先登录")){
					result.setNeedLogin(true);
				}
				result.setOrders(orders);
				return result;
			}
			JSONObject retvalObj = dataObj.getJSONObject("retval");
			JSONArray orderIds = retvalObj.names();
			for (int i=0; i < orderIds.length(); i++){
				JSONObject orderObj = (JSONObject)retvalObj.get(orderIds.getString(i));
				OrderSummary order = new OrderSummary();
				order.setId(orderObj.getString("order_id"));
				order.setPrice((float)orderObj.getDouble("order_amount"));
				order.setShopName(orderObj.getString("seller_name"));
				order.setStatus(orderObj.getInt("status"));
				order.setTime(orderObj.getString("add_time"));
				orders.add(order);
			}
			result.setOrders(orders);
			return result;
		} catch (JSONException ex){
			Log.e("ZZOUR", "error in parse my order list result:　" + ex);
			ex.printStackTrace();
			result.setSuccess(false);
			result.setOrders(orders);
			result.setMsg("解析返回结果错误");
			return result;
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
	
	private static String buildOrderDetailUrl(int orderId){
		return GlobalSettings.getServerAddress() + orderDetailPath + "&order_id=" + orderId;
	}
	
	public static OrderDetailResult getOrderDetail(int orderId, User user){
		String src = buildOrderDetailUrl(orderId);
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
			Log.e("ZZOUR", "get order detail from server faield");
			return null;
		}
		return parseOrderDetailResult(data);
	}
	
	private static OrderDetailResult parseOrderDetailResult(String data){
		OrderDetailResult result = new OrderDetailResult();
		try {
			Log.e("ZZOUR", "order detail response data: " + data);
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
				if (msg != null && msg.startsWith("您需要先扥股")){
					result.setMsg(msg);
					result.setNeedLogin(true);
				}
				return result;
			}
			OrderDetail order = new OrderDetail();
			JSONObject retvalObj = dataObj.getJSONObject("retval");
			JSONObject orderObj = retvalObj.getJSONObject("order");
			String time = orderObj.getString("add_time");
			int status = orderObj.getInt("status");
			order.setTime(time);
			order.setStatus(status);
			order.setRemark(orderObj.getString("postscript"));
			order.setPrice((float)orderObj.getDouble("goods_amount"));
			JSONObject orderDataObj = retvalObj.getJSONObject("0");
			JSONObject orderExtObj = orderDataObj.getJSONObject("order_extm");
			Address addr = new Address();
			addr.setAddr(orderExtObj.getString("region_name") + " " + orderExtObj.getString("address"));
			addr.setId(-1);
			addr.setName(orderExtObj.getString("consignee"));
			addr.setPhone(orderExtObj.getString("phone_mob"));
			addr.setRegionId(orderExtObj.getInt("region_id"));
			addr.setRegionName(orderExtObj.getString("region_name"));
			order.setAddr(addr);
			JSONObject foodsObjs = orderDataObj.getJSONObject("goods_list");
			JSONArray foodsIds = foodsObjs.names();
			ArrayList<Food> foods = new ArrayList<Food>();
			for (int i=0; i < foodsIds.length(); i++){
				Food food = new Food();
				JSONObject foodObj = (JSONObject)foodsObjs.get(foodsIds.getString(i));
				food.setBuyCount(foodObj.getInt("quantity"));
				food.setId(foodObj.getInt("goods_id"));
				food.setImage(foodObj.getString("goods_image"));
				food.setName(foodObj.getString("goods_name"));
				food.setPrice((float)foodObj.getDouble("price"));
				food.setSpecId(foodObj.getInt("spec_id"));
				foods.add(food);
			}
			ArrayList<OrderLog> logs = new ArrayList<OrderLog>();
			JSONObject logsObj = orderDataObj.getJSONObject("order_logs");
			JSONArray logIds = logsObj.names();
			for (int i=0; i < logIds.length(); i++){
				OrderLog log = new OrderLog();
				JSONObject logObj = (JSONObject)logsObj.get(logIds.getString(i));
				log.setChangedStatus(logObj.getString("changed_status"));
				log.setId(logObj.getInt("log_id"));
				log.setOperator(logObj.getString("operator"));
				log.setOperatorId(logObj.getInt("operator_id"));
				log.setOrderId(logObj.getInt("order_id"));
				log.setOrderStatus(logObj.getString("order_status"));
				log.setRemark(logObj.getString("remark"));
				log.setTime(logObj.getString("log_time"));
				logs.add(log);
			}
			order.setFoods(foods);
			order.setLogs(logs);
			result.setOrder(order);
			return result;
		} catch (JSONException ex){
			Log.e("ZZOUR", "error in parse my order list result:　" + ex);
			ex.printStackTrace();
			result.setSuccess(false);
			result.setMsg("解析返回结果错误");
			return result;
		}
	}
	
	private static String buildFinishOrderUrl(int orderId){
		return GlobalSettings.getServerAddress() + finishOrderPath + "&order_id=" + orderId;
	}
	
	public static ApiResult finishOrder(int orderId, User user){
	    // Create a new HttpClient and Post Header
		DefaultHttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(buildFinishOrderUrl(orderId));
	    try {
	        CookieStore cookieStore = httpclient.getCookieStore();
	        BasicClientCookie cookie = new BasicClientCookie(sessionName, user.getSession());
		    cookie.setDomain(GlobalSettings.server);
		    cookie.setPath("/");
		    cookieStore.addCookie(cookie);
		    httpclient.setCookieStore(cookieStore);
	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        String data = null;
	        if (response != null){
	        	data = EntityUtils.toString(response.getEntity());
	        } else {
	        	return null;
	        }
	        return parseFinishOrderResult(data);
	    } catch (ClientProtocolException e) {
	        // Auto-generated catch block
	    	e.printStackTrace();
	    	return null;
	    } catch (IOException e) {
	        // Auto-generated catch block
	    	e.printStackTrace();
	    	return null;
	    }
	}
	
	private static ApiResult parseFinishOrderResult(String data){
		try {
			Log.e("ZZOUR", "finish order response data: " + data);
			JSONObject dataObj = new JSONObject(data);
			boolean success = dataObj.getBoolean("done");
			ApiResult result = new ApiResult();
			result.setSuccess(success);
			result.setMsg(dataObj.getString("msg"));
			return result;
		} catch (JSONException ex){
			Log.e("ZZOUR", "error in parse finish order result:　" + ex);
			ex.printStackTrace();
			return null;
		}
	}
}
