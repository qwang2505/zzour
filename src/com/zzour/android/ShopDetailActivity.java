package com.zzour.android;

import java.util.Iterator;

import com.zzour.android.models.ShopDetailContent;
import com.zzour.android.network.api.DataApi;
import com.zzour.android.utils.ImageTool;
import com.zzour.android.views.adapters.ShopDetailAdapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class ShopDetailActivity extends Activity{
	
	private static final String TAG = "ZZOUR";
	
	private ShopDetailAdapter mAdapter = null;
	private View mHeaderView = null;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		Intent intent = this.getIntent();
		int shopId = intent.getIntExtra("shop_id", -1);
		if (shopId == -1){
			Log.e(TAG, "no shop id in intent! why this activity start?");
			return;
		}
		
		setContentView(R.layout.shop_detail);
		
		ShopDetailContent shop = DataApi.getShopDetailById(shopId);
		ExpandableListView list = (ExpandableListView)findViewById(R.id.shop_detail);
		// show shop detail
		mAdapter = new ShopDetailAdapter(this);
		// init top view, and add to expendable list as a header
		this.initHeaderView(shop, list);
		Iterator<String> keys = shop.getFoods().keySet().iterator();
		while (keys.hasNext()){
			String cat = keys.next();
			mAdapter.setFoods(cat, shop.getFoods().get(cat));
		}
		list.setAdapter(mAdapter);
	}
	
	private void initHeaderView(ShopDetailContent shop, ExpandableListView list){
		mHeaderView = this.getLayoutInflater().inflate(R.layout.shop_detail_top, null);
		// initial basic shop information
		TextView text = (TextView)mHeaderView.findViewById(R.id.shop_name);
		text.setText(shop.getName());
		text = (TextView)mHeaderView.findViewById(R.id.shop_desc);
		text.setText(shop.getDesc());
		text = (TextView)mHeaderView.findViewById(R.id.shop_addr);
		text.setText(shop.getAddress());
		RatingBar rate = (RatingBar)mHeaderView.findViewById(R.id.shop_rating);
		rate.setRating(shop.getRate());
		// initial top banner
		ImageView banner = (ImageView)mHeaderView.findViewById(R.id.shop_banner);
		DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels - 20;
        // TODO get rael image in new thread
		Bitmap bmp = ImageTool.getBitmapByStream(getResources().openRawResource(R.drawable.scroll_image_1), 
				width, 
				(int)getResources().getDimension(R.dimen.detail_banner_height));
		banner.setImageBitmap(bmp);
		// TODO initial recommends foods images
		list.addHeaderView(mHeaderView);
	}
}
