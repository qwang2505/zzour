package com.zzour.android.models;

import android.graphics.Bitmap;

public class ShopSummaryContent extends BaseDataModel{
	// required
	private int id;
	private boolean isNew;
	// required
	private String logo;
	// required
	private String name;
	private String ownerName;
	private String regionName;
	private String address;
	private String telephone;
	private String notice;
	private int state;
	private int order;
	private boolean recommend;
	private boolean alive;
	private boolean onlineOrder;
	private int creditValue;
	// optional, default is empty
	private String description;
	// optional, default rate is 0
	private float grade;
	
	// bitmap for image.
	private Bitmap bitmap = null;
	
	public ShopSummaryContent(int id, boolean isNew, String logo, String name,
			String description, float grade) {
		super();
		this.id = id;
		this.isNew = isNew;
		this.logo = logo;
		this.name = name;
		this.description = description;
		this.grade = grade;
	}
	
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getNotice() {
		return notice;
	}
	public void setNotice(String notice) {
		this.notice = notice;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public boolean isRecommend() {
		return recommend;
	}
	public void setRecommend(boolean recommend) {
		this.recommend = recommend;
	}
	public boolean isAlive() {
		return alive;
	}
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	public boolean isOnlineOrder() {
		return onlineOrder;
	}
	public void setOnlineOrder(boolean onlineOrder) {
		this.onlineOrder = onlineOrder;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public float getGrade() {
		return grade;
	}
	public void setGrade(float grade) {
		this.grade = grade;
	}
	public boolean isNew() {
		return isNew;
	}
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public int getCreditValue() {
		return creditValue;
	}
	public void setCreditValue(int creditValue) {
		this.creditValue = creditValue;
	}
	@Override
	public boolean expired() {
		// TODO Auto-generated method stub
		return false;
	}
}
