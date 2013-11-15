package com.zzour.android.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

public class OrderDetail {
	private String time;
	private int status;
	private ArrayList<OrderLog> logs;
	private ArrayList<Food> foods;
	private Address addr;
	private String remark;
	private float price;
	private int buyerId;
	private boolean cd;
	private String cdMsg;
	private final int EIGHT_HOUR = 8 * 3600 * 1000;
	private Comparator<OrderLog> comparator = new Comparator<OrderLog>(){
		@Override
		public int compare(OrderLog o1, OrderLog o2) {
			return o1.getRealTime().compareTo(o2.getRealTime());
		}
	};
	
	public int getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(int buyerId) {
		this.buyerId = buyerId;
	}
	public boolean isCd() {
		return cd;
	}
	public void setCd(boolean cd) {
		this.cd = cd;
	}
	public String getCdMsg() {
		return cdMsg;
	}
	public void setCdMsg(String cdMsg) {
		this.cdMsg = cdMsg;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		Date date = new Date(Long.valueOf(time) * 1000 + EIGHT_HOUR);
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.time = sdf.format(date);
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public ArrayList<OrderLog> getLogs() {
		return logs;
	}
	public void setLogs(ArrayList<OrderLog> logs) {
		// order by time and add extra message
		Collections.sort(logs, comparator);
		Iterator<OrderLog> it = logs.iterator();
		while (it.hasNext()){
			OrderLog log = it.next();
			String remark = log.getRemark();
			if (log.getOperatorId() == this.getBuyerId()){
				log.setRemark("Äú£º" + remark);
			} else {
				log.setRemark("ÉÌ¼Ò»Ø¸´£º" + remark);
			}
		}
		this.logs = logs;
	}
	public ArrayList<Food> getFoods() {
		return foods;
	}
	public void setFoods(ArrayList<Food> foods) {
		this.foods = foods;
	}
	public Address getAddr() {
		return addr;
	}
	public void setAddr(Address addr) {
		this.addr = addr;
	}
}
