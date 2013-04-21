package com.zzour.android.test;

import java.util.ArrayList;
import java.util.Iterator;

import com.zzour.android.models.ShopList;
import com.zzour.android.models.ShopSummaryContent;
import com.zzour.android.models.dao.ShopSummaryDAO;
import com.zzour.android.network.api.DataApi;

import android.test.AndroidTestCase;
import android.util.Log;

public class ShopSummaryDAOTest extends AndroidTestCase{
	public void testInsertShopSummary() throws Throwable {
		// test insert shop summary
		ShopList shopList = DataApi.getShopList();
		if (shopList != null){
			Log.d("ZZOUR", shopList.getmSearchKeyword());
		}
		ArrayList<ShopSummaryContent> shops = shopList.getmShops();
		Iterator<ShopSummaryContent> it = shops.iterator();
		ShopSummaryDAO dao = new ShopSummaryDAO(this.mContext);
		int order = 0;
		while (it.hasNext()){
			dao.insert(it.next(), order);
			order++;
		}
		Log.d("ZZOUR", "insert " + order + " shop summary content to db");
		
		// test get shop summary
		ArrayList<ShopSummaryContent> shops1 = dao.get(15);
		Log.d("ZZOUR", "get " + shops1.size() + " shop summary content from db");
		
		// test increse order of records
		dao.updateOrder(2);
		ArrayList<ShopSummaryContent> shops2 = dao.get(2, 15);
		Log.d("ZZOUR", "get " + shops2.size() + " shop summary content from db after increase order");
		
		// test clean db
		dao.clean();
		ArrayList<ShopSummaryContent> shops3 = dao.get(15);
		Log.d("ZZOUR", "get " + shops3.size() + " shop summary content from db after clean");
	}
}
