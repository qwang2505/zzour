package com.zzour.android;

import java.util.ArrayList;
import java.util.Iterator;

import com.zzour.android.base.BaseActivity;
import com.zzour.android.models.Food;
import com.zzour.android.models.ShopDetailContent;
import com.zzour.android.models.ShoppingCart;
import com.zzour.android.models.dao.CollectionDAO;
import com.zzour.android.network.api.ShopDetailApi;
import com.zzour.android.settings.LocalPreferences;
import com.zzour.android.utils.ActivityTool;
import com.zzour.android.views.adapters.ShopDetailAdapter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class ShopDetailActivity extends BaseActivity{
	
	private final int LOGIN_REQUEST_CODE = 100;
	private final int DIAL_LOGIN_REQUEST_CODE = 101;
	
	private ShopDetailAdapter mAdapter = null;
	private View mHeaderView = null;
	private ShopDetailContent mShop = null;
	private Handler mToastHandler = new Handler();
	
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
		CollectionDAO dao = new CollectionDAO(this);
		boolean collected = dao.exists(mShopId);
		if (!collected) {
			Button btn = (Button) findViewById(R.id.collect_btn);
			btn.setVisibility(Button.VISIBLE);
			btn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// add to collect
							if (mShop == null) {
								Toast.makeText(getApplicationContext(),
										"请加载完成后再进行收藏", Toast.LENGTH_SHORT)
										.show();
								return;
							}
							CollectionDAO dao = new CollectionDAO(
									ShopDetailActivity.this);
							dao.insert(mShop);
							Toast.makeText(getApplicationContext(), "收藏成功", Toast.LENGTH_SHORT).show();
							return;
						}
					});
		}
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
					if (foods.size() == 0){
						// give out toast
						mToastHandler.post(new Runnable(){
			 	    		   public void run(){
			 	    			  Toast.makeText(ShopDetailActivity.this, "您没有选择任何商品", Toast.LENGTH_SHORT).show();
			 	    		   }
			 	    	   });
						return;
					}
					// save foods to shopping cart
					ShoppingCart.saveFoods(mShop, foods);
					// check if already login. If not, require login first
					if (!LocalPreferences.authed(ShopDetailActivity.this)){
						// start activity for result to login
						Intent intent2 = new Intent(ShopDetailActivity.this, LoginActivity.class);
						ActivityTool.startActivityForResult(ShopDetailActivity.this, LoginActivity.class, LOGIN_REQUEST_CODE, intent2);
						Toast.makeText(ShopDetailActivity.this, "请先登陆", Toast.LENGTH_SHORT).show();
						return;
					}
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
					// save to shopping cart
					ShoppingCart.saveFoods(mShop, foods);
					// TODO if dial, need login or not?
					if (!LocalPreferences.authed(ShopDetailActivity.this)){
						// start activity for result to login
						Intent intent2 = new Intent(ShopDetailActivity.this, LoginActivity.class);
						ActivityTool.startActivityForResult(ShopDetailActivity.this, LoginActivity.class, DIAL_LOGIN_REQUEST_CODE, intent2);
						Toast.makeText(ShopDetailActivity.this, "请先登陆", Toast.LENGTH_SHORT).show();
						return;
					}
					// go to shopping cart.
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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK){
	        switch (requestCode) {
	        case LOGIN_REQUEST_CODE:
	        	ActivityTool.startActivity(ShopDetailActivity.this, ShoppingCartActivity.class);
	            break;
	        case DIAL_LOGIN_REQUEST_CODE:
				// go to shopping cart.
				Intent intent = new Intent(ShopDetailActivity.this, ShoppingDialActivity.class);
				intent.putExtra("shop_id", mShop.getId());
				intent.putExtra("shop_phone", mShop.getTelephone());
				ActivityTool.startActivity(ShopDetailActivity.this, ShoppingDialActivity.class, intent);
	        	break;
	        default:
	            break;
	        }
		}
	}
}
