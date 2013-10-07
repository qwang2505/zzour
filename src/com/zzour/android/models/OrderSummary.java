package com.zzour.android.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderSummary {
	private String id;
	private String shopName;
	private String time;
	private float price;
	private int status;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getShopName() {
		return shopName;
	}
	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		Date date = new Date(Long.valueOf(time) * 1000);
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.time = sdf.format(date);
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
}
