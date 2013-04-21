package com.zzour.android.test;

import java.util.ArrayList;
import java.util.Iterator;

import com.zzour.android.models.ShopList;
import com.zzour.android.models.ShopSummaryContent;
import com.zzour.android.models.dao.ShopSummaryDAO;
import com.zzour.android.network.api.ShopListApi;

import android.test.AndroidTestCase;
import android.util.Log;

public class ShopSummaryDAOTest extends AndroidTestCase{
	public void testInsertShopSummary() throws Throwable {
		ShopSummaryDAO dao = new ShopSummaryDAO(mContext);
		dao.clean();
		ShopSummaryContent s = new ShopSummaryContent(1, false, "1", "1", "1", 1.0f);
		dao.insert(s, 0);
		Log.d("ZZOUR", "insert shop summary content to db");
		
		ArrayList<ShopSummaryContent> shops = dao.get();
		Log.d("ZZOUR", "got " + shops.size());
		Log.d("ZZOUR", "get id " + shops.get(0).getId() + " and name " + shops.get(0).getName());
		s = new ShopSummaryContent(1, false, "2", "2", "2", 2.0f);
		dao.insert(s, 1);
		shops = dao.get();
		Log.d("ZZOUR", "got " + shops.size());
		Log.d("ZZOUR", "get id " + shops.get(0).getId() + " and name " + shops.get(0).getName());
		
		
		s = new ShopSummaryContent(2, false, "2", "2", "2", 2.0f);
		dao.insert(s, 2);
		shops = dao.get();
		Log.d("ZZOUR", "got " + shops.size());
		Log.d("ZZOUR", "get id " + shops.get(0).getId() + " and name " + shops.get(0).getName());
		dao.clean();
	}
}
