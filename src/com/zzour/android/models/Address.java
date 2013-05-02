package com.zzour.android.models;

import android.util.Log;

public class Address {
	private String name;
	private String addr;
	private String phone;
	
	public Address(String name, String phone, String addr){
		this.name = name;
		this.phone = phone;
		this.addr = addr;
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
