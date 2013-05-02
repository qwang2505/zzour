package com.zzour.android.models;

// TODO buy information
public class Food extends BaseDataModel{
	
	private int id;
	private int shopId;
	private String category;
	private String name;
	private float price;
	private int soldCount;
	// optional
	private String image;
	private boolean checked = false;
	private int buyCount = 1;
	// set default value for test
	private float boxPrice = 0.5f;
	
	public Food(){
		
	}

	public Food(String text){
		String[] fs = text.split(",");
		if (fs.length != 4){
			return;
		}
		this.id = Integer.valueOf(fs[0]);
		this.name = fs[1];
		this.price = Float.valueOf(fs[2]);
		this.buyCount = Integer.valueOf(fs[3]);
	}
	public String toString(){
		return id + "," + name + "," + price + "," + buyCount;
	}
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	public int getShopId() {
		return shopId;
	}

	public void setShopId(int shopId) {
		this.shopId = shopId;
	}
	public float getBoxPrice() {
		return boxPrice;
	}

	public void setBoxPrice(float boxPrice) {
		this.boxPrice = boxPrice;
	}

	public int getBuyCount() {
		return buyCount;
	}

	public void setBuyCount(int buyCount) {
		this.buyCount = buyCount;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getSoldCount() {
		return soldCount;
	}

	public void setSoldCount(int soldCount) {
		this.soldCount = soldCount;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public boolean expired() {
		// TODO Auto-generated method stub
		return false;
	}

}
