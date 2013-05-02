package com.zzour.android.network.api;

import java.util.Date;
import java.util.UUID;

import android.content.Context;

import com.zzour.android.models.Order;
import com.zzour.android.models.OrderResult;
import com.zzour.android.models.dao.OrderDAO;

public class OrderApi {

	public static OrderResult order(Order order, Context context){
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
		order.setTime(new Date());
		OrderDAO dao = new OrderDAO(context);
		dao.insert(order);
		return result;
	}
}
