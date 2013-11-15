package com.zzour.android.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ShopList extends BaseDataModel{
	
	private ArrayList<String> mBanners;
	private ArrayList<ShopSummaryContent> mShops;
	private String mSearchKeyword;
	private Comparator<ShopSummaryContent> comparator = new Comparator<ShopSummaryContent>(){
		@Override
		public int compare(ShopSummaryContent shop1, ShopSummaryContent shop2) {
			if (shop1.isAlive() != shop2.isAlive()){
				if (shop1.isAlive()){
					return -1;
				} else {
					return 1;
				}
			} else {
				return shop1.getOrder() - shop2.getOrder();
			}
		}
	};
	
	public ShopList(ArrayList<ShopSummaryContent> mShops){
		// sort shops
		Collections.sort(mShops, comparator);
		this.mShops = mShops;
		this.mSearchKeyword = "default search keyword";
		this.mBanners = new ArrayList<String>();
		this.mBanners.add("http://www.zzour.com/data/files/mall/template/201304231757237155.jpg");
	}
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
	@Override
	public boolean expired() {
		// TODO Auto-generated method stub
		return false;
	}
}
