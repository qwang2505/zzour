package com.zzour.android.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class Order {

	// uuid for order, returned from server
	private String id;
	// time for order
	private Date time;
	private HashMap<Integer, HashMap<Food, Integer>> foods = new HashMap<Integer, HashMap<Food, Integer>>();
	private HashMap<Integer, String> shopNames = new HashMap<Integer, String>();
	private HashMap<Integer, String> shopImages = new HashMap<Integer, String>();
	// add price here to make sure the price is right.
	private float totalBoxPrice;
	private float totalPrice;
	private Address address;
	private String sendTime;
	private String message;
	private String resultMsg;
	
	public String getResultMsg() {
		return resultMsg;
	}
	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getFoodCount(){
		return foods.size();
	}
	public void addFood(int shopId, String shopName, String image, Food food){
		HashMap<Food, Integer> fs;
		if (!foods.containsKey(shopId)){
			fs = new HashMap<Food, Integer>();
			foods.put(shopId, fs);
		} else {
			fs = foods.get(shopId);
		}
		fs.put(food, food.getBuyCount());
		if (shopName != null && !shopNames.containsKey(shopId)){
			shopNames.put(shopId, shopName);
		}
		if (image != null && !shopImages.containsKey(shopId)){
			shopImages.put(shopId, image);
		}
	}
	public float getTotalBoxPrice() {
		return totalBoxPrice;
	}
	public void setTotalBoxPrice(float totalBoxPrice) {
		this.totalBoxPrice = totalBoxPrice;
	}
	public float getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Iterator<Integer> getShops(){
		return foods.keySet().iterator();
	}
	public Iterator<Food> getFoods(int shopId){
		return foods.get(shopId).keySet().iterator();
	}
	public String getShopName(int shopId){
		return shopNames.get(shopId);
	}
	public String getShopImage(int shopId){
		return shopImages.get(shopId);
	}
}
