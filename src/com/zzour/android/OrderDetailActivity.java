package com.zzour.android;

import java.util.Iterator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzour.android.base.BaseActivity;
import com.zzour.android.models.ApiResult;
import com.zzour.android.models.Food;
import com.zzour.android.models.OrderDetail;
import com.zzour.android.models.OrderLog;
import com.zzour.android.models.User;
import com.zzour.android.network.api.MyOrderApi;
import com.zzour.android.settings.GlobalSettings;
import com.zzour.android.settings.LocalPreferences;

public class OrderDetailActivity extends BaseActivity{
	
	private OrderDetail mOrder = null;
	private int mOrderId = -1;
	private ApiResult mFinishOrderResult = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_detail);
		
		// get data from bundle
		Intent intent = this.getIntent();
		String orderId = intent.getStringExtra("order_id");
		mOrderId = Integer.valueOf(orderId);
		String shopName = intent.getStringExtra("shop_name");
		String time = intent.getStringExtra("time");
		int status = intent.getIntExtra("status", 0);
		float price = intent.getFloatExtra("price", 0);
		// set data
		((TextView)findViewById(R.id.shop_name)).setText(shopName);
		((TextView)findViewById(R.id.time)).setText(time);
		((TextView)findViewById(R.id.status)).setText(GlobalSettings.getStatusShortDesc(status));
		((TextView)findViewById(R.id.price)).setText(price + "");
		
		// set button listeners
		((Button)findViewById(R.id.back_btn)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				OrderDetailActivity.this.onBackPressed();
				return;
			}
		});
		
		// get order detail in loading task
		new LoadingTask(this, mOrderId).execute();
	}

	public void show() {
		if (mOrder == null){
			Toast.makeText(this, "订单详情加载失败，请重试", Toast.LENGTH_SHORT);
			return;
		}
		// time
		((TextView)findViewById(R.id.time)).setText(mOrder.getTime());
		((TextView)findViewById(R.id.status)).setText(GlobalSettings.getStatusShortDesc(mOrder.getStatus()));
		((TextView)findViewById(R.id.price)).setText(mOrder.getPrice() + "");
		// address
		Log.e(TAG, mOrder.getAddr().getAddr());
		Log.e(TAG, mOrder.getRemark() == null ? "" : mOrder.getRemark());
		((TextView)findViewById(R.id.addr)).setText(mOrder.getAddr().getAddr());
		((TextView)findViewById(R.id.phone)).setText(mOrder.getAddr().getPhone());
		((TextView)findViewById(R.id.remark)).setText(mOrder.getRemark() == null ? "" : mOrder.getRemark());
		// show logs
		Iterator<OrderLog> iter = mOrder.getLogs().iterator();
		while (iter.hasNext()){
			OrderLog log = iter.next();
			View view = LayoutInflater.from(OrderDetailActivity.this).inflate(R.layout.order_log, null);
			((TextView)view.findViewById(R.id.log_time)).setText(log.getTime());
			String text = Html.fromHtml(log.getRemark()).toString();
			((TextView)view.findViewById(R.id.log_text)).setText(text);
			LinearLayout parent = (LinearLayout)findViewById(R.id.logs);
			parent.addView(view, parent.getChildCount() - 1);
		}
		Iterator<Food> iter2 = mOrder.getFoods().iterator();
		while (iter2.hasNext()){
			Food food = iter2.next();
			View view = LayoutInflater.from(OrderDetailActivity.this).inflate(R.layout.order_food, null);
			((TextView)view.findViewById(R.id.food_name)).setText(food.getName());
			((TextView)view.findViewById(R.id.food_price)).setText("￥" + food.getPrice());
			((TextView)view.findViewById(R.id.food_count)).setText(food.getBuyCount() + "份");
			LinearLayout parent = (LinearLayout)findViewById(R.id.foods);
			parent.addView(view, parent.getChildCount() - 1);
		}
		// finish order
		Button btn = (Button)findViewById(R.id.finish_order);
		if (GlobalSettings.canFinishOrder(mOrder.getStatus())){
			btn.setVisibility(Button.VISIBLE);
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					new FinishOrderTask(OrderDetailActivity.this, mOrderId).execute();
				}
			});
		}
	}
	
	public void setOrderDetailResult(OrderDetail order){
		mOrder = order;
	}
	
	private class LoadingTask extends AsyncTask<String, Void, Boolean> {
		
		private OrderDetailActivity activity = null;
		private int orderId = -1;
		private ProgressDialog mDialog = null;

		public LoadingTask(OrderDetailActivity activity, int orderId) {
	        this.activity = activity;
	        this.orderId = orderId;
	        this.mDialog = new ProgressDialog(activity);
	    }
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			// call api to get order detail
			User user = LocalPreferences.getUser(activity);
			OrderDetail order = MyOrderApi.getOrderDetail(orderId, user);
			activity.setOrderDetailResult(order);
			return true;
		}
		
		protected void onPreExecute() {
	        this.mDialog.setMessage("加载中，请稍候。。。");
	        this.mDialog.show();
	    }

	        @Override
	    protected void onPostExecute(final Boolean success) {
	        if (mDialog.isShowing()) {
	        	mDialog.dismiss();
	        }
	        // load order detai finished, update ui
	        activity.show();
	    }
	}
	
	public void setFinishOrderResult(ApiResult result){
		this.mFinishOrderResult = result;
	}
	
	public void showFinishOrder(){
		if (mFinishOrderResult == null || !mFinishOrderResult.isSuccess()){
			Toast.makeText(getApplicationContext(), "确认收货失败，请重新尝试", Toast.LENGTH_SHORT).show();
			return;
		} else {
			Toast.makeText(getApplicationContext(), "确认收货成功", Toast.LENGTH_SHORT).show();
			// TODO update status text
			((TextView)findViewById(R.id.status)).setText("已完成");
			return;
		}
	}
	
	private class FinishOrderTask extends AsyncTask<String, Void, Boolean> {
		
		private OrderDetailActivity activity = null;
		private int orderId = -1;
		private ProgressDialog mDialog = null;

		public FinishOrderTask(OrderDetailActivity activity, int orderId) {
	        this.activity = activity;
	        this.orderId = orderId;
	        this.mDialog = new ProgressDialog(activity);
	    }
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			// call api to get order detail
			User user = LocalPreferences.getUser(activity);
			// call finish order
			ApiResult result = MyOrderApi.finishOrder(orderId, user);
			activity.setFinishOrderResult(result);
			return true;
		}
		
		protected void onPreExecute() {
	        this.mDialog.setMessage("加载中，请稍候。。。");
	        this.mDialog.show();
	    }

	        @Override
	    protected void onPostExecute(final Boolean success) {
	        if (mDialog.isShowing()) {
	        	mDialog.dismiss();
	        }
	        // load order detai finished, update ui
	        activity.showFinishOrder();
	    }
	}
}
