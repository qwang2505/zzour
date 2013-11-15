package com.zzour.android.network.api.results;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.zzour.android.models.OrderSummary;

public class OrderListResult extends ApiResult {
	private ArrayList<OrderSummary> orders;
	
	private Comparator<OrderSummary> comparator = new Comparator<OrderSummary>(){
		@Override
		public int compare(OrderSummary o1, OrderSummary o2) {
			return -(o1.getTime().compareTo(o2.getTime()));
		}
	};

	public ArrayList<OrderSummary> getOrders() {
		return orders;
	}

	public void setOrders(ArrayList<OrderSummary> orders) {
		// order by time
		Collections.sort(orders, comparator);
		this.orders = orders;
	}
}
