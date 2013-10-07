package com.zzour.android.network.api.results;

import java.util.ArrayList;

import com.zzour.android.models.OrderSummary;

public class OrderListResult extends ApiResult {
	private ArrayList<OrderSummary> orders;

	public ArrayList<OrderSummary> getOrders() {
		return orders;
	}

	public void setOrders(ArrayList<OrderSummary> orders) {
		this.orders = orders;
	}
}
