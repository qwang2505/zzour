package com.zzour.android.models;

import java.util.ArrayList;
import java.util.HashMap;

public class ShopDetailContent extends BaseDataModel{
	
	private int id;
	private String banner;
	private String name;
	private float rate;
	private String address;
	private String desc;
	private ArrayList<Food> recommends = new ArrayList<Food>();
	private HashMap<String, ArrayList<Food> > foods = new HashMap<String, ArrayList<Food>>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public ArrayList<Food> getRecommends() {
		return recommends;
	}

	public void setRecommends(ArrayList<Food> recommends) {
		this.recommends = recommends;
	}

	public HashMap<String, ArrayList<Food>> getFoods() {
		return foods;
	}

	public void setFoods(HashMap<String, ArrayList<Food>> foods) {
		this.foods = foods;
	}

	@Override
	public boolean expired() {
		// TODO Auto-generated method stub
		return false;
	}
}
