package com.zzour.android.models;

import java.util.ArrayList;
import java.util.HashMap;

public class ShopDetailContent extends BaseDataModel{
	
	private int id;
	private String banner;
	private String name;
	private float grade;
	private String address;
	private String desc;
	
	private boolean onlineOrder;
	private String sendTime;
	private int goodsCount;
	private float praiseRate;
	private boolean live;
	private String shopHours;
	private String telephone;
	private String notice;
	
	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	public boolean isOnlineOrder() {
		return onlineOrder;
	}

	public void setOnlineOrder(boolean onlineOrder) {
		this.onlineOrder = onlineOrder;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
		if (this.sendTime == null || this.sendTime == "null"){
			this.sendTime = "40∑÷÷”";
		}
	}

	public int getGoodsCount() {
		return goodsCount;
	}

	public void setGoodsCount(int goodsCount) {
		this.goodsCount = goodsCount;
	}

	public float getPraiseRate() {
		return praiseRate;
	}

	public void setPraiseRate(float praiseRate) {
		this.praiseRate = praiseRate;
	}

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	public String getShopHours() {
		return shopHours;
	}

	public void setShopHours(String shopHours) {
		this.shopHours = shopHours;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}


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

	public float getGrade() {
		return grade;
	}

	public void setGrade(float rate) {
		this.grade = rate;
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
