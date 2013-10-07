package com.zzour.android.models;

import com.zzour.android.utils.MD5Hash;

public class AccountInfo {
	private String name;
	private String password;
	private String accessToken;
	private User.AuthType type;
	private long expire;
	// for renren client
	private String uid;
	// for qq client
	private String openId;
	private String nickName;
	private String profileUrl;
	
	private boolean success = false;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getProfileUrl() {
		return profileUrl;
	}
	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		// construct user name and password
		this.openId = openId;
		this.name = "qq" + MD5Hash.md5For8Bit(openId);
		this.password = "pass" + MD5Hash.md5For8Bit(openId);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public User.AuthType getType() {
		return type;
	}
	public void setType(User.AuthType type) {
		this.type = type;
	}
	public long getExpire() {
		return expire;
	}
	public void setExpire(long expire) {
		this.expire = expire;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
		// construct name and password by user id in renren account
		this.name = "renren" + MD5Hash.md5For8Bit(uid);
		this.password = "pass" + MD5Hash.md5For8Bit(uid);
	}
}
