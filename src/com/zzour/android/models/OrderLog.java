package com.zzour.android.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderLog {
	private int id;
	private int orderId;
	private int operatorId;
	private String operator;
	private String orderStatus;
	private String changedStatus;
	private String remark;
	private String time;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public int getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public String getChangedStatus() {
		return changedStatus;
	}
	public void setChangedStatus(String changedStatus) {
		this.changedStatus = changedStatus;
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
		Date date = new Date(Long.valueOf(time) * 1000);
		SimpleDateFormat sdf= new SimpleDateFormat("HH:mm:ss");
		this.time = sdf.format(date);
	}
	
}
