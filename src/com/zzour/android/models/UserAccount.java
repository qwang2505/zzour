package com.zzour.android.models;

public class UserAccount extends BaseDataModel{
	
	// when use third party account system, user name should be qq number, renren account, etc.
	private String userName;
	private String email;
	private int integral;
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getIntegral() {
		return integral;
	}

	public void setIntegral(int integral) {
		this.integral = integral;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public boolean expired() {
		return false;
	}
}
