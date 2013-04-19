package com.zzour.android;

import java.util.ArrayList;
import java.util.Iterator;

import com.zzour.android.models.Food;
import com.zzour.android.models.ShopDetailContent;
import com.zzour.android.models.ShoppingCart;
import com.zzour.android.network.api.DataApi;
import com.zzour.android.utils.ImageTool;
import com.zzour.android.views.adapters.ShopDetailAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class ShopDetailActivity extends Activity{
	
	private static final String TAG = "ZZOUR";
	
	private ShopDetailAdapter mAdapter = null;
	private View mHeaderView = null;
	private ShopDetailContent mShop = null;
	
	@Override
	public void onResume(){
		super.onResume();
		if (mAdapter != null){
			mAdapter.notifyDataSetChanged();
			mAdapter.updateTotal();
		}
	}

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
		
		mShop = DataApi.getShopDetailById(shopId);
		ExpandableListView list = (ExpandableListView)findViewById(R.id.shop_detail);
		// show shop detail
		mAdapter = new ShopDetailAdapter(this);
		// init top view, and add to expendable list as a header
		this.initHeaderView(mShop, list);
		Iterator<String> keys = mShop.getFoods().keySet().iterator();
		while (keys.hasNext()){
			String cat = keys.next();
			mAdapter.setFoods(cat, mShop.getFoods().get(cat));
		}
		list.setAdapter(mAdapter);
		
		Button btn = (Button)findViewById(R.id.go_to_cart);
		btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				// TODO if buy any, save into cart
				ArrayList<Food> foods = mAdapter.getBoughtFoods();
				if (foods.size() == 0){
					// TODO give out toast
					return;
				}
				// TODO if do not log in, redirect to login activity, but save food info.
				// TODO if already log in, go to shopping cart.
				ShoppingCart.saveFoods(mShop, foods);
				Intent intent = new Intent(ShopDetailActivity.this, ShoppingCartActivity.class);
				startActivity(intent);
			}
		});
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
		Bitmap bmp = ImageTool.getBitmapByStream(R.drawable.scroll_image_1, getResources().openRawResource(R.drawable.scroll_image_1), 
				width, 
				(int)getResources().getDimension(R.dimen.detail_banner_height));
		banner.setImageBitmap(bmp);
		// TODO initial recommends foods images
		list.addHeaderView(mHeaderView);
	}
}
