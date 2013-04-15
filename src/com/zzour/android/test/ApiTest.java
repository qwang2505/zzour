package com.zzour.android.test;

import com.zzour.android.models.ShopDetailContent;
import com.zzour.android.models.ShopList;
import com.zzour.android.network.api.DataApi;

import android.test.AndroidTestCase;
import android.util.Log;

public class ApiTest extends AndroidTestCase{
	
	
	public void testFakeApi() throws Throwable {
		ShopList shopList = DataApi.getShopList();
		if (shopList != null){
			Log.d("ZZOUR", shopList.getmSearchKeyword());
		}
		ShopDetailContent shop = DataApi.getShopDetailById(0);
		if (shop != null){
			Log.d("ZZOUR", shop.getName());
		}
		Log.d("ZZOUR", "api test done");
	}

}
