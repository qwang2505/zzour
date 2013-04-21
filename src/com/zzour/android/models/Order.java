package com.zzour.android.models;

import java.util.HashMap;

public class Order {

	// TODO uuid for order
	private String id;
	// TODO time for order
	private HashMap<Integer, Integer> foods = new HashMap<Integer,Integer>();
	// add price here to make sure the price is right.
	private float totalBoxPrice;
	private float totalPrice;
	private Address address;
	private String sendTime;
	private String message;
	
	public int getFoodCount(){
		return foods.size();
	}
	public void addFood(int id, int count){
		foods.put(id, count);
	}
	public HashMap<Integer, Integer> getFoods() {
		return foods;
	}
	public void setFoods(HashMap<Integer, Integer> foods) {
		this.foods = foods;
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
}
