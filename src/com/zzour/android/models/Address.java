package com.zzour.android.models;

import android.util.Log;

public class Address {
	private int id;
	private int userId;
	private String name;
	private String addr;
	private String phone;
	private int regionId;
	private String regionName;
	
	public Address(){
		this.id = -1;
		this.userId = -1;
	}
	
	public Address(String text){
		String[] ts = text.split(",,");
		if (ts.length != 3){
			Log.e("ZZOUR", "not right address: " + text);
			return;
		}
		this.name = ts[0];
		this.phone = ts[1];
		this.addr = ts[2];
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getRegionId() {
		return regionId;
	}

	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String toString(){
		// use ,, as separator to avoid conflict
		return name + ",," + phone + ",," + addr;
	}
}
