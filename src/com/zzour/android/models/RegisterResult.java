package com.zzour.android.models;

import java.util.HashMap;

public class RegisterResult {
	private boolean success;
	private String msg;
	private HashMap<Integer, String> msgMap = new HashMap<Integer, String>();
	
	
	public RegisterResult() {
		super();
		this.msgMap.put(1, "Successful");
		this.msgMap.put(-1, "用户名已存在");
		this.msgMap.put(-2, "系统错误，请重新尝试");
		this.success = false;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public void setMsg(int m){
		if (m != 1){
			this.success = false;
		}
		if (!this.msgMap.containsKey(m)){
			this.msg = this.msgMap.get(-2);
		}else {
			this.msg = this.msgMap.get(m);
		}
	}
}