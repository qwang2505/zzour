package com.zzour.android.models;

import java.util.ArrayList;

public class ShopList extends BaseDataModel{
	public ShopList(String mSearchKeyword, ArrayList<String> mBanners,
			ArrayList<ShopSummaryContent> mShops) {
		super();
		this.mSearchKeyword = mSearchKeyword;
		this.mBanners = mBanners;
		this.mShops = mShops;
	}
	public ShopList(String mSearchKeyword, String[] banners,
			ArrayList<ShopSummaryContent> mShops) {
		super();
		this.mSearchKeyword = mSearchKeyword;
		this.mShops = mShops;
		if (this.mBanners == null){
			this.mBanners = new ArrayList<String>();
		}
		for (int i=0; i < banners.length; i++){
			this.mBanners.add(banners[i]);
		}
	}
	private String mSearchKeyword;
	public String getmSearchKeyword() {
		return mSearchKeyword;
	}
	public void setmSearchKeyword(String mSearchKeyword) {
		this.mSearchKeyword = mSearchKeyword;
	}
	public ArrayList<String> getmBanners() {
		return mBanners;
	}
	public void setmBanners(ArrayList<String> mBanners) {
		this.mBanners = mBanners;
	}
	public ArrayList<ShopSummaryContent> getmShops() {
		return mShops;
	}
	public void setmShops(ArrayList<ShopSummaryContent> mShops) {
		this.mShops = mShops;
	}
	
	// shops count
	public int size(){
		return mShops.size();
	}
	// get ShopSummaryContent at index
	public ShopSummaryContent get(int index){
		return mShops.get(index);
	}
	private ArrayList<String> mBanners;
	private ArrayList<ShopSummaryContent> mShops;
	@Override
	public boolean expired() {
		// TODO Auto-generated method stub
		return false;
	}
}
