package com.zzour.android.network.api;

import java.util.UUID;

import com.zzour.android.models.Order;
import com.zzour.android.models.OrderResult;

public class OrderApi {

	public static OrderResult order(Order order){
		// TODO send request to make a order.
		// TODO here must return a non-null value
		OrderResult result = new OrderResult();
		String id = UUID.randomUUID().toString(); 
		result.setId(id);
		result.setMsg("");
		// save order, success or not
		order.setId(id);
		order.setResultMsg("");
		// no matter success or fail, we save it.
		saveOrder(order);
		return result;
	}
	
	private static void saveOrder(Order order){
		
	}
}
