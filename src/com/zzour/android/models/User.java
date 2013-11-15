package com.zzour.android.models;

public class User extends BaseDataModel{
	
	// when use third party account system, user name should be qq number, renren account, etc.
	private String userName;
	private String nickName;
	// when use third party account system, pwd should be token, etc.
	private String pwd;
	private String session;
	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	// auth type, normal, qq account, renren account, etc.
	private AuthType type;
	
	public User(String name, String pwd, String session, AuthType type){
		this.userName = name;
		this.pwd = pwd;
		this.type = type;
		this.session = session;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNickName() {
		if (this.nickName == null || this.nickName.length() == 0){
			return this.userName;
		}
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
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
		NORMAL, TENCENT, RENREN
	}

	@Override
	public boolean expired() {
		return false;
	}

}
