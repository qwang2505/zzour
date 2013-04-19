package com.zzour.android.models;

import android.graphics.Bitmap;

public class ShopSummaryContent extends BaseDataModel{
	// required
	private int id;
	private boolean isNew;
	// required
	private String image;
	// required
	private String name;
	// optional, default is empty
	private String description;
	// optional, default rate is 0
	private int rate;
	
	// bitmap for image.
	private Bitmap bitmap = null;
	
	public ShopSummaryContent(int id, boolean isNew, String image, String name,
			String description, int rate) {
		super();
		this.id = id;
		this.isNew = isNew;
		this.image = image;
		this.name = name;
		this.description = description;
		this.rate = rate;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
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
	public int getRate() {
		return rate;
	}
	public void setRate(int rate) {
		this.rate = rate;
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
	@Override
	public boolean expired() {
		// TODO Auto-generated method stub
		return false;
	}
}
