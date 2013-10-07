package com.zzour.android.network.api.results;

import com.zzour.android.models.OrderDetail;

public class OrderDetailResult extends ApiResult {
	private OrderDetail order = null;
	public OrderDetail getOrder() {
		return order;
	}
	public void setOrder(OrderDetail order) {
		this.order = order;
	}
}
