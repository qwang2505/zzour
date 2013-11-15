package com.zzour.android;

import java.util.Iterator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzour.android.base.BaseActivity;
import com.zzour.android.models.Food;
import com.zzour.android.models.ShoppingCart;

public class ShoppingDialActivity extends BaseActivity{
	
	private static final String TAG = "ZZOUR";
	
	private TextView totalPriceView;
	
	private int mCurrentShop = -1;
	private int mCurrentFood = -1;
	private int mCurrentCount = -1;
	private EditText mCurrentView = null;
	private String mPhoneNumber = null;
	
	private AlertDialog mNumberPicker;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shoping_dial);
		
		Intent intent = this.getIntent();
		mCurrentShop = intent.getIntExtra("shop_id", -1);
		if (mCurrentShop == -1){
			Toast.makeText(getApplicationContext(), "商店id不合法", Toast.LENGTH_SHORT).show();
			this.onBackPressed();
			return;
		}
		mPhoneNumber = intent.getStringExtra("shop_phone");
		
		((Button)findViewById(R.id.back_btn)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				ShoppingDialActivity.this.onBackPressed();
				return;
			}
		});
		
		show();
	}
	
	@Override
	public void onBackPressed(){
		// clear shopping cart
		ShoppingCart.clear();
		super.onBackPressed();
	}
	
	public void show(){
		LinearLayout products = (LinearLayout)findViewById(R.id.products);
		float totalPrice = 0.0f;
		String shopName = "";
		
		// get product view
		LinearLayout main = (LinearLayout)getLayoutInflater().inflate(R.layout.product_main, null);
		// get shop id from result
		int shopId = mCurrentShop;
		if (shopName.length() == 0){
			shopName = ShoppingCart.getShopName(shopId);
		}
		Iterator<Integer> foodIds = ShoppingCart.getFoods(shopId);
		while (foodIds.hasNext()){
			Food food = ShoppingCart.getFood(shopId, foodIds.next());
			RelativeLayout foodItem = (RelativeLayout)getLayoutInflater().inflate(R.layout.product_food_item, null);
			TextView foodName = (TextView)foodItem.findViewById(R.id.product_food_item_name);
			TextView foodPrice = (TextView)foodItem.findViewById(R.id.product_food_item_price);
			EditText foodCount = (EditText)foodItem.findViewById(R.id.product_food_item_count);
			TextView foodItemId = (TextView)foodItem.findViewById(R.id.product_food_item_id);
			ImageButton deleteBtn = (ImageButton)foodItem.findViewById(R.id.product_food_item_delete_btn);
			OnClickListener listener = new OnClickListener(){
				@Override
				public void onClick(View v) {
					// find id view, get shop and delete it.
					RelativeLayout parent = (RelativeLayout)v.getParent();
					String text = (String) ((TextView)parent.findViewById(R.id.product_food_item_id)).getText();
					Food food = ShoppingCart.getFoodByStringId(text);
					// update ui
					LinearLayout grandParent = (LinearLayout)parent.getParent();
					boolean shopEmpty = ShoppingCart.deleteFood(text);
					grandParent.removeView(parent);
					if (shopEmpty){
						LinearLayout grandgrandParent = (LinearLayout)grandParent.getParent();
						grandgrandParent.removeView(grandParent);
					}
					// reset total price and total box price.
					float oriTotalPrice = Float.valueOf(totalPriceView.getText().toString());
					oriTotalPrice -= food.getPrice() * food.getBuyCount();
					totalPriceView.setText(String.valueOf(oriTotalPrice));
				}
			};
			foodCount.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					RelativeLayout parent = (RelativeLayout)v.getParent();
					// get position and save
					TextView idView = (TextView)parent.findViewById(R.id.product_food_item_id);
					String text = idView.getText().toString();
					String[] ids = text.split(";");
					if (ids.length != 2){
						return;
					}
					mCurrentShop = Integer.valueOf(ids[0]);
					mCurrentFood = Integer.valueOf(ids[1]);
					Food food = ShoppingCart.getFoodByStringId(text);
					mCurrentCount = food.getBuyCount();
					mCurrentView = (EditText)v;
					// get current value
					showNumberPickerDialog(food.getBuyCount());
				}
				
			});
			deleteBtn.setOnClickListener(listener);
			foodName.setOnClickListener(listener);
			foodName.setText(food.getName());
			foodPrice.setText(String.valueOf(food.getPrice()));
			foodCount.setText(String.valueOf(food.getBuyCount()));
			foodItemId.setText(ShoppingCart.getStringId(shopId, food.getId()));
			main.addView(foodItem);
			totalPrice += food.getPrice() * food.getBuyCount();
		}
		products.addView(main);
	
		// set shop name
		((TextView)findViewById(R.id.shop_name)).setText(shopName);
		// reset total price and total box price.
		totalPriceView = (TextView)findViewById(R.id.total_price);
		totalPriceView.setText(String.valueOf(totalPrice));
		
		// dial button
		Button btn = (Button)findViewById(R.id.dial);
		btn.setText("拨打电话 " + mPhoneNumber);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO for the first time, give out tip
				// dial number
				try{
					Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + mPhoneNumber));
					startActivity(intent);
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "启动系统拨号错误，请重新尝试", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	public void showNumberPickerDialog(int number){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (mCurrentShop == -1 || mCurrentFood == -1 || mCurrentCount == -1 || mCurrentView == null){
					Log.e(TAG, "what happended?");
					return;
				}
				int count = Integer.valueOf(((TextView)mNumberPicker.findViewById(R.id.numpicker_input)).getText().toString());
				Food f = ShoppingCart.getFood(mCurrentShop, mCurrentFood);
				// calling api to set buy count
				f.setBuyCount(count);
				// update total money, total box money
				float price = Float.valueOf(totalPriceView.getText().toString());
				if (mCurrentCount == count){
					return;
				} else if (mCurrentCount > count){
					price -= f.getPrice() * (mCurrentCount - count);
				} else {
					price += f.getPrice() * (count - mCurrentCount);
				}
				totalPriceView.setText(String.valueOf(price));
				mCurrentView.setText(String.valueOf(count));
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mCurrentShop = -1;
				mCurrentFood = -1;
				return;
			}
		});
		View view = LayoutInflater.from(this).inflate(R.layout.number_picker_main, null);
		((TextView)view.findViewById(R.id.numpicker_input)).setText("" + number);
		builder.setView(view);
		mNumberPicker = builder.create();
		mNumberPicker.show();
	}
}