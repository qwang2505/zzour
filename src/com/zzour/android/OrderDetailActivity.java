package com.zzour.android;

import java.util.Iterator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zzour.android.base.BaseActivity;
import com.zzour.android.models.Food;
import com.zzour.android.models.Order;
import com.zzour.android.models.dao.OrderDAO;

public class OrderDetailActivity extends BaseActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_detail);
		
		Intent intent = this.getIntent();
		String orderId = intent.getStringExtra("order_id");
		OrderDAO dao = new OrderDAO(this);
		Order order = dao.getOrder(orderId);
		
		((TextView)findViewById(R.id.order_detail_total_price)).setText(String.valueOf(order.getTotalPrice()));
		((TextView)findViewById(R.id.order_detail_address_name)).setText(order.getAddress().getName());
		((TextView)findViewById(R.id.order_detail_address_phone)).setText(order.getAddress().getPhone());
		((TextView)findViewById(R.id.order_detail_address_detail)).setText(order.getAddress().getAddr());
		((TextView)findViewById(R.id.order_detail_time_info_detail)).setText(order.getSendTime());
		String msg = order.getMessage();
		if (msg == null || msg.length() == 0){
			((TextView)findViewById(R.id.order_detail_message)).setText("нч");
		} else {
			((TextView)findViewById(R.id.order_detail_message)).setText(order.getMessage());
		}
		// set shop and food
		Iterator<Integer> shopIds = order.getShops();
		LinearLayout products = (LinearLayout)findViewById(R.id.order_detail_products);
		while (shopIds.hasNext()){
			LinearLayout main = (LinearLayout)getLayoutInflater().inflate(R.layout.product_main, null);
			TextView shopName = (TextView)main.findViewById(R.id.product_shop_name);
			int shopId = shopIds.next();
			shopName.setText(order.getShopName(shopId));
			Iterator<Food> foods = order.getFoods(shopId);
			while (foods.hasNext()){
				Food food = foods.next();
				RelativeLayout foodItem = (RelativeLayout)getLayoutInflater().inflate(R.layout.order_detail_food_item, null);
				TextView foodName = (TextView)foodItem.findViewById(R.id.product_food_item_name);
				TextView foodPrice = (TextView)foodItem.findViewById(R.id.product_food_item_price);
				TextView foodCount = (TextView)foodItem.findViewById(R.id.product_food_item_count);
				TextView foodItemId = (TextView)foodItem.findViewById(R.id.product_food_item_id);
				foodName.setText(food.getName());
				foodPrice.setText(String.valueOf(food.getPrice()));
				foodCount.setText(String.valueOf(food.getBuyCount()));
				foodItemId.setText(String.valueOf(food.getId()));
				main.addView(foodItem);
			}
			products.addView(main);
		}
		((Button)findViewById(R.id.back_btn)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				OrderDetailActivity.this.onBackPressed();
				return;
			}
		});
	}
}
