package com.zzour.android;

import java.util.ArrayList;
import java.util.Iterator;

import com.zzour.android.base.BaseActivity;
import com.zzour.android.models.Food;
import com.zzour.android.models.ShopDetailContent;
import com.zzour.android.models.ShoppingCart;
import com.zzour.android.network.api.ShopDetailApi;
import com.zzour.android.utils.ActivityTool;
import com.zzour.android.utils.ImageTool;
import com.zzour.android.views.adapters.ShopDetailAdapter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class ShopDetailActivity extends BaseActivity{
	
	private static final String TAG = "ZZOUR";
	
	private ShopDetailAdapter mAdapter = null;
	private View mHeaderView = null;
	private ShopDetailContent mShop = null;
	private ImageView mBanner = null;
	
	private Handler mLoadImageHandler = new Handler();
	private Handler mToastHandler = new Handler();
	private Bitmap mBitmap = null;
	
	private int mShopId = -1;
	
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
		mShopId = intent.getIntExtra("shop_id", -1);
		if (mShopId == -1){
			Log.e(TAG, "no shop id in intent! why this activity start?");
			return;
		}
		
		setContentView(R.layout.shop_detail);
		
		// load data in async task
		new LoadingTask(this).execute();
	}
	
	public void setShop(ShopDetailContent shop){
		this.mShop = shop;
	}
	
	private void show(){
		//mShop = ShopDetailApi.getShopDetailById(mShopId, this);
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
				// if buy any, save into cart
				ArrayList<Food> foods = mAdapter.getBoughtFoods();
				if (foods.size() == 0){
					// give out toast
					mToastHandler.post(new Runnable(){
		 	    		   public void run(){
		 	    			  Toast.makeText(ShopDetailActivity.this, "您没有选择任何商品", Toast.LENGTH_SHORT).show();
		 	    		   }
		 	    	   });
					return;
				}
				// TODO if do not log in, redirect to login activity, but save food info.
				// TODO if already log in, go to shopping cart.
				ShoppingCart.saveFoods(mShop, foods);
				Log.d(TAG, "save foods");
				ActivityTool.startActivity(ShopDetailActivity.this, ShoppingCartActivity.class);
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
		mBanner = (ImageView)mHeaderView.findViewById(R.id.shop_banner);
		DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final int width = metrics.widthPixels - 20;
        // get real image in new thread
		Bitmap bmp = ImageTool.getBitmapByStream(R.drawable.logo, getResources().openRawResource(R.drawable.logo), 
				width, 
				(int)getResources().getDimension(R.dimen.detail_banner_height));
		mBanner.setImageBitmap(bmp);
		new Thread(new Runnable() {
	 	       @Override
	 	       public void run() {
	 	    	   mBitmap = ImageTool.getBitmapByUrl(mShop.getBanner(), width, (int)getResources().getDimension(R.dimen.detail_banner_height), ShopDetailActivity.this);
	 	    	   mLoadImageHandler.post(new Runnable(){
	 	    		   public void run(){
	 	    			   if (mBitmap == null){
	 	    				   return;
	 	    			   }
	 	    			   mBanner.setImageBitmap(mBitmap);
	 	    		   }
	 	    	   });
	 	       }
	 	    }).start();
		// TODO initial recommends foods images
		list.addHeaderView(mHeaderView);
	}
	
	private class LoadingTask extends AsyncTask<String, Void, Boolean> {
		
		private ShopDetailActivity activity = null;
		private ProgressDialog mDialog = null;

		public LoadingTask(ShopDetailActivity activity) {
	        this.activity = activity;
	        this.mDialog = new ProgressDialog(activity);
	    }
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			ShopDetailContent shop = ShopDetailApi.getShopDetailById(mShopId, this.activity);
			activity.setShop(shop);
			return true;
		}
		
		protected void onPreExecute() {
	        this.mDialog.setMessage(this.activity.getResources().getString(R.string.loading_progress_text));
	        this.mDialog.show();
	    }

	        @Override
	    protected void onPostExecute(final Boolean success) {
	        if (mDialog.isShowing()) {
	        	mDialog.dismiss();
	        }
	        this.activity.show();
	    }
	}
}
