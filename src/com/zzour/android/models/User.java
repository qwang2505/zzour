package com.zzour.android.models;

public class User extends BaseDataModel{
	
	// when use third party account system, user name should be qq number, renren account, etc.
	private String userName;
	// when use third party account system, pwd should be token, etc.
	private String pwd;
	// auth type, normal, qq account, renren account, etc.
	private AuthType type;
	
	public User(String name, String pwd, AuthType type){
		this.userName = name;
		this.pwd = pwd;
		this.type = type;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public AuthType getType() {
		return type;
	}

	public void setType(AuthType type) {
		this.type = type;
	}

	public enum AuthType{
		NORMAL, QQ, RENREN
	}

	@Override
	public boolean expired() {
		return false;
	}

}
