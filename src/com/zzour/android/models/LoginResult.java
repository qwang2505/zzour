package com.zzour.android.models;

import java.util.HashMap;

public class LoginResult {
	private boolean success;
	private String msg;
	private HashMap<Integer, String> msgMap = new HashMap<Integer, String>();
	
	
	public LoginResult() {
		super();
		this.msgMap.put(1, "Successful");
		this.msgMap.put(-1, "Username or password is wrong");
		this.msgMap.put(-2, "Unknow error");
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
		if (!this.msgMap.containsKey(m)){
			this.msg = this.msgMap.get(-2);
		}else {
			this.msg = this.msgMap.get(m);
		}
	}
}
