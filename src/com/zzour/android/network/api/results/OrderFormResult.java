package com.zzour.android.network.api.results;

import java.util.ArrayList;

import com.zzour.android.models.Address;
import com.zzour.android.models.Food;
import com.zzour.android.models.ShipMethod;

public class OrderFormResult extends ApiResult {
	private ArrayList<Food> foods = null;
	private ArrayList<Address> addrs = null;
	private ArrayList<ShipMethod> shipMethods = null;
	public ArrayList<ShipMethod> getShipMethods() {
		return shipMethods;
	}
	public void setShipMethods(ArrayList<ShipMethod> shipMethods) {
		this.shipMethods = shipMethods;
	}
	public ArrayList<Food> getFoods() {
		return foods;
	}
	public void setFoods(ArrayList<Food> foods) {
		this.foods = foods;
	}
	public ArrayList<Address> getAddrs() {
		return addrs;
	}
	public void setAddrs(ArrayList<Address> addrs) {
		this.addrs = addrs;
	}
}
