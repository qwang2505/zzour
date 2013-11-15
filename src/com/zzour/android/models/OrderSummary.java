package com.zzour.android.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class OrderSummary {
	private String id;
	private String shopName;
	private Date time;
	private float price;
	private int status;
	private final int EIGHT_HOUR = 8 * 3600 * 1000;
	
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
	public Date getTime() {
		return time;
	}
	public String getStringTime(){
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		return sdf.format(this.time);
	}
	public void setTime(String time) {
		Date date = new Date(Long.valueOf(time) * 1000 + EIGHT_HOUR);
		this.time = date;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
}
