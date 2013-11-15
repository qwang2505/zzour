package com.zzour.android;

import java.util.Iterator;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.zzour.android.models.Food;
import com.zzour.android.models.OrderDetail;
import com.zzour.android.models.OrderLog;
import com.zzour.android.models.User;
import com.zzour.android.network.api.AcountApi;
import com.zzour.android.network.api.MyOrderApi;
import com.zzour.android.network.api.results.ApiResult;
import com.zzour.android.network.api.results.OrderDetailResult;
import com.zzour.android.settings.GlobalSettings;
import com.zzour.android.settings.LocalPreferences;
import com.zzour.android.views.CancelOrderDialog;
import com.zzour.android.views.CustomDialog;

public class OrderDetailActivity extends BaseActivity{
	
	private OrderDetail mOrder = null;
	private int mOrderId = -1;
	private ApiResult mFinishOrderResult = null;
	private ApiResult mPushOrderResult = null;
	private ApiResult mCancelOrderResult = null;

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
		
		// set button listeners
		((Button)findViewById(R.id.refresh)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				new LoadingTask(OrderDetailActivity.this, mOrderId).execute();
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
		((TextView)findViewById(R.id.addr)).setText(mOrder.getAddr().getAddr());
		((TextView)findViewById(R.id.phone)).setText(mOrder.getAddr().getPhone());
		((TextView)findViewById(R.id.remark)).setText(mOrder.getRemark() == null ? "" : mOrder.getRemark());
		// show logs
		Iterator<OrderLog> iter = mOrder.getLogs().iterator();
		LinearLayout parent = (LinearLayout)findViewById(R.id.logs);
		parent.removeAllViews();
		while (iter.hasNext()){
			OrderLog log = iter.next();
			View view = LayoutInflater.from(OrderDetailActivity.this).inflate(R.layout.order_log, null);
			((TextView)view.findViewById(R.id.log_time)).setText(log.getTime());
			String text = Html.fromHtml(log.getRemark()).toString();
			((TextView)view.findViewById(R.id.log_text)).setText(text);
			parent.addView(view, parent.getChildCount());
		}
		Iterator<Food> iter2 = mOrder.getFoods().iterator();
		LinearLayout foodParent = (LinearLayout)findViewById(R.id.foods);
		foodParent.removeAllViews();
		while (iter2.hasNext()){
			Food food = iter2.next();
			View view = LayoutInflater.from(OrderDetailActivity.this).inflate(R.layout.order_food, null);
			((TextView)view.findViewById(R.id.food_name)).setText(food.getName());
			((TextView)view.findViewById(R.id.food_price)).setText("￥" + food.getPrice());
			((TextView)view.findViewById(R.id.food_count)).setText(food.getBuyCount() + "份");
			foodParent.addView(view, foodParent.getChildCount() - 1);
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
		// push order
		Button pushButton = (Button)findViewById(R.id.push_order);
		// if status not right, hide button
		if (!GlobalSettings.canPushOrder(mOrder.getStatus())){
			pushButton.setVisibility(Button.GONE);
		} else {
			// add click listener
			pushButton.setVisibility(Button.VISIBLE);
			pushButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// if can not push order, show toast message
					if (!mOrder.isCd()){
						String msg = mOrder.getCdMsg();
						if (msg.length() == 0){
							msg = "现在还不能催单哦";
						}
						showPushOrderMessageDialog(msg);
						return;
					}
					// give out dialog
					showPushOrderDialog();
				}
			});
		}
		// cancel order
		Button cancelButton = (Button)findViewById(R.id.cancel_order);
		if (!GlobalSettings.canCancelOrder(mOrder.getStatus()) &&
				!GlobalSettings.canForceCancelOrder(mOrder.getStatus())){
			cancelButton.setVisibility(Button.GONE);
		} else {
			cancelButton.setVisibility(Button.VISIBLE);
			cancelButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showCancelOrderDialog();
				}
			});
		}
	}
	
	private void showCancelOrderDialog(){
		final CancelOrderDialog.Builder builder = new CancelOrderDialog.Builder(OrderDetailActivity.this);
		builder.setPositiveButtonId(R.id.positiveButton);
		builder.setNegativeButtonId(R.id.negativeButton);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	// get reason from dialog
            	String reason = builder.getReason();
            	// start task to cancel order
            	new CancelOrderTask(OrderDetailActivity.this, mOrderId, reason, mOrder.getStatus()).execute();
            	dialog.dismiss();
            }
        });
		CancelOrderDialog dialog = builder.create();
		dialog.show();
	}
	
	private void showPushOrderDialog(){
		CustomDialog.Builder builder = new CustomDialog.Builder(OrderDetailActivity.this);
		builder.setLayoutId(R.layout.push_order_dialog);
		builder.setStyleId(R.style.Dialog);
		builder.setPositiveButtonId(R.id.positiveButton);
		builder.setNegativeButtonId(R.id.negativeButton);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	// start task to push order
            	new PushOrderTask(OrderDetailActivity.this, mOrderId).execute();
            	dialog.dismiss();
            }
        });
		CustomDialog dialog = builder.create();
		dialog.show();
	}
	
	private void showPushOrderMessageDialog(String msg){
		CustomDialog.Builder builder = new CustomDialog.Builder(OrderDetailActivity.this);
		builder.setLayoutId(R.layout.push_order_message_dialog);
		builder.setStyleId(R.style.Dialog);
		builder.setNegativeButtonId(R.id.negativeButton);
		builder.setMessageId(R.id.message);
		builder.setMessage(msg);
		builder.setNegativeButton("了解", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
		CustomDialog dialog = builder.create();
		dialog.show();
	}
	
	private void showPushOrderFinishedDialog(){
		CustomDialog.Builder builder = new CustomDialog.Builder(OrderDetailActivity.this);
		builder.setLayoutId(R.layout.push_order_finish_dialog);
		builder.setStyleId(R.style.Dialog);
		builder.setNegativeButtonId(R.id.negativeButton);
		builder.setNegativeButton("好的", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                new LoadingTask(OrderDetailActivity.this, mOrderId).execute();
            }
        });
		CustomDialog dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();
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
			OrderDetailResult result = MyOrderApi.getOrderDetail(orderId, user);
			if (result != null && result.isNeedLogin() && !result.isSuccess()){
				AcountApi.loginNormal(user.getUserName(), user.getNickName(), user.getPwd(), user.getType(), activity);
				result = MyOrderApi.getOrderDetail(orderId, user);
			}
			if (result == null){
				return true;
			}
			OrderDetail order = result.getOrder();
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
	        // load order detail finished, update ui
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
			// TODO go to comment page
			Toast.makeText(getApplicationContext(), "确认收货成功", Toast.LENGTH_SHORT).show();
			// start loading task to refresh order detail data
			new LoadingTask(this, mOrderId).execute();
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
			// do not check
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
	
	private class PushOrderTask extends AsyncTask<String, Void, Boolean> {
		
		private OrderDetailActivity activity = null;
		private int orderId = -1;
		private ProgressDialog mDialog = null;

		public PushOrderTask(OrderDetailActivity activity, int orderId) {
	        this.activity = activity;
	        this.orderId = orderId;
	        this.mDialog = new ProgressDialog(activity);
	    }
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			// call api to get order detail
			User user = LocalPreferences.getUser(activity);
			// call finish order
			// do not check
			ApiResult result = MyOrderApi.pushOrder(orderId, user);
			activity.setPushOrderResult(result);
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
	        // load order detail finished, update ui
	        activity.showPushOrder();
	    }
	}
	
	public void setPushOrderResult(ApiResult result){
		this.mPushOrderResult = result;
	}
	
	public void showPushOrder(){
		if (mPushOrderResult == null || !mPushOrderResult.isSuccess()){
			Toast.makeText(getApplicationContext(), "催单失败，请重新尝试", Toast.LENGTH_SHORT).show();
			return;
		} else {
			// give out dialog
			showPushOrderFinishedDialog();
			return;
		}
	}
	
	private class CancelOrderTask extends AsyncTask<String, Void, Boolean> {
		
		private OrderDetailActivity activity = null;
		private int orderId = -1;
		private String reason = null;
		private int status = -1;
		private ProgressDialog mDialog = null;

		public CancelOrderTask(OrderDetailActivity activity, int orderId, String reason, int status) {
	        this.activity = activity;
	        this.orderId = orderId;
	        this.reason = reason;
	        this.status = status;
	        this.mDialog = new ProgressDialog(activity);
	    }
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			// call api to cancel order
			User user = LocalPreferences.getUser(activity);
			ApiResult result = null;
			if (GlobalSettings.canCancelOrder(status)){
				result = MyOrderApi.cancelOrder(orderId, reason, user);
			} else {
				result = MyOrderApi.wCancelOrder(orderId, user);
			}
			activity.setCancelOrderResult(result);
			return true;
		}
		
		protected void onPreExecute() {
	        this.mDialog.setMessage("处理中，请稍候。。。");
	        this.mDialog.show();
	    }

	        @Override
	    protected void onPostExecute(final Boolean success) {
	        if (mDialog.isShowing()) {
	        	mDialog.dismiss();
	        }
	        // load order detail finished, update ui
	        if (GlobalSettings.canCancelOrder(status)){
	        	activity.showCancelOrder();
	        } else if (GlobalSettings.canForceCancelOrder(status)){
	        	activity.showForceCancelOrder();
	        }
	    }
	}
	
	public void setCancelOrderResult(ApiResult result){
		this.mCancelOrderResult = result;
	}
	public void showCancelOrder(){
		if (mCancelOrderResult == null || !mCancelOrderResult.isSuccess()){
			Toast.makeText(getApplicationContext(), "退单失败，请重新尝试", Toast.LENGTH_SHORT).show();
			return;
		} else {
			// give out dialog
			showCancelOrderFinishedDialog();
			return;
		}
	}
	public void showForceCancelOrder(){
		if (mCancelOrderResult == null || !mCancelOrderResult.isSuccess()){
			Toast.makeText(getApplicationContext(), "强行退单失败，请重新尝试", Toast.LENGTH_SHORT).show();
			return;
		} else {
			// give out dialog
			showForceCancelOrderFinishedDialog();
			return;
		}
	}
	
	private void showCancelOrderFinishedDialog(){
		CustomDialog.Builder builder = new CustomDialog.Builder(OrderDetailActivity.this);
		builder.setLayoutId(R.layout.cancel_order_finish_dialog);
		builder.setStyleId(R.style.Dialog);
		builder.setNegativeButtonId(R.id.negativeButton);
		builder.setNegativeButton("好的", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                new LoadingTask(OrderDetailActivity.this, mOrderId).execute();
            }
        });
		CustomDialog dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();
	}
	
	private void showForceCancelOrderFinishedDialog(){
		CustomDialog.Builder builder = new CustomDialog.Builder(OrderDetailActivity.this);
		builder.setLayoutId(R.layout.force_cancel_order_finish_dialog);
		builder.setStyleId(R.style.Dialog);
		builder.setNegativeButtonId(R.id.negativeButton);
		builder.setNegativeButton("好的", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // start loading task to refresh order detail data
    			new LoadingTask(OrderDetailActivity.this, mOrderId).execute();
            }
        });
		CustomDialog dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();
	}
}
