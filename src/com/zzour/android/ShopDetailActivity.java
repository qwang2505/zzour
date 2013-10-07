package com.zzour.android;

import java.util.ArrayList;
import java.util.Iterator;

import com.zzour.android.base.BaseActivity;
import com.zzour.android.models.Food;
import com.zzour.android.models.ShopDetailContent;
import com.zzour.android.models.ShoppingCart;
import com.zzour.android.models.User;
import com.zzour.android.network.api.ShopDetailApi;
import com.zzour.android.settings.LocalPreferences;
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
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
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
		
		((Button)findViewById(R.id.back_btn)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				ShopDetailActivity.this.onBackPressed();
				return;
			}
		});
		
		// load data in async task
		new LoadingTask(this).execute();
	}
	
	public void setShop(ShopDetailContent shop){
		this.mShop = shop;
	}
	
	public boolean isOnline(){
		if (this.mShop == null){
			Log.e(TAG, "shop is null while calling is online");
			return false;
		}
		return this.mShop.isOnlineOrder();
	}
	
	private void show(){
		// init title bar text
		TextView title_bar = (TextView)findViewById(R.id.title_bar_text_view);
		title_bar.setText(mShop.getName());
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
		for (int i=0; i < mAdapter.getGroupCount(); i++){
			list.expandGroup(i);
		}
		list.setGroupIndicator(null);//除去自带的箭头
		// for online and not online, show different button
		if (mShop.isOnlineOrder()){
			Button btn = (Button)findViewById(R.id.go_to_cart);
			btn.setVisibility(Button.VISIBLE);
			btn.setOnClickListener(new OnClickListener(){
	
				@Override
				public void onClick(View view) {
					// if buy any, save into cart
					ArrayList<Food> foods = mAdapter.getBoughtFoods();
					/*if (foods.size() == 0){
						// give out toast
						mToastHandler.post(new Runnable(){
			 	    		   public void run(){
			 	    			  Toast.makeText(ShopDetailActivity.this, "您没有选择任何商品", Toast.LENGTH_SHORT).show();
			 	    		   }
			 	    	   });
						return;
					}*/
					// TODO check if already login. If not, require login first
					User user = LocalPreferences.getUser(ShopDetailActivity.this);
					if (user == null){
						// give out toast
						mToastHandler.post(new Runnable(){
			 	    		   public void run(){
			 	    			  Toast.makeText(ShopDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
			 	    		   }
			 	    	   });
						return;
					}
					// go to shopping cart.
					ShoppingCart.saveFoods(mShop, foods);
					Log.d(TAG, "save foods");
					ActivityTool.startActivity(ShopDetailActivity.this, ShoppingCartActivity.class);
				}
			});
		} else {
			Button btn = (Button)findViewById(R.id.go_to_dial);
			btn.setVisibility(Button.VISIBLE);
			btn.setText("拨打电话 " + mShop.getTelephone());
			btn.setOnClickListener(new OnClickListener(){
				
				@Override
				public void onClick(View view) {
					// if buy any, save into cart
					ArrayList<Food> foods = mAdapter.getBoughtFoods();
					// TODO if dial, need login or not?
					User user = LocalPreferences.getUser(ShopDetailActivity.this);
					if (user == null){
						// give out toast
						mToastHandler.post(new Runnable(){
			 	    		   public void run(){
			 	    			  Toast.makeText(ShopDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
			 	    		   }
			 	    	   });
						return;
					}
					// go to shopping cart.
					ShoppingCart.saveFoods(mShop, foods);
					Intent intent = new Intent(ShopDetailActivity.this, ShoppingDialActivity.class);
					intent.putExtra("shop_id", mShop.getId());
					intent.putExtra("shop_phone", mShop.getTelephone());
					ActivityTool.startActivity(ShopDetailActivity.this, ShoppingDialActivity.class, intent);
				}
			});
		}
	}
	
	private void initHeaderView(ShopDetailContent shop, ExpandableListView list){
		mHeaderView = this.getLayoutInflater().inflate(R.layout.shop_detail_top, null);
		// initial basic shop information
		TextView text = (TextView)mHeaderView.findViewById(R.id.send_time);
		text.setText("平均速度：" + shop.getSendTime());
		text = (TextView)mHeaderView.findViewById(R.id.praise_count);
		// TODO use correct data
		text.setText(Float.valueOf(shop.getPraiseRate()).intValue() + "人认为该店不错");
		text = (TextView)mHeaderView.findViewById(R.id.telephone);
		text.setText("电话：" + shop.getTelephone());
		text = (TextView)mHeaderView.findViewById(R.id.shop_hours);
		text.setText("营业时间：" + shop.getShopHours());
		text = (TextView)mHeaderView.findViewById(R.id.online_order);
		if (shop.isOnlineOrder()){
			text.setText("本店支持网上订餐");
		} else {
			text.setText("本店只支持电话订餐");
		}
		RatingBar rate = (RatingBar)mHeaderView.findViewById(R.id.shop_rating);
		rate.setRating(shop.getGrade());
		WebView notice = (WebView)mHeaderView.findViewById(R.id.notice);
		//notice.loadData("<style type='text/css'>*{font-size: 14px;background-color:#efecea}</style>" + shop.getNotice(), "text/html; charset=UTF-8", null);
		notice.loadData(shop.getNotice(), "text/html; charset=UTF-8", null);
		// initial top banner
		//mBanner = (ImageView)mHeaderView.findViewById(R.id.shop_banner);
		DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final int width = metrics.widthPixels - 20;
        // get real image in new thread
        /*
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
	 	*/
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
